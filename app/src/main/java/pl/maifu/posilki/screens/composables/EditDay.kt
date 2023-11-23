package pl.maifu.posilki.screens.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pl.maifu.posilki.data.Schedule
import java.time.format.DateTimeFormatter

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