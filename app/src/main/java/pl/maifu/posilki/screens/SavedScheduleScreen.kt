package pl.maifu.posilki.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pl.maifu.posilki.data.Schedule
import pl.maifu.posilki.readFontSize
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

@Composable
fun SavedScheduleScreen(navController: NavHostController) {
    val vm: ScheduleViewModel = viewModel()
    var fontSize by rememberSaveable { mutableIntStateOf(25) }
    LaunchedEffect(true) {
        val font = readFontSize()
        fontSize = 25 + font
        vm.scheduleCalendar()
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
                    navController.popBackStack()
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back button"
                    )
                }
                Text(
                    text = "Lista",
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                val itemsList = vm.savedSchedule.sortedByDescending { it.date }
                LazyColumn(
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(itemsList) {
                        ItemOfChanged(day = it, fontSize = fontSize,
                            delete = { schedule ->
                                vm.deleteSchedule(schedule)
                            })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemOfChanged(day: Schedule, fontSize: Int, delete: (Schedule) -> Unit) {
    val color = when (day.workSchift) {
//        1 -> MaterialTheme.colorScheme.tertiaryContainer
//        2 -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val description = if (day.edited != "") day.edited else day.type
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            delete(day)
                        })
                    .size(50.dp)
            )
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.padding(start = 6.dp, top = 1.dp, bottom = 4.dp, end = 6.dp)
                ) {
                    val df = DateTimeFormatter.ofPattern("dd.MM.yy EEEE")

                    Text(
                        text = day.date.format(df),
                        fontSize = fontSize.sp,
                        modifier = Modifier.weight(6f),
                        lineHeight = fontSize.sp
                    )
                    Text(
                        text = description,
                        fontSize = fontSize.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 15.dp),
                        lineHeight = fontSize.sp
                    )
                    val bg = when (day.date.dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color.Blue.copy(alpha = 0.5f)
                        DayOfWeek.SUNDAY -> Color.Red.copy(alpha = 0.5f)
                        else -> Color.Transparent
                    }
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        imageVector = Icons.Filled.Circle,
                        contentDescription = null,
                        tint = bg
                    )
                }
                if (day.note != "") {
                    Text(
                        text = day.note,
                        fontSize = fontSize.sp,
                        modifier = Modifier.padding(start = 7.dp, end = 15.dp, bottom = 3.dp),
                        lineHeight = fontSize.sp
                    )
                }
            }
        }


    }
}