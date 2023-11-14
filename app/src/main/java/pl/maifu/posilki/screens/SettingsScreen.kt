package pl.maifu.posilki.screens

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pl.maifu.posilki.posilki
import pl.maifu.posilki.readFirstDay
import pl.maifu.posilki.readFontSize
import pl.maifu.posilki.saveBrigade
import pl.maifu.posilki.saveFontSize
import pl.maifu.posilki.updatePosilkiWorkDays
import pl.maifu.posilki.workday
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val isWatch = Build.MODEL == "GLL-AL01"
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = if (isWatch) Arrangement.Center else Arrangement.Start
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back button"
                    )
                }
                if (!isWatch) {
                    Text(
                        text = "Ustawienia",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 30.sp
                    )
                }
            }
            val options = listOf("Brygada 1", "Brygada 2", "Brygada 3", "Brygada 4")
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(options[readFirstDay()]) }
            Text(
                text = "Wybierz brygade",
                fontSize = 25.sp,
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            ExposedDropdownMenuBox(modifier = Modifier.padding(10.dp),
                expanded = expanded,
                onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedOptionText,
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
                                saveBrigade(options.indexOf(selectionOption))
                                Log.d("Brigade", "Saved")
                                posilki.workday()
                                updatePosilkiWorkDays(posilki)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Text(
                text = "Rozmiar czcionki",
                fontSize = 25.sp,
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            var sliderPosition by remember { mutableFloatStateOf(readFontSize().toFloat()) }
            val interactionSource = remember { MutableInteractionSource() }
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    interactionSource = interactionSource,
                    onValueChangeFinished = {
                        saveFontSize(sliderPosition.roundToInt())
                    },
                    thumb = {
                        Column(
                            modifier = Modifier.padding(bottom = 35.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = sliderPosition.roundToInt().toString(),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary)
                            )
                        }
                    },
                    valueRange = -10f..20f
                )
            }

            Text(
                text = "Informacje",
                fontSize = 25.sp,
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            ) {
                val padding = Modifier.padding(start = 15.dp, end = 15.dp, top = 2.dp)
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Urządzenie: ${Build.MODEL}",
                    modifier = padding,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Text(
                    text = "Producent: ${Build.BRAND}",
                    modifier = padding,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Text(
                    text = "Android: ${Build.VERSION.RELEASE}",
                    modifier = padding,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Text(
                    text = "SDK: ${Build.VERSION.SDK_INT}",
                    modifier = padding,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.padding(10.dp))
            }
        }
    }
}