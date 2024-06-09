package net.mm2d.color.chooser.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.alpha
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorChooserView(
    colorState: MutableState<Int>,
    modifier: Modifier = Modifier,
) {
    val inputColor = colorState.value
    val alphaState = remember { mutableStateOf(inputColor.alpha) }
    val opacityState = remember { mutableStateOf(inputColor.toOpacity()) }
    val colorEventState = remember { mutableStateOf(ColorEvent(inputColor, ColorSource.INITIAL)) }

    val opacityEventState =
        remember { mutableStateOf(ColorEvent(inputColor.toOpacity(), ColorSource.INITIAL)) }

    LaunchedEffect(Unit) {
        snapshotFlow { opacityEventState.value }
            .distinctUntilChanged()
            .collect {
                opacityState.value = it.color
            }
    }
    LaunchedEffect(Unit) {
        combine(
            snapshotFlow { opacityEventState.value },
            snapshotFlow { alphaState.value },
            ::Pair,
        )
            .distinctUntilChanged()
            .collect { (opacityColorEvent, alpha) ->
                colorState.value = opacityColorEvent.color.setAlpha(alpha)
            }
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
                        opacityEventState = opacityEventState,
                        touchCapturing = touchCapturing,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                1 -> {
                    PaletteChooser(
                        opacityEventState = opacityEventState,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                2 -> {
                    RgbChooser(
                        opacityEventState = opacityEventState,
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
                opacityState = opacityState,
                alphaState = alphaState,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            var opacity by opacityState
            var alpha by alphaState
            val color = opacity.setAlpha(alpha)
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 8.dp)
                    .width(256.dp + 8.dp * 4 + 24.dp),
            ) {
                Box(
                    modifier = Modifier.size(width = 64.dp + 8.dp * 2, height = 24.dp + 8.dp * 2),
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color(0x1a000000))
                            .padding(1.dp)
                            .background(Color.White)
                            .padding(2.dp)
                            .size(width = 64.dp, height = 24.dp)
                            .background(
                                ShaderBrush(
                                    ImageShader(
                                        ImageBitmap.imageResource(id = R.drawable.mm2d_cc_bg_alpha),
                                        TileMode.Repeated,
                                        TileMode.Repeated,
                                    ),
                                ),
                            )
                            .background(Color(color)),
                    )
                }
                var hasError by remember { mutableStateOf(false) }
                var text by remember(color) { mutableStateOf("%08X".format(color)) }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .height(24.dp + 4.dp * 2)
                        .border(
                            2.dp,
                            if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        ),
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = {
                            if (it.length > 8 || it.contains("[^0-9a-fA-F]".toRegex())) {
                                return@BasicTextField
                            }
                            text = it
                            if (it.length == 6 || it.length == 8) {
                                val c = it.toIntOrNull(16) ?: return@BasicTextField
                                opacity = c.toOpacity()
                                alpha = if (it.length == 8) c.alpha else 0xff
                                hasError = false
                            } else {
                                hasError = true
                            }
                        },
                        textStyle = TextStyle.Default.copy(
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
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
