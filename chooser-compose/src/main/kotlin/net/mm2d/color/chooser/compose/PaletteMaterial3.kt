/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color

internal fun paletteMaterial3(
    context: Context,
): List<List<Color>> =
    buildList {
        add(
            listOf(
                Color(red = 255, green = 251, blue = 254),
                Color(red = 245, green = 238, blue = 250),
                Color(red = 231, green = 224, blue = 236),
                Color(red = 202, green = 196, blue = 208),
                Color(red = 174, green = 169, blue = 180),
                Color(red = 147, green = 143, blue = 153),
                Color(red = 121, green = 116, blue = 126),
                Color(red = 96, green = 93, blue = 102),
                Color(red = 73, green = 69, blue = 79),
                Color(red = 50, green = 47, blue = 55),
                Color(red = 29, green = 26, blue = 34),
            ),
        )
        add(
            listOf(
                Color(red = 255, green = 251, blue = 254),
                Color(red = 246, green = 237, blue = 255),
                Color(red = 234, green = 221, blue = 255),
                Color(red = 208, green = 188, blue = 255),
                Color(red = 182, green = 157, blue = 248),
                Color(red = 154, green = 130, blue = 219),
                Color(red = 127, green = 103, blue = 190),
                Color(red = 103, green = 80, blue = 164),
                Color(red = 79, green = 55, blue = 139),
                Color(red = 56, green = 30, blue = 114),
                Color(red = 33, green = 0, blue = 93),
            ),
        )
        add(
            listOf(
                Color(red = 255, green = 251, blue = 254),
                Color(red = 246, green = 237, blue = 255),
                Color(red = 232, green = 222, blue = 248),
                Color(red = 204, green = 194, blue = 220),
                Color(red = 176, green = 167, blue = 192),
                Color(red = 149, green = 141, blue = 165),
                Color(red = 122, green = 114, blue = 137),
                Color(red = 98, green = 91, blue = 113),
                Color(red = 74, green = 68, blue = 88),
                Color(red = 51, green = 45, blue = 65),
                Color(red = 29, green = 25, blue = 43),
            ),
        )
        add(
            listOf(
                Color(red = 255, green = 251, blue = 250),
                Color(red = 255, green = 236, blue = 241),
                Color(red = 255, green = 216, blue = 228),
                Color(red = 239, green = 184, blue = 200),
                Color(red = 210, green = 157, blue = 172),
                Color(red = 181, green = 131, blue = 146),
                Color(red = 152, green = 105, blue = 119),
                Color(red = 125, green = 82, blue = 96),
                Color(red = 99, green = 59, blue = 72),
                Color(red = 73, green = 37, blue = 50),
                Color(red = 49, green = 17, blue = 29),
            ),
        )
        add(
            listOf(
                Color(red = 255, green = 251, blue = 249),
                Color(red = 252, green = 238, blue = 238),
                Color(red = 249, green = 222, blue = 220),
                Color(red = 242, green = 184, blue = 181),
                Color(red = 236, green = 146, blue = 142),
                Color(red = 228, green = 105, blue = 98),
                Color(red = 220, green = 54, blue = 46),
                Color(red = 179, green = 38, blue = 30),
                Color(red = 140, green = 29, blue = 24),
                Color(red = 96, green = 20, blue = 16),
                Color(red = 65, green = 14, blue = 11),
            ),
        )
        add(
            listOf(
                Color(red = 255, green = 251, blue = 255),
                Color(red = 254, green = 247, blue = 255),
                Color(red = 247, green = 242, blue = 250),
                Color(red = 245, green = 239, blue = 247),
                Color(red = 243, green = 237, blue = 247),
                Color(red = 236, green = 230, blue = 240),
                Color(red = 230, green = 224, blue = 233),
                Color(red = 222, green = 216, blue = 225),
                Color(red = 202, green = 197, blue = 205),
                Color(red = 174, green = 169, blue = 177),
                Color(red = 147, green = 143, blue = 150),
            ),
        )
        add(
            listOf(
                Color(red = 121, green = 118, blue = 125),
                Color(red = 96, green = 93, blue = 100),
                Color(red = 72, green = 70, blue = 76),
                Color(red = 59, green = 56, blue = 62),
                Color(red = 54, green = 52, blue = 59),
                Color(red = 50, green = 47, blue = 53),
                Color(red = 43, green = 41, blue = 48),
                Color(red = 33, green = 31, blue = 38),
                Color(red = 29, green = 27, blue = 32),
                Color(red = 20, green = 18, blue = 24),
                Color(red = 15, green = 13, blue = 19),
            ),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_accent1_10)),
                    Color(context.getColor(android.R.color.system_accent1_50)),
                    Color(context.getColor(android.R.color.system_accent1_100)),
                    Color(context.getColor(android.R.color.system_accent1_200)),
                    Color(context.getColor(android.R.color.system_accent1_300)),
                    Color(context.getColor(android.R.color.system_accent1_400)),
                    Color(context.getColor(android.R.color.system_accent1_500)),
                    Color(context.getColor(android.R.color.system_accent1_600)),
                    Color(context.getColor(android.R.color.system_accent1_700)),
                    Color(context.getColor(android.R.color.system_accent1_800)),
                    Color(context.getColor(android.R.color.system_accent1_900)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_accent2_10)),
                    Color(context.getColor(android.R.color.system_accent2_50)),
                    Color(context.getColor(android.R.color.system_accent2_100)),
                    Color(context.getColor(android.R.color.system_accent2_200)),
                    Color(context.getColor(android.R.color.system_accent2_300)),
                    Color(context.getColor(android.R.color.system_accent2_400)),
                    Color(context.getColor(android.R.color.system_accent2_500)),
                    Color(context.getColor(android.R.color.system_accent2_600)),
                    Color(context.getColor(android.R.color.system_accent2_700)),
                    Color(context.getColor(android.R.color.system_accent2_800)),
                    Color(context.getColor(android.R.color.system_accent2_900)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_accent3_10)),
                    Color(context.getColor(android.R.color.system_accent3_50)),
                    Color(context.getColor(android.R.color.system_accent3_100)),
                    Color(context.getColor(android.R.color.system_accent3_200)),
                    Color(context.getColor(android.R.color.system_accent3_300)),
                    Color(context.getColor(android.R.color.system_accent3_400)),
                    Color(context.getColor(android.R.color.system_accent3_500)),
                    Color(context.getColor(android.R.color.system_accent3_600)),
                    Color(context.getColor(android.R.color.system_accent3_700)),
                    Color(context.getColor(android.R.color.system_accent3_800)),
                    Color(context.getColor(android.R.color.system_accent3_900)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_neutral1_10)),
                    Color(context.getColor(android.R.color.system_neutral1_50)),
                    Color(context.getColor(android.R.color.system_neutral1_100)),
                    Color(context.getColor(android.R.color.system_neutral1_200)),
                    Color(context.getColor(android.R.color.system_neutral1_300)),
                    Color(context.getColor(android.R.color.system_neutral1_400)),
                    Color(context.getColor(android.R.color.system_neutral1_500)),
                    Color(context.getColor(android.R.color.system_neutral1_600)),
                    Color(context.getColor(android.R.color.system_neutral1_700)),
                    Color(context.getColor(android.R.color.system_neutral1_800)),
                    Color(context.getColor(android.R.color.system_neutral1_900)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_neutral2_10)),
                    Color(context.getColor(android.R.color.system_neutral2_50)),
                    Color(context.getColor(android.R.color.system_neutral2_100)),
                    Color(context.getColor(android.R.color.system_neutral2_200)),
                    Color(context.getColor(android.R.color.system_neutral2_300)),
                    Color(context.getColor(android.R.color.system_neutral2_400)),
                    Color(context.getColor(android.R.color.system_neutral2_500)),
                    Color(context.getColor(android.R.color.system_neutral2_600)),
                    Color(context.getColor(android.R.color.system_neutral2_700)),
                    Color(context.getColor(android.R.color.system_neutral2_800)),
                    Color(context.getColor(android.R.color.system_neutral2_900)),
                ),
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_error_10)),
                    Color(context.getColor(android.R.color.system_error_50)),
                    Color(context.getColor(android.R.color.system_error_100)),
                    Color(context.getColor(android.R.color.system_error_200)),
                    Color(context.getColor(android.R.color.system_error_300)),
                    Color(context.getColor(android.R.color.system_error_400)),
                    Color(context.getColor(android.R.color.system_error_500)),
                    Color(context.getColor(android.R.color.system_error_600)),
                    Color(context.getColor(android.R.color.system_error_700)),
                    Color(context.getColor(android.R.color.system_error_800)),
                    Color(context.getColor(android.R.color.system_error_900)),
                ),
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_primary_container_light)),
                    Color(context.getColor(android.R.color.system_on_primary_container_light)),
                    Color(context.getColor(android.R.color.system_primary_light)),
                    Color(context.getColor(android.R.color.system_on_primary_light)),
                    Color(context.getColor(android.R.color.system_secondary_container_light)),
                    Color(context.getColor(android.R.color.system_on_secondary_container_light)),
                    Color(context.getColor(android.R.color.system_secondary_light)),
                    Color(context.getColor(android.R.color.system_on_secondary_light)),
                    Color(context.getColor(android.R.color.system_tertiary_container_light)),
                    Color(context.getColor(android.R.color.system_on_tertiary_container_light)),
                    Color(context.getColor(android.R.color.system_tertiary_light)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_on_tertiary_light)),
                    Color(context.getColor(android.R.color.system_background_light)),
                    Color(context.getColor(android.R.color.system_on_background_light)),
                    Color(context.getColor(android.R.color.system_surface_light)),
                    Color(context.getColor(android.R.color.system_on_surface_light)),
                    Color(context.getColor(android.R.color.system_surface_container_low_light)),
                    Color(context.getColor(android.R.color.system_surface_container_lowest_light)),
                    Color(context.getColor(android.R.color.system_surface_container_light)),
                    Color(context.getColor(android.R.color.system_surface_container_high_light)),
                    Color(context.getColor(android.R.color.system_surface_container_highest_light)),
                    Color(context.getColor(android.R.color.system_surface_bright_light)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_surface_dim_light)),
                    Color(context.getColor(android.R.color.system_surface_variant_light)),
                    Color(context.getColor(android.R.color.system_on_surface_variant_light)),
                    Color(context.getColor(android.R.color.system_outline_light)),
                    Color(context.getColor(android.R.color.system_outline_variant_light)),
                    Color(context.getColor(android.R.color.system_error_light)),
                    Color(context.getColor(android.R.color.system_on_error_light)),
                    Color(context.getColor(android.R.color.system_error_container_light)),
                    Color(context.getColor(android.R.color.system_on_error_container_light)),
                    Color(context.getColor(android.R.color.system_control_activated_light)),
                    Color(context.getColor(android.R.color.system_control_normal_light)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_control_highlight_light)),
                    Color(context.getColor(android.R.color.system_text_primary_inverse_light)),
                    Color(context.getColor(android.R.color.system_text_secondary_and_tertiary_inverse_light)),
                    Color(context.getColor(android.R.color.system_text_primary_inverse_disable_only_light)),
                    Color(context.getColor(android.R.color.system_text_secondary_and_tertiary_inverse_disabled_light)),
                    Color(context.getColor(android.R.color.system_text_hint_inverse_light)),
                    Color(context.getColor(android.R.color.system_palette_key_color_primary_light)),
                    Color(context.getColor(android.R.color.system_palette_key_color_secondary_light)),
                    Color(context.getColor(android.R.color.system_palette_key_color_tertiary_light)),
                    Color(context.getColor(android.R.color.system_palette_key_color_neutral_light)),
                    Color(context.getColor(android.R.color.system_palette_key_color_neutral_variant_light)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_primary_container_dark)),
                    Color(context.getColor(android.R.color.system_on_primary_container_dark)),
                    Color(context.getColor(android.R.color.system_primary_dark)),
                    Color(context.getColor(android.R.color.system_on_primary_dark)),
                    Color(context.getColor(android.R.color.system_secondary_container_dark)),
                    Color(context.getColor(android.R.color.system_on_secondary_container_dark)),
                    Color(context.getColor(android.R.color.system_secondary_dark)),
                    Color(context.getColor(android.R.color.system_on_secondary_dark)),
                    Color(context.getColor(android.R.color.system_tertiary_container_dark)),
                    Color(context.getColor(android.R.color.system_on_tertiary_container_dark)),
                    Color(context.getColor(android.R.color.system_tertiary_dark)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_on_tertiary_dark)),
                    Color(context.getColor(android.R.color.system_background_dark)),
                    Color(context.getColor(android.R.color.system_on_background_dark)),
                    Color(context.getColor(android.R.color.system_surface_dark)),
                    Color(context.getColor(android.R.color.system_on_surface_dark)),
                    Color(context.getColor(android.R.color.system_surface_container_low_dark)),
                    Color(context.getColor(android.R.color.system_surface_container_lowest_dark)),
                    Color(context.getColor(android.R.color.system_surface_container_dark)),
                    Color(context.getColor(android.R.color.system_surface_container_high_dark)),
                    Color(context.getColor(android.R.color.system_surface_container_highest_dark)),
                    Color(context.getColor(android.R.color.system_surface_bright_dark)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_surface_dim_dark)),
                    Color(context.getColor(android.R.color.system_surface_variant_dark)),
                    Color(context.getColor(android.R.color.system_on_surface_variant_dark)),
                    Color(context.getColor(android.R.color.system_outline_dark)),
                    Color(context.getColor(android.R.color.system_outline_variant_dark)),
                    Color(context.getColor(android.R.color.system_error_dark)),
                    Color(context.getColor(android.R.color.system_on_error_dark)),
                    Color(context.getColor(android.R.color.system_error_container_dark)),
                    Color(context.getColor(android.R.color.system_on_error_container_dark)),
                    Color(context.getColor(android.R.color.system_control_activated_dark)),
                    Color(context.getColor(android.R.color.system_control_normal_dark)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_control_highlight_dark)),
                    Color(context.getColor(android.R.color.system_text_primary_inverse_dark)),
                    Color(context.getColor(android.R.color.system_text_secondary_and_tertiary_inverse_dark)),
                    Color(context.getColor(android.R.color.system_text_primary_inverse_disable_only_dark)),
                    Color(context.getColor(android.R.color.system_text_secondary_and_tertiary_inverse_disabled_dark)),
                    Color(context.getColor(android.R.color.system_text_hint_inverse_dark)),
                    Color(context.getColor(android.R.color.system_palette_key_color_primary_dark)),
                    Color(context.getColor(android.R.color.system_palette_key_color_secondary_dark)),
                    Color(context.getColor(android.R.color.system_palette_key_color_tertiary_dark)),
                    Color(context.getColor(android.R.color.system_palette_key_color_neutral_dark)),
                    Color(context.getColor(android.R.color.system_palette_key_color_neutral_variant_dark)),
                ),
            )
            add(
                listOf(
                    Color(context.getColor(android.R.color.system_primary_fixed)),
                    Color(context.getColor(android.R.color.system_primary_fixed_dim)),
                    Color(context.getColor(android.R.color.system_on_primary_fixed)),
                    Color(context.getColor(android.R.color.system_on_primary_fixed_variant)),
                    Color(context.getColor(android.R.color.system_secondary_fixed)),
                    Color(context.getColor(android.R.color.system_secondary_fixed_dim)),
                    Color(context.getColor(android.R.color.system_on_secondary_fixed)),
                    Color(context.getColor(android.R.color.system_on_secondary_fixed_variant)),
                    Color(context.getColor(android.R.color.system_tertiary_fixed)),
                    Color(context.getColor(android.R.color.system_tertiary_fixed_dim)),
                    Color(context.getColor(android.R.color.system_on_tertiary_fixed)),
                    Color(context.getColor(android.R.color.system_on_tertiary_fixed_variant)),
                ),
            )
        }
    }
