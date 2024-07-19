package ua.com.compose.data.enums

import android.content.Context
import org.json.JSONObject
import ua.com.compose.data.adobecolor.core.toACOBytes
import ua.com.compose.data.adobecolor.core.toASEBytes
import ua.com.compose.data.adobecolor.model.AdobeColor
import ua.com.compose.data.db.ColorItem
import ua.com.compose.extension.color
import ua.com.compose.extension.userColorName
import ua.com.compose.extension.writeToFile
import ua.com.compose.colors.asHex
import ua.com.compose.colors.asRGB
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil


enum class  EFileExportScheme(val title: String, val allowForAll: Boolean = true) {

    TXT(title = ".txt") {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                colors.forEach {
                    append(colorType.colorToString(it.color()))
                    append(" ")
                    append(it.userColorName())
                    appendLine()
                }
            }
            return context.writeToFile("$palette.txt", builder)
        }
    },

    JSON(title = ".json") {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val json = JSONObject()
            val redJson = JSONObject()

            colors.forEach {
                val hex = it.color().asHex(withAlpha = false)
                val name = (it.userColorName())
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("'", "")
                    .lowercase()

                redJson.put(name, hex.toString())
            }

            json.put(palette, redJson)
            return context.writeToFile("$palette.json", json.toString())
        }
    },
    XML(title = ".xml") {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                appendLine("<resources>")
                colors.forEach {
                    val hex = it.color().asHex(withAlpha = false)
                    val name = (it.userColorName())
                        .replace(" ", "_")
                        .replace("-", "_")
                        .replace("'", "")
                        .lowercase()

                    appendLine("    <color name=\"${name}\">${hex}</color>")
                }
                appendLine("</resources>")
            }
            return context.writeToFile("$palette.xml", builder)
        }
    },
    CSV(title = ".csv") {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                appendLine("name,color")
                colors.forEach {
                    val hex = it.color().asHex(withAlpha = false)

                    val name = (it.userColorName())
                        .replace(" ", "_")
                        .replace("-", "_")
                        .replace("'", "")
                        .lowercase()

                    appendLine("${name},${hex}")
                }
            }
            return context.writeToFile("$palette.csv", builder)
        }
    },
    YAML(title = ".yaml") {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                appendLine("resources:")
                colors.forEach {
                    val hex = it.color().asHex(withAlpha = false)
                    val name = (it.userColorName())
                        .replace(" ", "_")
                        .replace("-", "_")
                        .replace("'", "")
                        .lowercase()

                    appendLine("- name: \"${name}\"")
                    appendLine("color: \"${hex}\"")
                }
            }
            return context.writeToFile("$palette.yaml", builder)
        }
    },
    TOML(title = ".toml") {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                colors.forEach {
                    appendLine("[[resources]]")
                    val hex = it.color().asHex(withAlpha = false)
                    val name = (it.userColorName())
                        .replace(" ", "_")
                        .replace("-", "_")
                        .replace("'", "")
                        .lowercase()

                    appendLine("name = \"${name}\"")
                    appendLine("color = \"${hex}\"")
                    appendLine()
                }
            }
            return context.writeToFile("$palette.toml", builder)
        }
    },
    GPL(title = ".gpl") {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                this.appendLine("GIMP Palette")
                this.appendLine("Name: $palette")
                this.appendLine("#")
                colors.forEach {
                    val name = (it.userColorName())
                        .replace(" ", "_")
                        .replace("-", "_")
                        .replace("'", "")
                        .lowercase()

                    val rgb = it.color().asRGB()

                    this.appendLine("${rgb.red} ${rgb.green} ${rgb.blue} $name")
                }
            }
            return context.writeToFile("$palette.gpl", builder)
        }
    },
    CSS(title = ".css", allowForAll = false) {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val builder = buildString {
                appendLine(""":root {""")
                colors.forEach {
                    val name = (it.userColorName())
                        .replace(" ", "_")
                        .replace("-", "_")
                        .replace("'", "")
                        .lowercase()

                    val rgb = it.color().asRGB()
                    appendLine("""--$name: rgb(${rgb.red}, ${rgb.green}, ${rgb.blue});""")
                }
                appendLine("}")
            }
            return context.writeToFile("$palette.css", builder)
        }
    },
    SVG(title = ".svg", allowForAll = false) {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val sizeH = 220
            val sizeW = 240
            val padding = 30
            val width = padding + (4 * (padding + sizeW))
            val height = padding + (ceil(colors.count() / 4f) * (sizeH + padding))

            val builder = buildString {
                appendLine("""<svg width="$width" height="$height" viewBox="0 0 $width $height" fill="none" xmlns="http://www.w3.org/2000/svg">""")
                appendLine("""<g id="$palette">""")


                var x = padding
                var y = padding
                colors.forEachIndexed { index, it ->
                    val hex = it.color().asHex(withAlpha = false)
                    val _name = it.userColorName()
                    val name = _name
                        .replace(" ", "_")
                        .replace("-", "_")
                        .replace("'", "")
                        .lowercase()

                    val rgb = hex.asRGB()

                    appendLine("""<g id="$name">""")
                    appendLine("""<rect x="$x" y="$y" width="$sizeW" height="$sizeH" fill="white"/>""")
                    appendLine("""<rect x="$x" y="$y" width="$sizeW" height="140" fill="$hex"/>""")

                    appendLine("""<text fill="black" xml:space="preserve" style="white-space: pre" font-family="sans-serif" font-size="14" font-weight="600" letter-spacing="0em"><tspan x="${x + 8}" y="${y + 162.785}">$_name</tspan></text>""")
                    appendLine("""<text fill="#878787" xml:space="preserve" style="white-space: pre" font-family="sans-serif" font-size="12" font-weight="600" letter-spacing="0em"><tspan x="${x + 8}" y="${y + 184.102}">$hex</tspan></text>""")
                    appendLine("""<text fill="#878787" xml:space="preserve" style="white-space: pre" font-family="sans-serif" font-size="12" font-weight="600" letter-spacing="0em"><tspan x="${x + 8}" y="${y + 204.102}">rgb(${rgb.red}, ${rgb.green}, ${rgb.blue})</tspan></text>""")

                    appendLine("""</g>""")

                    x+= padding + sizeW

                    if(((index + 1) % 4) == 0) {
                        x = padding
                        y+= padding + sizeH
                    }
                }
                appendLine("""</g>""")
                appendLine("""</svg>""")
            }
            return context.writeToFile("$palette.svg", builder)
        }
    },
    ACO(title = ".aco", allowForAll = false) {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val data = colors.map {
                val name = (it.userColorName())
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("'", "")
                    .lowercase()
                AdobeColor(it.color().intColor, name)
            }.toACOBytes()

            try {
                val outputFile = File(context.cacheDir.path, "$palette.aco")
                outputFile.writeBytes(data)
                return outputFile
            } catch (e: IOException) {
            }
            return null
        }
    },
    ASE(title = ".ase", allowForAll = false) {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val data = colors.map {
                val name = (it.userColorName())
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("'", "")
                    .lowercase()
                AdobeColor(it.color().intColor, name)
            }.toASEBytes(palette)

            try {
                val outputFile = File(context.cacheDir.path, "$palette.ase")
                outputFile.writeBytes(data)
                return outputFile
            } catch (e: IOException) {
            }
            return null
        }
    },
    ACT(title = ".act", allowForAll = false) {
        override fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File? {
            val outputFile = File(context.cacheDir, "$palette.act")
            try {
                DataOutputStream(FileOutputStream(outputFile)).use { outputStream ->
                    colors.forEach {
                        val rgb = it.color().asRGB()
                        outputStream.write(rgb.red)
                        outputStream.write(rgb.green)
                        outputStream.write(rgb.blue)
                        outputStream.write(0)
                    }

                    val remainingBytes = 772 - colors.size * 4 - 4 // 4 байти на кожен колір + 2 байти для кількості кольорів + 2 байти для 0xFF
                    repeat(remainingBytes) {
                        outputStream.writeByte(0)
                    }

                    outputStream.writeShort(colors.size)
                    outputStream.write(0xFF)
                    outputStream.write(0xFF)
                }
                return outputFile
            } catch (e: IOException) {
            }
            return null
        }
    };

    fun fileFormat() = this.title

    abstract fun create(context: Context, palette: String, colors: List<ColorItem>, colorType: EColorType): File?

}