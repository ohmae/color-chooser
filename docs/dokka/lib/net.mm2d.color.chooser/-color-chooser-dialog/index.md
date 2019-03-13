[lib](../../index.md) / [net.mm2d.color.chooser](../index.md) / [ColorChooserDialog](./index.md)

# ColorChooserDialog

`class ColorChooserDialog : DialogFragment`

Color chooser dialog

**Author**
[大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)

### Types

| Name | Summary |
|---|---|
| [Callback](-callback/index.md) | `interface Callback`<br>Result callback implements to Fragment or Activity |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ColorChooserDialog()`<br>Color chooser dialog |

### Functions

| Name | Summary |
|---|---|
| [onCancel](on-cancel.md) | `fun onCancel(dialog: `[`DialogInterface`](https://developer.android.com/reference/android/content/DialogInterface.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateDialog](on-create-dialog.md) | `fun onCreateDialog(savedInstanceState: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Dialog`](https://developer.android.com/reference/android/app/Dialog.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [show](show.md) | `fun show(activity: FragmentActivity, requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0, initialColor: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = Color.WHITE, withAlpha: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`fun show(fragment: Fragment, requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0, initialColor: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = Color.WHITE, withAlpha: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Show dialog |
