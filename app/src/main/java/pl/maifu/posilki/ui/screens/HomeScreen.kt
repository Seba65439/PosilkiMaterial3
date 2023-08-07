package pl.maifu.posilki.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.maifu.posilki.Posilek
import pl.maifu.posilki.R
import pl.maifu.posilki.currentDate
import pl.maifu.posilki.index
import pl.maifu.posilki.menu
import pl.maifu.posilki.posilki
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(onClick: (String) -> Unit) {

    var fontHeader by rememberSaveable { mutableStateOf(35) }
    var fontBody by rememberSaveable { mutableStateOf(25) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    menu(false)
                },
                modifier = Modifier.size(50.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(6f)
                            .align(Alignment.CenterVertically)
                    )
                    IconButton(onClick = {
                        onClick("info")
                    }, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info button"
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
            val progressValue = 1f
            val infiniteTransition = rememberInfiniteTransition(label = "")

            val progressAnimationValue by infiniteTransition.animateFloat(
                initialValue = 0.0f,
                targetValue = progressValue,
                animationSpec = infiniteRepeatable(animation = tween(900)), label = ""
            )
            if (posilki.isEmpty()) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = progressAnimationValue,
                        strokeWidth = 5.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }


            } else {
                if (posilki.size == 1) {
                    Toast.makeText(
                        LocalContext.current,
                        "Coś poszło nie tak, spróbuj jeszcze raz",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    LazyList(posilki, fontHeader = fontHeader, fontBody = fontBody)
                }
            }

        }

    }

}

@Composable
fun LazyList(list: List<Posilek>, fontHeader: Int, fontBody: Int) {
    val state = rememberLazyListState()
    LazyColumn(
        contentPadding = PaddingValues(1.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        state = state,
    ) {
        items(list) { m ->
            if (currentDate == m.data) {
                LazyItem(
                    m = m,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    fontBody = fontBody,
                    fontHeader = fontHeader,
                    textColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            } else if (m.workday)
                LazyItem(
                    m = m,
                    fontBody = fontBody,
                    fontHeader = fontHeader,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            else
                LazyItem(m = m, fontBody = fontBody, fontHeader = fontHeader)
        }
        CoroutineScope(Dispatchers.Main).launch {
            state.scrollToItem(index ?: 0)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyItem(
    m: Posilek,
    color: Color = MaterialTheme.colorScheme.surface,
    fontHeader: Int,
    fontBody: Int,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val df = SimpleDateFormat("dd.MM.yy E", Locale.getDefault())
    var expandedState by rememberSaveable {
        mutableStateOf(m.state)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f,
        label = ""
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color,
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = { expandedState = !expandedState }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.padding(start = 6.dp, top = 1.dp, bottom = 4.dp, end = 6.dp)
        )

        {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = df.format(m.data),
                    color = textColor,
                    fontSize = fontHeader.sp,
                    modifier = Modifier.weight(6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(modifier = Modifier
                    .alpha(0.5F)
                    .weight(1f)
                    .rotate(rotationState),
                    onClick = { expandedState = !expandedState }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Rozwiń")
                }
            }
            if (expandedState) {
                Text(
                    text = m.opis,
                    color = textColor,
                    fontSize = fontBody.sp,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 33.sp,
                    modifier = Modifier.padding(end = 40.dp)
                )
            }
        }
    }
}
