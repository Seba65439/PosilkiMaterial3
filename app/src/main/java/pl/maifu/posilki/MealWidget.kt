package pl.maifu.posilki

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object MealWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Paper.init(context)
        val readed = Paper.book().read<SnapshotStateList<Posilek>>("menu")
        val meal = mutableListOf<Posilek>()
        try {
            val data: Date = Date.from(Instant.now())
            data.time = data.time - TimeUnit.HOURS.toMillis(22)
            for (it in readed!!) {
                if (it.data.after(data)) {
                    meal.addAll(readed!!.subList(readed.indexOf(it), readed.lastIndex + 1))
                    break
                }
            }
        } catch (e: Exception) {
        }
        provideContent {
            ContentView(meal)
        }


    }

    @Composable
    private fun ContentView(m: List<Posilek>) {
        val df = SimpleDateFormat("dd.MM.yy E", Locale.getDefault())

        Column(modifier = GlanceModifier.fillMaxSize().background(Color.DarkGray)) {
            if (m.isNotEmpty()) {
                m.forEach {
                    Text(
                        text = "${df.format(it.data)}\n${it.opis}",
                        modifier = GlanceModifier.padding(horizontal = 20.dp, vertical = 2.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            color = ColorProvider(Color.White),
                            fontSize = 12.sp
                        )
                    )
                }
            } else {
                Text(text = "Otwórz aplikację aby skonfigurować",
                    modifier = GlanceModifier.padding(20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(Color.White),
                        fontSize = 15.sp)
                )
            }
        }

    }
}