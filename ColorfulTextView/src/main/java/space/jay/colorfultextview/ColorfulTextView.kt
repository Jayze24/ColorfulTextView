/**
 * @since 2022-12-12 오전 12:36
 * @author Jay
 *
 * @explanation
 * AppCompatTextView를 상속 받아서 만든 그래디언트 애니메이션 텍스트뷰이다.
 *
 * @sample
 * <space.jay.colorfultextview.ColorfulTextView
 *      android:id="@+id/textViewSearchBookLoading"
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      android:text="Colorful Text View"
 *      android:textSize="32sp"
 *      android:textStyle="bold|italic"
 *      app:colorful_colors="@array/colors_loading"
 *      app:colorful_duration="500"
 *      app:colorful_direction="right" />
 *
 * @comment
 * 1. onDestroy() 에서 animationRemove() 를 호출 하자
 * 2. 색상을 변경해주고 싶을 때는 setAnimator를 호출 하자
 * 3. 지속적으로 보이는 애니메이션이라면 onResume() 과 onPause()에서 animationResume() 과 animationPause()
 */
package space.jay.colorfultextview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView

class ColorfulTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatTextView(context, attrs) {

    private val noColorResourceId = -1
    private var animator: ValueAnimator? = null
    private var duration: Int = 500
    private lateinit var gradientMaker: GradientMaker

    init {
        val set = context.obtainStyledAttributes(attrs, R.styleable.ColorfulTextView)
        val colorsResourceId = set.getResourceId(R.styleable.ColorfulTextView_colorful_colors, noColorResourceId)
        val colors = getColorsFromResource(colorsResourceId)
        val duration = set.getInt(R.styleable.ColorfulTextView_colorful_duration, duration)
        val direction = TypeDirection.getType(set.getInt(R.styleable.ColorfulTextView_colorful_direction, TypeDirection.RIGHT.value))
        set.recycle()
        setAnimator(direction = direction, duration = duration, colors = colors)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when (visibility) {
            View.VISIBLE -> animationStart()
            View.INVISIBLE,
            View.GONE -> animationPause()
        }
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        if (screenState == SCREEN_STATE_ON) {
            animationStart()
        } else {
            animationPause()
        }
    }

    fun setAnimator(
        direction: TypeDirection = TypeDirection.RIGHT,
        duration: Int = 500,
        vararg colors: Int,
    ) {
        if (colors.size < 2) {
            throw IllegalArgumentException("colors must be more than two")
        }
        animationRemove()
        gradientMaker = GradientMaker(
            measuredTextSize = getTextSizeMeasured(),
            direction = direction,
            arrayColor = colors
        )
        animator = ValueAnimator.ofFloat(0f, gradientMaker.windowSize.toFloat()).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()

            addUpdateListener { valueAnimator ->
                invalidateGradientColor(valueAnimator.currentPlayTime, duration)
            }
        }
    }

    fun animationStart() {
        if (animator == null) {
            throw IllegalStateException("must set colors before start")
        }
        animator!!.start()
    }

    fun animationResume() = animator?.resume()

    fun animationPause() = animator?.pause()

    fun animationRemove() {
        animator?.also {
            it.end()
            it.removeAllUpdateListeners()
        }
        animator = null
    }

    private fun getColorsFromResource(colorsResourceId: Int): IntArray {
        return if (colorsResourceId == noColorResourceId) {
            intArrayOf(Color.YELLOW, Color.BLUE, Color.MAGENTA)
        } else {
            resources.getIntArray(colorsResourceId)
        }
    }

    private fun getTextSizeMeasured() = TextSizeMeasured(
        width = this.paint.measureText(this.text.toString()),
        height = this.textSize
    )

    private fun invalidateGradientColor(currentPlayTime: Long, duration: Int) {
        this@ColorfulTextView.paint.shader = gradientMaker.getShader(currentPlayTime, duration)
        this@ColorfulTextView.invalidate()
    }
}