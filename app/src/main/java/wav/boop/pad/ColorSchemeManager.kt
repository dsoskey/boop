package wav.boop.pad

interface ColorSchemeManager {
    var colorScheme: ColorScheme
    fun onColorSchemeChange(newScheme: ColorScheme)
}