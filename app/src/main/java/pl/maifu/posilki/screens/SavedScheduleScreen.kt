package pl.maifu.posilki.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.PlaylistRemove
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.maifu.posilki.data.Schedule
import pl.maifu.posilki.readFontSize
import pl.maifu.posilki.screens.composables.EditDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedScheduleScreen(openDrawer: () -> Unit) {
    val vm: ScheduleViewModel = viewModel()
    var fontSize by rememberSaveable { mutableIntStateOf(25) }
    val showDialog = remember { mutableStateOf(false) }
    val clickedElement = remember { mutableStateOf(Schedule(LocalDate.now(), 0, "", "", "")) }
    if (showDialog.value) {
        EditDay(
            onDismissRequest = { showDialog.value = false },
            onConfirmation = { optionSelected, note ->
                showDialog.value = false
                val toSave = clickedElement.value.copy(note = note, edited = optionSelected)
                vm.saveSchedule(toSave)
            },
            clickedElement = clickedElement.value
        )
    }
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
                    openDrawer()
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Menu, contentDescription = "Menu button"
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
                val itemsList =
                    vm.savedSchedule.groupBy { it.date.year }.toSortedMap(reverseOrder())
                if (itemsList.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                modifier = Modifier
                                    .padding(15.dp)
                                    .size(60.dp),
                                imageVector = Icons.Outlined.PlaylistRemove,
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                text = "Brak zapisanych pozycji",
                                textAlign = TextAlign.Center,
                                fontSize = fontSize.sp
                            )
                        }

                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsList.forEach { year ->
                        stickyHeader {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(start = 15.dp),
                                text = year.key.toString(),
                                fontSize = fontSize.sp
                            )
                        }
                        items(year.value.sortedByDescending { it.date }) {
                            ItemOfChanged(day = it, fontSize = fontSize, delete = { schedule ->
                                vm.deleteSchedule(schedule)
                            }, dialog = {
                                showDialog.value = true
                                clickedElement.value = it
                            }, holiday = vm.holidayName(it.date))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemOfChanged(
    day: Schedule,
    fontSize: Int,
    delete: (Schedule) -> Unit,
    dialog: () -> Unit,
    holiday: String
) {
    val context = LocalContext.current
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
            .fillMaxWidth()
            .combinedClickable(onClick = {
                Toast
                    .makeText(context, "Przytrzymaj dłużej aby edytować", Toast.LENGTH_LONG)
                    .show()
            }, onLongClick = {
                dialog()
            }),
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
                    .combinedClickable(onClick = {
                        Toast
                            .makeText(context, "Przytrzymaj dłużej aby usunąć", Toast.LENGTH_LONG)
                            .show()
                    }, onLongClick = {
                        delete(day)
                    })
                    .size(50.dp)
            )
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier.padding(start = 6.dp, top = 1.dp, bottom = 4.dp, end = 6.dp)
                ) {
                    val df = DateTimeFormatter.ofPattern("dd.MM.yy E")

                    Text(
                        text = day.date.format(df),
                        modifier = Modifier.weight(6f),
                        fontSize = fontSize.sp,
                        lineHeight = fontSize.sp
                    )
                    val bg = when (day.date.dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color(3, 173, 252).copy(alpha = 0.5f)
                        DayOfWeek.SUNDAY -> Color(255, 87, 87).copy(alpha = 0.5f)
                        else -> Color.Transparent
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent)
                            .clip(CircleShape)
                            .background(bg)
                    ) {
                        Text(
                            text = description,
                            fontSize = fontSize.sp,
                            lineHeight = fontSize.sp,
                        )
                    }
                }
                if (holiday.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(255, 87, 87).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = holiday,
                            fontSize = fontSize.sp,
                            lineHeight = fontSize.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
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

