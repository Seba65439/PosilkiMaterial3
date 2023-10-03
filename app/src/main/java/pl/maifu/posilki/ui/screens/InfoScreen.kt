package pl.maifu.posilki.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                        text = "Informacje",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 30.sp
                    )
                }

            }

            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = "Urządzenie: ${Build.MODEL}",
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Text(
                    text = "Producent: ${Build.BRAND}",
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Text(
                    text = "Android: ${Build.VERSION.RELEASE}",
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Text(
                    text = "Autor: maifu",
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
            }


        }


    }
}