package space.jay.colorfultextview

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

internal class AnimatorLinear {

    private var animator: ValueAnimator? = null
    private lateinit var gradientLinear: GradientLinear
    private var view: ColorfulTextView? = null

    fun isReady() = animator != null

    fun start() {
        animator?.start()
    }

    fun resume() {
        animator?.resume()
    }

    fun pause() {
        animator?.pause()
    }

    fun remove() {
        animator?.also {
            it.end()
            it.removeAllUpdateListeners()
        }
        animator = null
        view = null
    }

    fun setAnimation(
        view: ColorfulTextView,
        direction: TypeDirection,
        duration: Int,
        colors: IntArray,
    ) {
        if (colors.size < 2) {
            throw IllegalArgumentException("colors must be more than two")
        }
        remove()
        this.view = view
        setGradient(view, direction, colors)
        setAnimator(duration)
    }

    private fun setGradient(
        view: ColorfulTextView,
        direction: TypeDirection = TypeDirection.RIGHT,
        colors: IntArray
    ) {
        gradientLinear = GradientLinear(
            measuredTextSize = getTextSizeMeasured(view),
            direction = direction,
            arrayColor = colors
        )
    }

    private fun getTextSizeMeasured(view: ColorfulTextView) = TextSizeMeasured(
        width = view.paint.measureText(view.text.toString()),
        height = view.textSize
    )

    private fun setAnimator(duration: Int) {
        animator = ValueAnimator.ofFloat(0f, gradientLinear.windowSize.toFloat()).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()

            addUpdateListener { valueAnimator ->
                invalidateGradientColor(valueAnimator.currentPlayTime, duration)
            }
        }
    }

    private fun invalidateGradientColor(currentPlayTime: Long, duration: Int) {
        view?.also {
            it.paint.shader = gradientLinear.getShader(currentPlayTime, duration)
            it.invalidate()
        }
    }
}