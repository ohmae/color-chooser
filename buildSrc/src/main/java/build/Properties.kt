package build

object Properties {
    const val groupId: String = "net.mm2d"

    private const val versionMajor: Int = 0
    private const val versionMinor: Int = 1
    private const val versionPatch: Int = 7
    const val versionName: String = "$versionMajor.$versionMinor.$versionPatch"
    const val versionCode: Int = versionMajor * 10000 + versionMinor * 100 + versionPatch

    object Url {
        const val site: String = "https://github.com/ohmae/color-chooser"
        const val github: String = "https://github.com/ohmae/color-chooser"
        const val scm: String = "scm:git:https://github.com/ohmae/color-chooser.git"
    }
}
