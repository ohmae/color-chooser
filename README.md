# color chooser

[![license](https://img.shields.io/github/license/ohmae/color-chooser.svg)](./LICENSE)
[![GitHub release](https://img.shields.io/github/release/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/releases)
[![GitHub issues](https://img.shields.io/github/issues/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/issues?q=is%3Aissue+is%3Aclosed)
![Maven Central](https://img.shields.io/maven-central/v/net.mm2d.color-chooser/color-chooser)

## ScreenShots

|![](readme/1.png)|![](readme/2.png)|![](readme/3.png)|
|:-:|:-:|:-:|
|![](readme/4.png)|![](readme/5.png)|![](readme/6.png)|
|![](readme/7.png)|![](readme/8.png)|![](readme/9.png)|

## How to use

jCenter will close in May. In 0.2.4 moved to mavenCentral from jcenter.  
Please note that the **groupID has changed**

Download from mavenCentral.  
latest version: ![Maven Central](https://img.shields.io/maven-central/v/net.mm2d.color-chooser/color-chooser)

```gradle
repositories {
    jcenter()
}
dependencies {
    implementation 'net.mm2d.color-chooser:color-chooser:<version>'
}
```

Versions below 0.2.4 were distributed with jCenter.
However, jCenter will close and old versions are not migrated to mavenCentral.
If you need an older version, please use the Github Pages repository.

```gradle
repositories {
    maven { url = URI("https://ohmae.github.com/maven") }
}
dependencies {
    implementation 'net.mm2d:color-chooser:<version>'
}
```

To show dialog. On `FragmentActivity` or `Fragment`

```kotlin
ColorChooserDialog.show(
    this,         // Fragment or FragmentActivity
    REQUEST_CODE, // request code for receive result, optional, default 0
    initialColor, // initial color, optional, default #FFFFFF
    true,         // need for alpha, optional, default false
    TAB_RGB       // initial tab, TAB_PALETTE/TAB_HSV/TAB_RGB, optional, default  TAB_PALETTE
)
```

To receive result. Implement `ColorChooserDialog.Callback` to `Activity` or `Fragment`

```kotlin
class MainActivity : AppCompatActivity(), ColorChooserDialog.Callback {
    override fun onColorChooserResult(requestCode: Int, resultCode: Int, color: Int) {
        if (requestCode != REQUEST_CODE || resultCode != Activity.RESULT_OK) return
        // use color
    }
}
```

Please see [Sample code](sample/src/main/java/net/mm2d/color/chooser/sample/MainActivity.kt) for detail.

## API Document

- [dokka](https://ohmae.github.io/color-chooser/dokka/)

## Dependent OSS

### color-chooser

- [Kotlin](https://kotlinlang.org/)
- [Android Jetpack](https://developer.android.com/jetpack/)
  - androidx.appcompat:appcompat
  - androidx.core:core-ktx
  - com.google.android.material:material

### sample app

- [Kotlin](https://kotlinlang.org/)
- [Android Jetpack](https://developer.android.com/jetpack/)
  - androidx.appcompat:appcompat
  - androidx.constraintlayout:constraintlayout
- [LeakCanary](https://github.com/square/leakcanary)

## Author

大前 良介 (OHMAE Ryosuke)
http://www.mm2d.net/

## License

[MIT License](./LICENSE)
