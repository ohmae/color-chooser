package net.mm2d.color.chooser.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.alpha
import kotlinx.coroutines.launch
import net.mm2d.color.chooser.compose.ColorSource.INITIAL

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorChooserView(
    colorState: MutableState<Int>,
    modifier: Modifier = Modifier,
) {
    val inputColor = colorState.value
    val alphaState = remember { mutableStateOf(inputColor.alpha) }
    val opacityState = remember { mutableStateOf(inputColor.toOpacity()) }
    val colorDataState =
        remember { mutableStateOf(ColorData(opacityState.value, INITIAL)) }
    LaunchedEffect(Unit) {
        snapshotFlow { colorDataState.value.color.setAlpha(alphaState.value) }
            .collect { colorState.value = it }
    }

    Column(
        modifier = modifier,
    ) {
        val pagerState = rememberPagerState(pageCount = { 3 })
        val touchCapturing = remember { mutableStateOf(true) }
        PagerTab(
            titles = listOf("HSV", "PALETTE", "RGB"),
            pagerState = pagerState,
        )
        pagerState.currentPage
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = touchCapturing.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(256.dp + 32.dp),
        ) {
            when (it) {
                0 -> {
                    HsvChooser(
                        colorDataState = colorDataState,
                        touchCapturing = touchCapturing,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                1 -> {
                    PaletteChooser(
                        colorDataState = colorDataState,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                2 -> {
                    RgbChooser(
                        colorDataState = colorDataState,
                        touchCapturing = touchCapturing,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                else -> {
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            AlphaView(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 8.dp),
                colorState = opacityState,
                alphaState = alphaState,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerTab(
    titles: List<String>,
    pagerState: PagerState,
) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = Modifier.fillMaxWidth(),
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
            )
        },
    ) {
        titles.forEachIndexed { index, title ->
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { scope.launch { pagerState.animateScrollToPage(index) } }
                    .padding(8.dp),
            )
        }
    }
}
