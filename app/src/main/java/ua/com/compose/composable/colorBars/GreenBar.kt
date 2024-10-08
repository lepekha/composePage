package ua.com.compose.composable.colorBars

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import ua.com.compose.colors.asHSL
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.colorRGBdecimalOf
import ua.com.compose.colors.data.HSLColor
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.data.RGBColor
import ua.com.compose.colors.textColor
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.asComposeColor
import ua.com.compose.extension.vibrate

@Composable
fun GreenBar(
    color: MutableState<IColor>,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var pressOffset by remember { mutableStateOf<Offset?>(null) }

    BoxWithConstraints(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    view.vibrate(type = EVibrate.BUTTON)
                    // Обробка tap (натискання)
                    val width = this.size.width
                    val radiusPx = this.size.height / 2f
                    val newX = down.position.x.coerceIn(radiusPx, width.toFloat() - radiusPx)
                    val newGreen = ((newX - radiusPx) / (width - 2 * radiusPx)).coerceIn(0f, 1f)

                    // Оновлюємо колір
                    val rgb = (color.value as RGBColor)
                    color.value = rgb.copy(green = (newGreen * 255).toInt())
                    pressOffset = Offset(newX, this.size.height / 2f)

                    // Обробка drag (перетягування)
                    drag(down.id) { change ->
                        val newDragX = change.position.x.coerceIn(radiusPx, width.toFloat() - radiusPx)
                        val newDragGreen = ((newDragX - radiusPx) / (width - 2 * radiusPx)).coerceIn(0f, 1f)

                        // Оновлюємо колір
                        color.value = rgb.copy(green = (newDragGreen * 255).toInt())
                        pressOffset = Offset(newDragX, this.size.height / 2f)
                        change.consume() // Споживаємо зміни
                    }
                }
            }
    ) {
        val widthPx = maxWidth
        val radiusPx = maxHeight / 2

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Отримуємо поточні RGB значення
            val rgb = (color.value as RGBColor)

            // Створюємо градієнт для зеленого кольору
            val greenColors = (0..255).map { greenValue ->
                colorRGBdecimalOf(red = rgb.red, green = greenValue, blue = rgb.blue).asComposeColor()
            }

            // Малюємо фон з градієнтом
            drawRoundRect(
                brush = Brush.horizontalGradient(greenColors),
                topLeft = Offset(0f, 0f),
                size = Size(widthPx.toPx(), maxHeight.toPx()),
                cornerRadius = CornerRadius(radiusPx.toPx(), radiusPx.toPx())
            )

            // Позиція поточного зеленого кольору
            val currentGreenX = (rgb.green / 255f) * (widthPx.toPx() - 2 * radiusPx.toPx()) + radiusPx.toPx()

            // Малюємо індикатор
            val centerX = pressOffset?.x ?: currentGreenX

            // Гарантуємо, що індикатор не виходить за межі бара з урахуванням радіусу
            val clampedX = centerX.coerceIn(radiusPx.toPx(), widthPx.toPx() - radiusPx.toPx())

            // Малюємо зовнішній білий круговий індикатор
            drawCircle(
                color = rgb.textColor().asComposeColor(),
                radius = radiusPx.toPx() - 3.dp.toPx(),
                center = Offset(clampedX, radiusPx.toPx())
            )
            // Малюємо внутрішній кольоровий індикатор
            drawCircle(
                color = rgb.asComposeColor(),
                radius = radiusPx.toPx() - 5.dp.toPx(),
                center = Offset(clampedX, radiusPx.toPx())
            )
        }
    }
}