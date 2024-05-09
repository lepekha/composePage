package ua.com.compose.screens.info

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.StringRes
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.data.InfoColor
import ua.com.compose.data.EColorType
import ua.com.compose.data.colorName
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import java.lang.Exception

sealed interface ColorInfoItem {

    data class Text(val title: String, val value: String): ColorInfoItem
    data class Colors(@StringRes val titleResId: Int, val colors: List<Int>): ColorInfoItem
    data class Color(val title: String, val color: Int): ColorInfoItem

}

class ColorInfoViewModel(private val addColorUseCase: AddColorUseCase): ViewModel() {

    private val _items: MutableLiveData<List<ColorInfoItem>> = MutableLiveData(listOf())
    val items: LiveData<List<ColorInfoItem>> = _items

    fun create(name: String?, color: Int) = viewModelScope.launch(Dispatchers.IO) {
        val items = mutableListOf<ColorInfoItem>()

        val _name = name ?: color.colorName()
        items.add(ColorInfoItem.Color(title = _name, color = color))

        val colors = EColorType.visibleValues().map { ColorInfoItem.Text(title = it.title(), value = it.colorToString(color, withSeparator = ",")) }
        items.addAll(colors)

        shadesOf(startColor = color, endColor = Color.BLACK).takeIf { it.isNotEmpty() }?.distinct()?.let {
            items.add(ColorInfoItem.Colors(titleResId = R.string.color_pick_shades, colors = it))
        }

        tintsOf(startColor = color, endColor = Color.WHITE).takeIf { it.isNotEmpty() }?.distinct()?.let {
            items.add(ColorInfoItem.Colors(titleResId = R.string.color_pick_tints, colors = it))
        }
        toneOf(baseColor = color).takeIf { it.isNotEmpty() }?.distinct()?.let {
            items.add(ColorInfoItem.Colors(titleResId = R.string.color_pick_tones, colors = it))
        }
        tetradicOf(baseColor = color).takeIf { it.isNotEmpty() }?.distinct()?.let {
            items.add(ColorInfoItem.Colors(titleResId = R.string.color_pick_tetradic_color, colors = it))
        }

        triadicOf(baseColor = color).takeIf { it.isNotEmpty() }?.distinct()?.let {
            items.add(ColorInfoItem.Colors(titleResId = R.string.color_pick_triadic_colors, colors = it))
        }

        analogousOf(baseColor = color).takeIf { it.isNotEmpty() }?.distinct()?.let {
            items.add(ColorInfoItem.Colors(titleResId = R.string.color_pick_analogous_colors, colors = it))
        }

        monochromaticOf(baseColor = color).takeIf { it.isNotEmpty() }?.distinct()?.let {
            items.add(ColorInfoItem.Colors(titleResId = R.string.color_pick_monochromatic_colors, colors = it))
        }

        complementaryOf(baseColor = color).takeIf { it.isNotEmpty() }?.distinct()?.let {
            items.add(ColorInfoItem.Colors(titleResId = R.string.color_pick_complementary_color, colors = it))
        }

        _items.postValue(items)
    }

    private fun triadicOf(baseColor: Int): List<Int>  = try {
        val palette = Palette.from(ColorDrawable(baseColor).toBitmap(10, 10)).generate()
        val triadicColors = ArrayList<Int>()
        val triadicColorCount = 3
        if (palette.swatches.isNotEmpty()) {
            val swatches = palette.swatches

            if (swatches.isNotEmpty()) {
                val dominantSwatch = palette.dominantSwatch ?: swatches[0]

                val hsl = FloatArray(3)
                ColorUtils.colorToHSL(dominantSwatch.rgb, hsl)

                val hue = hsl[0]

                for (i in 0 until triadicColorCount) {
                    val triadicHue = (hue + i * 120) % 360
                    val triadicColor =
                        ColorUtils.HSLToColor(floatArrayOf(triadicHue, hsl[1], hsl[2]))
                    triadicColors.add(triadicColor)
                }
            }
        }
        triadicColors
    } catch (e: Exception) {
        listOf()
    }

    private fun tetradicOf(baseColor: Int): List<Int> {
        val angleStep = 90 // крок зміни кута (90 градусів)

        val tetradicColors = ArrayList<Int>()

        val baseHsv = FloatArray(3)
        Color.colorToHSV(baseColor, baseHsv)

        for (i in 1..4) {
            val angle = (i * angleStep) % 360
            val tetradicHsv = floatArrayOf((baseHsv[0] + angle) % 360, baseHsv[1], baseHsv[2])
            val tetradicColor = Color.HSVToColor(tetradicHsv)
            tetradicColors.add(tetradicColor)
        }
        return tetradicColors
    }

    private fun toneOf(baseColor: Int): List<Int> {
        val toneColors = ArrayList<Int>()
        val targetSaturation = 1f/ 6f
        for (i in 1 until 7) {
            val hsv = FloatArray(3)
            Color.colorToHSV(baseColor, hsv)
            hsv[1] = targetSaturation * i

            val modifiedColor = Color.HSVToColor(hsv)
            toneColors.add(modifiedColor)
        }

        return toneColors
    }

    private fun analogousOf(baseColor: Int): List<Int> = try {
        val palette = Palette.from(ColorDrawable(baseColor).toBitmap(10,10)).generate()
        val analogousColors = ArrayList<Int>()
        val analogousColorCount = 6

        if (palette.swatches.isNotEmpty()) {
            val swatches = palette.swatches

            if (swatches.isNotEmpty()) {
                val dominantSwatch = palette.dominantSwatch ?: swatches[0]

                val hsl = FloatArray(3)
                ColorUtils.colorToHSL(dominantSwatch.rgb, hsl)

                val hue = hsl[0]

                for (i in 0 until analogousColorCount) {
                    val analogousHue = (hue + (i + 1) * 30) % 360
                    val analogousColor = ColorUtils.HSLToColor(floatArrayOf(analogousHue, hsl[1], hsl[2]))
                    analogousColors.add(analogousColor)
                }
            }
        }
        analogousColors
    } catch (e: Exception) {
        listOf()
    }

    private fun monochromaticOf(baseColor: Int): List<Int> {
        val monochromaticColors = ArrayList<Int>()

        val lightnessStep = 1f / 7f // крок зміни яскравості (від 0 до 1)

        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(baseColor, hsl)

        for (i in 1..6) {
            val lightness = hsl[2] * (1 - i * lightnessStep) // зменшення яскравості
            val monochromaticColor = ColorUtils.HSLToColor(floatArrayOf(hsl[0], hsl[1], lightness))
            monochromaticColors.add(monochromaticColor)
        }
        return monochromaticColors
    }

    private fun complementaryOf(baseColor: Int): List<Int> {
        val complementaryColors = ArrayList<Int>()

        val hsv = FloatArray(3)
        Color.colorToHSV(baseColor, hsv)

        val complementaryHue = (hsv[0] + 180) % 360

        val complementaryColor1 = Color.HSVToColor(floatArrayOf(complementaryHue, hsv[1], hsv[2]))
        complementaryColors.add(complementaryColor1)

        val complementaryHue2 = (complementaryHue + 180) % 360

        val complementaryColor2 = Color.HSVToColor(floatArrayOf(complementaryHue2, hsv[1], hsv[2]))
        complementaryColors.add(complementaryColor2)
        return complementaryColors
    }

    private fun shadesOf(startColor: Int, endColor: Int): List<Int> {
        val shadesCount = 6

        val shades = ArrayList<Int>()

        for (i in 0 until shadesCount) {
            val shade = ColorUtils.blendARGB(startColor, endColor, i.toFloat() / (shadesCount - 1))
            shades.add(shade)
        }
        return shades
    }

    private fun tintsOf(startColor: Int, endColor: Int): List<Int> {
        val shadesCount = 6

        val shades = ArrayList<Int>()

        for (i in 0 until shadesCount) {
            val shade = ColorUtils.blendARGB(startColor, endColor, 1 - (i.toFloat() / (shadesCount - 1)))
            shades.add(shade)
        }
         return shades
    }

    fun pressPaletteAdd(color: Int) = viewModelScope.launch(Dispatchers.IO) {
        analytics.send(SimpleEvent(key = Analytics.Event.CREATE_COLOR_FROM_INFO))
        addColorUseCase.execute(listOf(InfoColor(color = color)))
    }
}