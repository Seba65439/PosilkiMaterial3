package pl.maifu.posilki.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pl.maifu.posilki.Screens
import pl.maifu.posilki.data.Schedule
import pl.maifu.posilki.readFontSize
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun ScheduleScreen(navController: NavHostController) {
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
                    navController.popBackStack()
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back button"
                    )
                }
                Text(
                    text = "Grafik",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        .weight(6f)
                        .align(Alignment.CenterVertically)
                )
                IconButton(onClick = {
                    navController.navigate(Screens.SCHEDULELIST.route)
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Outlined.List, contentDescription = "List button"
                    )
                }
            }
        }

    }) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            val df = DateTimeFormatter.ofPattern("LLLL")
            Column {
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                ) {
                    val date = vm.date.collectAsState()
                    OutlinedButton(onClick = {
                        vm.minus()
                    }) {
                        Icon(imageVector = Icons.Outlined.ArrowLeft, contentDescription = null)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = date.value.format(df).toString(),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = date.value.year.toString(),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    OutlinedButton(onClick = {
                        vm.plus()
                    }) {
                        Icon(imageVector = Icons.Outlined.ArrowRight, contentDescription = null)
                    }
                }
                val itemsList = vm.calendarMonth.collectAsState()
                val offset = remember { mutableIntStateOf(0) }
                val firstDrag = remember { mutableStateOf(true) }
                LazyColumn(contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(offset.intValue, 0) }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(onHorizontalDrag = { _, dragAmount ->
                                if (firstDrag.value) {
                                    val offsetChange = dragAmount.roundToInt()
                                    offset.intValue += offsetChange
                                    if (offset.intValue > 200) {
                                        offset.intValue = 0
                                        firstDrag.value = false
                                        vm.minus()
                                    } else if (offset.intValue < -200) {
                                        offset.intValue = 0
                                        firstDrag.value = false
                                        vm.plus()
                                    }
                                }
                            }, onDragEnd = {
                                offset.intValue = 0
                                firstDrag.value = true
                            })
                        }) {
                    items(itemsList.value) {
                        Item(day = it, fontSize = fontSize, dialog = {
                            showDialog.value = true
                            clickedElement.value = it
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Item(day: Schedule, fontSize: Int, dialog: () -> Unit) {
    var color = when (day.workSchift) {
        1 -> MaterialTheme.colorScheme.tertiaryContainer
        2 -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    if (day.date.compareTo(LocalDate.now()) == 0) {
        color = MaterialTheme.colorScheme.tertiary
    }
    val description = if (day.edited != "") day.edited else day.type
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = {
                dialog()
            }),
        colors = CardDefaults.cardColors(
            containerColor = color,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
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

@Composable
fun EditDay(
    onDismissRequest: () -> Unit,
    onConfirmation: (optionSelected: String, note: String) -> Unit,
    clickedElement: Schedule,
) {
    val df = DateTimeFormatter.ofPattern("dd.MM.yy E")
    val optionSelected = remember {
        mutableStateOf(clickedElement.edited)
    }
    val note = remember {
        mutableStateOf(clickedElement.note)
    }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null
                )
                Text(
                    text = df.format(clickedElement.date),
                    modifier = Modifier.padding(8.dp),
                )
                Text(
                    text = "Grafikowo: ${clickedElement.type}",
                    fontSize = 15.sp,
                    modifier = Modifier.padding(5.dp),
                )
                Dropdown(selected = optionSelected.value, isSelected = {
                    optionSelected.value = it
                })
                OutlinedTextField(maxLines = 4, label = {
                    Text("Notatka")
                }, value = note.value, onValueChange = { note.value = it })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Anuluj")
                    }
                    OutlinedButton(
                        onClick = {
                            onConfirmation(
                                optionSelected.value, note.value
                            )
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Row {
                            Icon(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = "Zapisz"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(selected: String, isSelected: (String) -> Unit) {
    val options = listOf("1", "2", "-", "DW", "U", "CH")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(selected) }

    ExposedDropdownMenuBox(modifier = Modifier.padding(10.dp),
        expanded = expanded,
        onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOptionText,
            label = {
                Text(text = "Wybierz opcje")
            },
            onValueChange = {},
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        isSelected(selectionOption)

                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}