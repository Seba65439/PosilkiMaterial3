package pl.maifu.posilki.screens

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paperdb.Paper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.random.Random

@Composable
fun ImportExportScreen(openDrawer: () -> Unit) {
    val df = DateTimeFormatter.ofPattern("dd_MM_yy")
    val fileName = "Kopia_${LocalDate.now().format(df)}_${Random.nextInt(100000, 999999)}"
    val path = Paper.book().path
    val files = mutableListOf<File>()
    val keys = Paper.book().allKeys
    keys.forEach {
        files.add(File(Paper.book().getPath(it)))
    }
    val context = LocalContext.current as Activity
    val chooseFolderLauncher =
        rememberLauncherForActivityResult(CreateDocument("application/zip")) { uri ->
            if (uri != null) {
                val outputStream = context.contentResolver.openOutputStream(uri)
                outputStream?.use {
                    it.write(compressFiles(files))
                }
            }
        }
    val readFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use {
                    val array = readAllBytes(it)
                    unzipFromStream(array, path)
                }
                val packageManager: PackageManager = context.packageManager
                val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                val componentName: ComponentName = intent.component!!
                val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
                context.startActivity(restartIntent)
                Runtime.getRuntime().exit(0)
            }
        }
    Scaffold(topBar = {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = 6.dp, end = 6.dp)
            ) {
                IconButton(onClick = {
                    openDrawer()
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Menu, contentDescription = "Menu button"
                    )
                }
                Text(
                    text = "Kopia zapasowa",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        .weight(6f)
                        .align(Alignment.CenterVertically)
                )
            }
        }

    }) { padding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Button(onClick = {
                    try {
                        chooseFolderLauncher.launch(fileName)
                    } catch (_: Exception) {
                        Toast.makeText(context, "Coś poszło nie tak", Toast.LENGTH_LONG).show()
                    }

                }) {
                    Text("Zapisz kopię")
                }
                Spacer(modifier = Modifier.height(40.dp))
                Button(onClick = {
                    try {
                        readFileLauncher.launch(arrayOf("application/zip"))
                    } catch (_: Exception) {
                        Toast.makeText(context, "Coś poszło nie tak", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text("Przywróć kopię")
                }
            }
        }
    }
}


fun compressFiles(files: List<File>): ByteArray {
    val baos = ByteArrayOutputStream()
    val zos = ZipOutputStream(baos)
    for (file in files) {
        val entry = ZipEntry(file.name)
        zos.putNextEntry(entry)
        file.inputStream().use { fis ->
            fis.copyTo(zos)
        }
        zos.closeEntry()
    }
    zos.close()
    return baos.toByteArray()
}

fun unzipFromStream(zipStream: ByteArray, destDir: String) {
    val zipInput = ByteArrayInputStream(zipStream)
    val zis = ZipInputStream(zipInput)
    val buffer = ByteArray(1024)
    var len: Int
    var entry = zis.nextEntry
    while (entry != null) {
        val filePath = destDir + File.separator + entry.name
        if (entry.isDirectory) {
            val dir = File(filePath)
            dir.mkdir()
        } else {
            val parentDir = File(filePath).parentFile
            if (!parentDir!!.exists()) {
                parentDir.mkdirs()
            }
            val fileOutput = FileOutputStream(filePath, false)
            while (zis.read(buffer).also { len = it } > 0) {
                fileOutput.write(buffer, 0, len)
            }
            fileOutput.close()
        }
        entry = zis.nextEntry
    }
    zis.closeEntry()
    zis.close()
    zipInput.close()
}

fun readAllBytes(inputStream: InputStream): ByteArray {
    val baos = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    var len: Int
    while (inputStream.read(buffer).also { len = it } > 0) {
        baos.write(buffer, 0, len)
    }
    inputStream.close()
    baos.close()
    return baos.toByteArray()
}