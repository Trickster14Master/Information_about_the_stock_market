package com.plcoding.stockmarketapp.presentation.company_info

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.plcoding.stockmarketapp.domain.model.IntroDayInfo
import kotlin.math.roundToInt
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
fun StockChart(
    infos: List<IntroDayInfo> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: Color = Color.Green
) {
    // константа с отступами
    val spacing = 100f
    // переменная для позрачначности цвета графика
    val transparentGraphColor = remember {
        graphColor.copy(alpha = 0.5f)
    }
    // получаем самое большое значение
    val upperValue = remember(infos) {
        (infos.maxOfOrNull { it.close }?.plus(1))?.roundToInt() ?: 0
    }
    // самое низкое значение
    val lowerValue = remember(infos) {
        infos.minOfOrNull { it.close }?.toInt() ?: 0
    }
    // переменная которая будет отвечать за толщены линий
    val density = LocalDensity.current
    // переменная с помощью которой будет рисоваться график
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }
    // создаём область для рисования
    Canvas(modifier = modifier) {
        // строим ось Х
        val spacePerHour = (size.width - spacing) / infos.size
        // описываем шаг между значениями
        (0 until infos.size - 1 step 2).forEach { i ->
            val info = infos[i]
            val hour = info.data.hour
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    hour.toString(),
                    spacing + i * spacePerHour,
                    size.height - 5,
                    textPaint
                )
            }
        }
        // строим ось У
        val priceStep = (upperValue - lowerValue) / 5f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * size.height / 5f,
                    textPaint
                )
            }
        }
        var lastX = 0f
        val strokePath = Path().apply {
            val height = size.height
            for(i in infos.indices) {
                val info = infos[i]
                val nextInfo = infos.getOrNull(i + 1) ?: infos.last()
                val leftRatio = (info.close - lowerValue) / (upperValue - lowerValue)
                val rightRatio = (nextInfo.close - lowerValue) / (upperValue - lowerValue)

                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (leftRatio * height).toFloat()
                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * height).toFloat()
                if(i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f
                quadraticBezierTo(
                    x1, y1, lastX, (y1 + y2) / 2f
                )
            }
        }
        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close()
            }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            )
        )
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}