package pl.maifu.posilki.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.paperdb.Paper
import pl.maifu.posilki.MainActivity
import pl.maifu.posilki.Posilek
import pl.maifu.posilki.R
import pl.maifu.posilki.widget.callback.MealWidgetUpdateCallback
import pl.maifu.posilki.workday
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
                    meal.addAll(readed.subList(readed.indexOf(it), readed.lastIndex + 1))
                    break
                }
            }
            meal.workday()
        } catch (_: Exception) {
        }
        provideContent {
            ContentView(meal.filter { it.workday }.take(2))
        }


    }

    @Composable
    private fun ContentView(m: List<Posilek>) {
        val df = SimpleDateFormat("dd.MM.yy E", Locale.getDefault())

        Column(
            modifier = GlanceModifier.fillMaxSize().background(Color.DarkGray)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Image(
                    provider = ImageProvider(R.drawable.ic_refresh),
                    contentDescription = "Refresh",
                    modifier = GlanceModifier.clickable(actionRunCallback(MealWidgetUpdateCallback::class.java))
                        .padding(5.dp)

                )
            }

            if (m.isNotEmpty()) {
                Spacer(modifier = GlanceModifier.fillMaxWidth().height(1.dp).background(Color.Gray))
                m.forEach {
                    Text(
                        text = "${df.format(it.data)} ${it.opis}",
                        modifier = GlanceModifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            color = ColorProvider(Color.White),
                            fontSize = 13.sp
                        )
                    )
                    Spacer(
                        modifier = GlanceModifier.fillMaxWidth().height(1.dp).background(Color.Gray)
                    )
                }
            } else {
                Text(
                    text = "Otwórz aplikację aby skonfigurować",
                    modifier = GlanceModifier.padding(20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(Color.White),
                        fontSize = 15.sp
                    )
                )
            }
        }

    }
}