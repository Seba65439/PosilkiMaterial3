//package pl.maifu.posilki
//
//import android.content.Context
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.glance.GlanceId
//import androidx.glance.appwidget.GlanceAppWidget
//import androidx.glance.appwidget.provideContent
//import androidx.glance.layout.Column
//import androidx.glance.text.Text
//import io.paperdb.Paper
//import java.time.Instant
//import java.util.Date
//import java.util.concurrent.TimeUnit
//
//object MealWidget : GlanceAppWidget() {
//    override suspend fun provideGlance(context: Context, id: GlanceId) {
//        Paper.init(context)
//        val readed = Paper.book().read<SnapshotStateList<Posilek>>("menu")
//        try {
//            val workday = readed!!.filter { it.workday }
//            val data: Date = Date.from(Instant.now())
//            data.time = data.time - TimeUnit.HOURS.toMillis(22)
//            var index = 0
//            for (it in workday) {
//                if (it.data.after(data)) {
//                    index = workday.indexOf(it)
//                    break
//                }
//            }
//
//            val meal = workday.subList(index, index+1)
//            provideContent {
//                ContentView(meal)
//            }
//        } catch (e: Exception) {
//
//        }
//
//
//    }
//
//    @Composable
//    private fun ContentView(m: List<Posilek>) {
//        Column {
//            m.forEach{
//                Text(text = "${it.data.toString()} ${it.opis}")
//            }
//        }
//
//    }
//}