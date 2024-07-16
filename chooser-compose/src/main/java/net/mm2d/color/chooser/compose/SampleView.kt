package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.ColorSource.TEXT
import net.mm2d.color.chooser.compose.util.alphaBackgroundBrush

@Composable
internal fun SampleView(
    withAlpha: Boolean,
    colorEventState: MutableState<ColorEvent>,
    modifier: Modifier = Modifier,
) {
    val color = colorEventState.value.color
    Row(
        modifier = modifier,
    ) {
        Spacer(
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp)
                .background(Color(0x1a000000))
                .padding(1.dp)
                .background(Color.White)
                .padding(2.dp)
                .size(width = 64.dp, height = 24.dp)
                .background(alphaBackgroundBrush())
                .background(color),
        )
        var hasError by remember(color) { mutableStateOf(false) }
        val digit = if (withAlpha) 8 else 6
        var text by remember(color) {
            mutableStateOf(
                if (withAlpha) {
                    "%08X".format(color.toArgb())
                } else {
                    "%06X".format(color.toArgb() and 0xFFFFFF)
                }
            )
        }
        val borderColor =
            if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(24.dp + 4.dp * 2)
                .border(2.dp, borderColor),
        ) {
            BasicTextField(
                value = text,
                onValueChange = {
                    if (it.length > digit || it.contains("[^0-9a-fA-F]".toRegex())) {
                        return@BasicTextField
                    }
                    text = it
                    if (it.length == digit) {
                        val colorLong = it.toLongOrNull(16) ?: return@BasicTextField
                        colorEventState.value = ColorEvent(Color(colorLong), TEXT)
                        hasError = false
                    } else {
                        hasError = true
                    }
                },
                textStyle = TextStyle.Default.copy(
                    fontSize = LocalDensity.current.run { 12.dp.toSp() },
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
    }
}
