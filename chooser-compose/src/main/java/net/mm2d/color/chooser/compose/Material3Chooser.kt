package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun Material3Chooser(
    colorEventState: MutableState<ColorEvent>,
    modifier: Modifier = Modifier,
) {
    var colorEvent by colorEventState
    val items = makeMaterialItems(MaterialTheme.colorScheme)
    LazyColumn(
        modifier = modifier,
    ) {
        items(items) { list ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                list.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(item.color)
                            .clickable {
                                colorEvent = ColorEvent(
                                    item.color.copy(colorEvent.color.alpha),
                                    ColorSource.M3,
                                )
                            },
                    ) {
                        Text(
                            text = item.name,
                            color = item.textColor,
                            fontSize = 10.sp,
                        )
                    }
                }
            }
        }
    }
}

private data class MaterialItem(
    val name: String = "",
    val color: Color = Color.Unspecified,
    val textColor: Color = Color.Unspecified,
)

private fun makeMaterialItems(colorScheme: ColorScheme): List<List<MaterialItem>> =
    listOf(
        listOf(
            MaterialItem(
                name = "primary",
                color = colorScheme.primary,
                textColor = colorScheme.onPrimary,
            ),
            MaterialItem(
                name = "onPrimary",
                color = colorScheme.onPrimary,
                textColor = colorScheme.primary,
            ),
        ),
        listOf(
            MaterialItem(
                name = "primaryContainer",
                color = colorScheme.primaryContainer,
                textColor = colorScheme.onPrimaryContainer,
            ),
            MaterialItem(
                name = "onPrimaryContainer",
                color = colorScheme.onPrimaryContainer,
                textColor = colorScheme.primaryContainer,
            ),
        ),
        listOf(
            MaterialItem(
                name = "secondary",
                color = colorScheme.secondary,
                textColor = colorScheme.onSecondary,
            ),
            MaterialItem(
                name = "onSecondary",
                color = colorScheme.onSecondary,
                textColor = colorScheme.secondary,
            ),
        ),
        listOf(
            MaterialItem(
                name = "secondaryContainer",
                color = colorScheme.secondaryContainer,
                textColor = colorScheme.onSecondaryContainer,
            ),
            MaterialItem(
                name = "onSecondaryContainer",
                color = colorScheme.onSecondaryContainer,
                textColor = colorScheme.secondaryContainer,
            ),
        ),
        listOf(
            MaterialItem(
                name = "tertiary",
                color = colorScheme.tertiary,
                textColor = colorScheme.onTertiary,
            ),
            MaterialItem(
                name = "onTertiary",
                color = colorScheme.onTertiary,
                textColor = colorScheme.tertiary,
            ),
        ),
        listOf(
            MaterialItem(
                name = "tertiaryContainer",
                color = colorScheme.tertiaryContainer,
                textColor = colorScheme.onTertiaryContainer,
            ),
            MaterialItem(
                name = "onTertiaryContainer",
                color = colorScheme.onTertiaryContainer,
                textColor = colorScheme.tertiaryContainer,
            ),
        ),
        listOf(
            MaterialItem(
                name = "background",
                color = colorScheme.background,
                textColor = colorScheme.onBackground,
            ),
            MaterialItem(
                name = "onBackground",
                color = colorScheme.onBackground,
                textColor = colorScheme.background,
            ),
        ),
        listOf(
            MaterialItem(
                name = "surface",
                color = colorScheme.surface,
                textColor = colorScheme.onSurface,
            ),
            MaterialItem(
                name = "onSurface",
                color = colorScheme.onSurface,
                textColor = colorScheme.surface,
            ),
        ),
        listOf(
            MaterialItem(
                name = "surfaceVariant",
                color = colorScheme.surfaceVariant,
                textColor = colorScheme.onSurfaceVariant,
            ),
            MaterialItem(
                name = "onSurfaceVariant",
                color = colorScheme.onSurfaceVariant,
                textColor = colorScheme.surfaceVariant,
            ),
        ),
        listOf(
            MaterialItem(
                name = "surfaceTint",
                color = colorScheme.surfaceTint,
                textColor = colorScheme.surface,
            ),
            MaterialItem(
                name = "inversePrimary",
                color = colorScheme.inversePrimary,
                textColor = colorScheme.primary,
            ),
        ),
        listOf(
            MaterialItem(
                name = "inverseSurface",
                color = colorScheme.inverseSurface,
                textColor = colorScheme.inverseOnSurface,
            ),
            MaterialItem(
                name = "inverseOnSurface",
                color = colorScheme.inverseOnSurface,
                textColor = colorScheme.inverseSurface,
            ),
        ),
        listOf(
            MaterialItem(
                name = "error",
                color = colorScheme.error,
                textColor = colorScheme.onError,
            ),
            MaterialItem(
                name = "onError",
                color = colorScheme.onError,
                textColor = colorScheme.error,
            ),
        ),
        listOf(
            MaterialItem(
                name = "errorContainer",
                color = colorScheme.errorContainer,
                textColor = colorScheme.onErrorContainer,
            ),
            MaterialItem(
                name = "onErrorContainer",
                color = colorScheme.onErrorContainer,
                textColor = colorScheme.errorContainer,
            ),
        ),
        listOf(
            MaterialItem(
                name = "outline",
                color = colorScheme.outline,
                textColor = colorScheme.outlineVariant,
            ),
            MaterialItem(
                name = "outlineVariant",
                color = colorScheme.outlineVariant,
                textColor = colorScheme.outline,
            ),
        ),
        listOf(
            MaterialItem(
                name = "surfaceBright",
                color = colorScheme.surfaceBright,
                textColor = colorScheme.onSurface,
            ),
            MaterialItem(
                name = "surfaceDim",
                color = colorScheme.surfaceDim,
                textColor = colorScheme.onSurface,
            ),
        ),
        listOf(
            MaterialItem(
                name = "surfaceContainer",
                color = colorScheme.surfaceContainer,
                textColor = colorScheme.onSurface,
            ),
            MaterialItem(
                name = "surfaceContainerHigh",
                color = colorScheme.surfaceContainerHigh,
                textColor = colorScheme.onSurface,
            ),
        ),
        listOf(
            MaterialItem(
                name = "surfaceContainerHighest",
                color = colorScheme.surfaceContainerHighest,
                textColor = colorScheme.onSurface,
            ),
            MaterialItem(
                name = "surfaceContainerLow",
                color = colorScheme.surfaceContainerLow,
                textColor = colorScheme.onSurface,
            ),
        ),
        listOf(
            MaterialItem(
                name = "surfaceContainerLowest",
                color = colorScheme.surfaceContainerLowest,
                textColor = colorScheme.onSurface,
            ),
            MaterialItem(
                name = "scrim",
                color = colorScheme.scrim,
                textColor = colorScheme.surface,
            ),
        ),
    )
