package pl.maifu.posilki.ui.screens

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.maifu.posilki.posilki
import pl.maifu.posilki.readFirstDay
import pl.maifu.posilki.saveBrigade
import pl.maifu.posilki.workday

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(onClick: (String) -> Unit) {
    val isWatch = Build.MODEL == "GLL-AL01"
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = if (isWatch) Arrangement.Center else Arrangement.Start
            ) {
                IconButton(onClick = {
                    onClick("home")
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
                TextField(
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = {},
                    label = { Text("Brygada") },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    modifier = Modifier.menuAnchor()
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
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
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
                    .verticalScroll(rememberScrollState())
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