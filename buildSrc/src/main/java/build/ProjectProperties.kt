package build

object ProjectProperties {
    const val groupId: String = "net.mm2d.color-chooser"
    const val name: String = "color chooser"
    const val description: String = "Color chooser dialog library for android"
    const val developerId: String = "ryo"
    const val developerName: String = "ryosuke"

    private const val versionMajor: Int = 0
    private const val versionMinor: Int = 6
    private const val versionPatch: Int = 1
    const val versionName: String = "$versionMajor.$versionMinor.$versionPatch"
    const val versionCode: Int = versionMajor * 10000 + versionMinor * 100 + versionPatch

    object Url {
        const val site: String = "https://github.com/ohmae/color-chooser"
        const val github: String = "https://github.com/ohmae/color-chooser"
        const val scm: String = "scm:git:https://github.com/ohmae/color-chooser.git"
    }
}
