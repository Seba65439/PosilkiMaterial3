package pl.maifu.posilki.screens

import android.os.Build
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.maifu.posilki.Posilek
import pl.maifu.posilki.R
import pl.maifu.posilki.currentDate
import pl.maifu.posilki.index
import pl.maifu.posilki.menu
import pl.maifu.posilki.posilki
import pl.maifu.posilki.posilkiWorkDays
import pl.maifu.posilki.readFilterIndex
import pl.maifu.posilki.readFontSize
import pl.maifu.posilki.saveFilterIndex
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(openDrawer: () -> Unit) {
    var fontHeader by rememberSaveable { mutableIntStateOf(35) }
    var fontBody by rememberSaveable { mutableIntStateOf(25) }
    LaunchedEffect(true) {
        val font = readFontSize()
        fontHeader = 35 + font
        fontBody = 25 + font
    }
    val isWatch = Build.MODEL == "GLL-AL01"
    if (isWatch) {
        fontHeader = 15
        fontBody = 15

    }

    Scaffold(floatingActionButton = {
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
    }, topBar = {
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
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu button"
                    )
                }
                if (!isWatch) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                            .weight(6f)
                            .align(Alignment.CenterVertically)
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var filter by rememberSaveable {
                    mutableIntStateOf(readFilterIndex())
                }
                if (posilki.isEmpty()) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
                    Box(contentAlignment = Alignment.Center) {
                        LottieAnimation(
                            composition = composition, iterations = LottieConstants.IterateForever
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
                        FilterChip(
                            onClick = {
                                filter = it
                                saveFilterIndex(it)
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 1.dp),
                            chipModifier = Modifier.padding(horizontal = 4.dp),
                            filterIndex = filter
                        )
                        when (filter) {
                            0 -> LazyList(
                                posilki, fontHeader = fontHeader, fontBody = fontBody
                            )

                            1 -> LazyList(
                                posilkiWorkDays, fontHeader = fontHeader, fontBody = fontBody
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LazyList(list: List<Posilek>, fontHeader: Int, fontBody: Int) {
    val isWatch = Build.MODEL == "GLL-AL01"
    val state = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        contentPadding = PaddingValues(if (isWatch) 20.dp else 1.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    state.scrollBy(it.verticalScrollPixels)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable()
            .fillMaxHeight(1f),
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
            } else if (m.workday) LazyItem(
                m = m,
                fontBody = fontBody,
                fontHeader = fontHeader,
                color = MaterialTheme.colorScheme.primaryContainer,
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            else LazyItem(m = m, fontBody = fontBody, fontHeader = fontHeader)
        }
        CoroutineScope(Dispatchers.Main).launch {
            state.scrollToItem(index ?: 0)
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
    var expandedState by remember {
        mutableStateOf(m.state)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    Card(colors = CardDefaults.cardColors(
        containerColor = color,
    ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300, easing = LinearOutSlowInEasing
                )
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = { expandedState = !expandedState }) {
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
                    modifier = Modifier.padding(end = 40.dp),
                    lineHeight = fontBody.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    chipModifier: Modifier = Modifier,
    filterIndex: Int
) {

    val filterChips = remember {
        mutableStateListOf(
            ChipsState(
                isSelected = false, text = "Wszystko"
            ), ChipsState(
                isSelected = false, text = "Pracujące"
            )
        )
    }
    filterChips[filterIndex].isSelected = true
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        filterChips.forEachIndexed { index, info ->
            FilterChip(
                modifier = chipModifier,
                onClick = {
                    filterChips.replaceAll {
                        it.copy(
                            isSelected = it.text == info.text
                        )
                    }
                    onClick(index)
                },
                label = {
                    Text(info.text)
                },
                selected = info.isSelected,
                leadingIcon = if (info.isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
            )
        }
    }


}

data class ChipsState(
    var isSelected: Boolean, val text: String
)
