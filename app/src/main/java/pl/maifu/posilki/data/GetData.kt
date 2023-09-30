package pl.maifu.posilki.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import pl.maifu.posilki.Constants
import pl.maifu.posilki.Posilek
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class GetData {
    private fun CoroutineScope.getLinkAsync(
        url: String,
        userAgent: String,
        timeout: Int = Constants.timeout
    ) = async {
        try {
            return@async Jsoup.connect(url)
                .method(Connection.Method.GET)
                .userAgent(userAgent)
                .timeout(timeout)
                .execute()
                .parse()
                .select("p a")
                .firstOrNull()
                ?.attr("href")
        } catch (e: Exception) {
            return@async null
        }
    }

    private fun CoroutineScope.downloadPdfAsync(downloadLink: String?, path: String) = async {
        if (downloadLink == "" || downloadLink == null) return@async false else
            try {
                withContext(Dispatchers.IO) {
                    URL(downloadLink).openStream()
                }.use { inp ->
                    BufferedInputStream(inp).use { bis ->
                        FileOutputStream(path).use { fos ->
                            val data = ByteArray(1024)
                            var count: Int
                            while (bis.read(data, 0, 1024).also { count = it } != -1) {
                                fos.write(data, 0, count)
                            }
                            return@async true
                        }
                    }
                }
            } catch (e: Exception) {
                return@async false
            }

    }

    private fun CoroutineScope.textFromPdfAsync(path: String) = async {
        try {
            val reader = PdfReader(path)
            val n = reader.numberOfPages
            var textPdf = ""
            for (i in 0 until n) {
                val newLine = PdfTextExtractor.getTextFromPage(reader, i + 1).trim { it <= ' ' }
                    .replace("\\s+".toRegex(), "\n")
                textPdf += "${newLine.lowercase(Locale.getDefault())}\n"
            }
            reader.close()
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }

            var lines = textPdf.lines()
            val dropIndex = lines.indexOf("nazwa")
            lines = lines.subList(dropIndex + 1, lines.lastIndex)
            Log.d("log.d", lines.toString())
            val meals = mutableListOf<Posilek>()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            var description = ""
            var tempDate = ""
            lines.forEachIndexed { index, string ->
                if (string.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                    if (index != 0) {
                        Log.d("log.d", "$tempDate $description")
                        meals.add(
                            Posilek(
                                dateFormat.parse(tempDate)!!,
                                description.split(" ").drop(2).joinToString(" ")
                            )
                        )
                        description = ""
                    }
                    tempDate = string
                } else {
                    description += " $string"
                }
            }
            Log.d("log.d", "$tempDate $description")
            meals.add(
                Posilek(
                    dateFormat.parse(tempDate)!!,
                    description.split(" ").drop(2).joinToString(" ")
                )
            )

            return@async meals
        } catch (e: Exception) {
            Log.d("log.d", e.message.toString())
            return@async null
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getMenu(): MutableList<Posilek> {
        val link = GlobalScope.getLinkAsync(Constants.URL, Constants.USER_AGENT).await()
        val download = GlobalScope.downloadPdfAsync(link, Constants.PATH).await()
        return if (download) {
            val txt = GlobalScope.textFromPdfAsync(Constants.PATH).await()
            if (txt.isNullOrEmpty()) {
                mutableStateListOf(Posilek(Date(1), "Błąd pobierania"))
            } else {
                txt
            }
        } else {
            mutableStateListOf(Posilek(Date(1), "Błąd pobierania"))
        }
    }
}
