package net.mm2d.color.chooser.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import net.mm2d.color.chooser.Tab

/**
 * ColorChooserView provides a color chooser.
 *
 * @param colorState The state of the color to be displayed.
 * @param modifier The modifier to be applied to the layout.
 * @param withAlpha Whether to show the alpha control.
 * @param initialTab The initial tab to be displayed. Default is [Tab.PALETTE]. See [Tab].
 * @param tabs The tabs to be displayed. Default is [Tab.PALETTE], [Tab.HSV], [Tab.RGB]. See [Tab].
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorChooserView(
    colorState: MutableState<Color>,
    modifier: Modifier = Modifier,
    withAlpha: Boolean = true,
    initialTab: Tab = Tab.DEFAULT_TAB,
    tabs: List<Tab> = Tab.DEFAULT_TABS,
) {
    val inputColor = colorState.value
    val colorEventState = remember { mutableStateOf(ColorEvent(inputColor, ColorSource.INITIAL)) }

    LaunchedEffect(Unit) {
        snapshotFlow { colorEventState.value }
            .distinctUntilChanged()
            .collect {
                colorState.value = it.color
            }
    }

    Column(
        modifier = modifier,
    ) {
        val pagerState = rememberPagerState(
            initialPage = tabs.indexOf(initialTab).coerceAtLeast(0),
            pageCount = { tabs.size },
        )
        val touchCapturing = remember { mutableStateOf(false) }
        PagerTab(
            titles = tabs.map { it.name },
            pagerState = pagerState,
        )
        pagerState.currentPage
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = !touchCapturing.value,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .defaultMinSize(minHeight = 255.dp + 32.dp),
        ) {
            val tab = tabs.getOrNull(it) ?: return@HorizontalPager
            when (tab) {
                Tab.PALETTE -> PaletteChooser(
                    colorEventState = colorEventState,
                    modifier = Modifier.fillMaxSize(),
                )

                Tab.HSV -> HsvChooser(
                    colorEventState = colorEventState,
                    touchCapturing = touchCapturing,
                    modifier = Modifier.fillMaxSize(),
                )

                Tab.RGB -> RgbChooser(
                    colorEventState = colorEventState,
                    touchCapturing = touchCapturing,
                    modifier = Modifier.fillMaxSize(),
                )

                Tab.M3 -> Material3Chooser(
                    colorEventState = colorEventState,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        if (withAlpha) {
            AlphaView(
                colorEventState = colorEventState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally),
            )
        }

        SampleView(
            withAlpha = withAlpha,
            colorEventState = colorEventState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, end = 16.dp),
        )
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
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier
                    .clickable { scope.launch { pagerState.animateScrollToPage(index) } }
                    .padding(vertical = 16.dp),
            )
        }
    }
}
