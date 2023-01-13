package space.jay.colorfultextview

import android.graphics.LinearGradient
import android.graphics.PointF
import android.graphics.Shader
import androidx.core.graphics.ColorUtils
import kotlin.math.ceil

internal class GradientMaker(
    val measuredTextSize: TextSizeMeasured,
    val direction: TypeDirection,
    val arrayColor: IntArray,
) {

    private val multiple: Int by lazy {
        val naturalColorSize = getNaturalColorSize()
        if (naturalColorSize > arrayColor.size) ceil(naturalColorSize / arrayColor.size).toInt() else 1
    }
    val windowSize: Int by lazy { arrayColor.size * multiple }

    fun getShader(currentPlayTime: Long, duration: Int): Shader {
        val pointStart : PointF
        val pointEnd : PointF
        when(direction) {
            TypeDirection.RIGHT -> {
                pointStart = PointF(measuredTextSize.width, 0f)
                pointEnd = PointF(0f, 0f)
            }
            TypeDirection.LEFT -> {
                pointStart = PointF(0f, 0f)
                pointEnd = PointF(measuredTextSize.width, 0f)
            }
            TypeDirection.UP -> {
                pointStart = PointF(0f, 0f)
                pointEnd = PointF(0f, measuredTextSize.height)
            }
            TypeDirection.DOWN -> {
                pointStart = PointF(0f, measuredTextSize.height)
                pointEnd = PointF(0f, 0f)
            }
        }

        return LinearGradient(
            pointStart.x,
            pointStart.y,
            pointEnd.x,
            pointEnd.y,
            getNextGradientColor(currentPlayTime, duration),
            null,
            Shader.TileMode.MIRROR
        )
    }

    private fun getNextGradientColor(currentPlayTime: Long, duration: Int) : IntArray {
        val fraction = (currentPlayTime % duration) / duration.toFloat()
        val colorIndex = ((currentPlayTime / duration) % windowSize).toInt()
        return IntArray(windowSize) {
            val index = it+colorIndex
            ColorUtils.blendARGB(getColor(index), getColor(index + 1), fraction)
        }
    }

    private fun getColor(index: Int) = arrayColor[(index / multiple) % arrayColor.size]

    private fun getNaturalColorSize() = when(direction) {
        TypeDirection.RIGHT,
        TypeDirection.LEFT -> measuredTextSize.width / 100
        TypeDirection.UP,
        TypeDirection.DOWN -> measuredTextSize.height / 30
    }

}