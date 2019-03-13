[lib](../../../index.md) / [net.mm2d.color.chooser](../../index.md) / [ColorChooserDialog](../index.md) / [Callback](index.md) / [onColorChooserResult](./on-color-chooser-result.md)

# onColorChooserResult

`abstract fun onColorChooserResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, color: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Call at close dialog

### Parameters

`requestCode` - requestCode of show parameter

`resultCode` - `Activity.RESULT_OK` or `Activity.RESULT_CANCELED`

`color` - selected color