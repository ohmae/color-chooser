# color chooser

[![license](https://img.shields.io/github/license/ohmae/color-chooser.svg)](./LICENSE)
[![GitHub release](https://img.shields.io/github/release/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/releases)
[![GitHub issues](https://img.shields.io/github/issues/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/issues?q=is%3Aissue+is%3Aclosed)
[![Maven Central](https://img.shields.io/maven-central/v/net.mm2d.color-chooser/color-chooser)](https://search.maven.org/artifact/net.mm2d.color-chooser/color-chooser)

## ScreenShots

|![](readme/1.png)|![](readme/2.png)|![](readme/3.png)|
|:-:|:-:|:-:|
|![](readme/4.png)|![](readme/5.png)|![](readme/6.png)|
|![](readme/7.png)|![](readme/8.png)|![](readme/9.png)|

## How to use

### Jetpack Compose

latest version: ![Maven Central](https://img.shields.io/maven-central/v/net.mm2d.color-chooser/color-chooser-compose)

```gradle
dependencies {
    implementation 'net.mm2d.color-chooser:color-chooser-compose:<version>'
}
```

Use `ColorChooserScreen` inside your screen or a dialog with a bounded width and height.
It reports edits through `onColorChanged`; the caller owns confirmation and cancellation.

```kotlin
@Composable
fun ColorPickerScreen(
    initialColor: Color,
    onConfirm: (Color) -> Unit,
    onCancel: () -> Unit,
    withAlpha: Boolean = true,
) {
    var selectedArgb by rememberSaveable(initialColor) {
        mutableIntStateOf(initialColor.toArgb())
    }
    if (!withAlpha && (selectedArgb ushr 24) != 255) {
        selectedArgb = selectedArgb or (0xFF shl 24)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        ColorChooserScreen(
            initialColor = initialColor,
            onColorChanged = { selectedArgb = it.toArgb() },
            withAlpha = withAlpha,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
        Row {
            TextButton(onClick = onCancel) { Text("Cancel") }
            TextButton(onClick = { onConfirm(Color(selectedArgb)) }) { Text("OK") }
        }
    }
}
```

- Inputs are converted to **8-bit sRGB** for editing and previews; callbacks return the same
  representation. Wide-gamut inputs are converted, not interpreted as sRGB components.
  `Color.Unspecified` is not supported.
- `withAlpha = false` discards transparency: previews and results are opaque, even when confirmed
  without editing. Turning alpha back on during editing starts with full opacity.
- Edited color and selected tab survive activity/process recreation. Restoration does not invoke
  `onColorChanged`, so also save the pending confirmation result, as in the example above.
  Keep `initialColor` as the session's starting color; changing it resets the edited color.
- When embedding in a `verticalScroll` or `LazyColumn` without a fixed item height, pass
  `disableInnerScroll = true`. Palette horizontal scrolling still requires a bounded width.
- Color ramps keep a left-to-right direction in RTL layouts. RGB, opacity and hue support
  accessibility value adjustment, arrow keys and Home/End. The saturation/brightness area supports
  accessibility increase/decrease actions and arrow keys (left/right for saturation, up/down for brightness).

`ColorChooserView`, `ColorChooserDialog` and `Tab` are deprecated but remain available for compatibility.
Migrate tab selections to `Chooser` (`Tab.PALETTE` becomes `Chooser.M2`) and use `ColorChooserScreen`.
The old `titleContentColor` maps to both `selectedTabContentColor` and `unselectedTabContentColor`
in `ColorChooserDefaults.colors()`. The legacy `ColorChooserView` reflects external `colorState`
updates and normalizes that state to the same sRGB/alpha rules.

See the [Compose sample screen](sample-compose/src/main/kotlin/net/mm2d/color/chooser/sample/compose/ChooserScreen.kt)
for confirmation, cancellation and saved-state handling.

### View-base app

latest version: ![Maven Central](https://img.shields.io/maven-central/v/net.mm2d.color-chooser/color-chooser)

```gradle
dependencies {
    implementation 'net.mm2d.color-chooser:color-chooser:<version>'
}
```

Register the listener to receive the result.
Write the following process in onViewCreated of Fragment or onCreate of Activity.

```kotlin
ColorChooserDialog.registerListener(REQUEST_KEY, this) {
    // it is selected color as @ColorInt
}
```

To show dialog. On `FragmentActivity` or `Fragment`

```kotlin
ColorChooserDialog.show(
    this,         // Fragment or FragmentActivity
    REQUEST_KEY,  // request key for receive result
    initialColor, // initial color, optional, default #FFFFFF
    true,         // need for alpha, optional, default false
    TAB_RGB       // initial tab, TAB_PALETTE/TAB_HSV/TAB_RGB, optional, default  TAB_PALETTE
)
```

*The style of implementing a callback interface in Activity and Fragment has been deprecated.*

Please see [Sample code](sample/src/main/java/net/mm2d/color/chooser/sample/MainActivity.kt) for detail.

## API Document

- [dokka](https://ohmae.github.io/color-chooser/dokka/)

## Dependent OSS

- [color-chooser](./chooser/dependencies/releaseRuntimeClasspath.txt)
- [color-chooser-compose](./chooser-compose/dependencies/releaseRuntimeClasspath.txt)

## Author

大前 良介 (OHMAE Ryosuke)
http://www.mm2d.net/

## License

[MIT License](./LICENSE)
