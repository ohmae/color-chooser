# color chooser
[![license](https://img.shields.io/github/license/ohmae/color-chooser.svg)](./LICENSE)
[![GitHub release](https://img.shields.io/github/release/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/releases)
[![GitHub issues](https://img.shields.io/github/issues/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/ohmae/color-chooser.svg)](https://github.com/ohmae/color-chooser/issues?q=is%3Aissue+is%3Aclosed)
[![Maven Repository](https://img.shields.io/badge/maven-jcenter-brightgreen.svg)](https://bintray.com/ohmae/maven/net.mm2d.color-chooser)
[![Maven metadata URI](https://img.shields.io/maven-metadata/v/https/jcenter.bintray.com/net/mm2d/color-chooser/maven-metadata.xml.svg)](https://bintray.com/ohmae/maven/net.mm2d.color-chooser)

## ScreenShots

|![](readme/1.png)|![](readme/2.png)|![](readme/3.png)|
|:-:|:-:|:-:|
|![](readme/4.png)|![](readme/5.png)|![](readme/6.png)|

## How to use

You can download this library from jCenter.
```gradle
repositories {
    jcenter()
}
```

Add dependencies, as following.
```gradle
dependencies {
    implementation 'net.mm2d:color-chooser:0.0.2'
}
```

To show dialog. On `FragmentActivity` or `Fragment`

```kotlin
ColorChooserDialog.show(this, REQUEST_CODE, initialColor)
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

## Author
大前 良介 (OHMAE Ryosuke)
http://www.mm2d.net/

## License
[MIT License](./LICENSE)
