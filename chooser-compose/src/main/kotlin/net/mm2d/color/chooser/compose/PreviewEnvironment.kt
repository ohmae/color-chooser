/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * ライト/ダークテーマ、RTL環境、フォントスケール拡大をまとめて確認するためのマルチプレビューアノテーション。
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "RTL", locale = "ar")
@Preview(name = "Font Scale 1.5x", fontScale = 1.5f)
internal annotation class PreviewEnvironment
