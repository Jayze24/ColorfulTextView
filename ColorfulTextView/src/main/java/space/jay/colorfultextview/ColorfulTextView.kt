/**
 * @since 2022-12-12 오전 12:36
 * @author Jay
 *
 * @explanation 
 * AppCompatTextView 를 상속 받아서 만든 그래디언트 애니메이션 뷰이다.
 * xml 에서 사용할때 아래 속성을 필수로 넣어줘야 한다.
 * <declare-styleable name="TextGradientAnimationView">
 *      <attr name="colors" format="reference"/>
 *      <attr name="duration" format="integer"/>
 * </declare-styleable>
 * 
 * @sample 
 * <space.jay.book.view.text.gradient.TextGradientAnimationView
 *      android:id="@+id/textViewSearchBookLoading"
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      android:text="@string/loading"
 *      android:textSize="32sp"
 *      android:textStyle="bold|italic"
 *      app:colors="@array/colors_loading"
 *      app:duration="500" />
 * 
 * @comment 
 * 1. onDestroy() 에서 animationRemove() 를 호출 하자
 * 2. 색상을 변경해주고 싶을 때는 아래와 같이 호출 하면 된다.
 *    setAnimator(1000, Color.BLUE, Color.GRAY, Color.GREEN, Color.CYAN)
 * 3. 지속적으로 보이는 애니메이션이라면 onResume() 과 onPause()에서 animationResume() 과 animationPause()
 */
package space.jay.colorfultextview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.graphics.ColorUtils

class ColorfulTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var animator: ValueAnimator? = null
    private var arrayColor: IntArray = intArrayOf()
    private var windowSize: Int = 0 // arrayColor에서 컬러를 가져올 사이즈
    private var noColorResourceId = -1

    init {
        val set = context.obtainStyledAttributes(attrs, R.styleable.ColorfulTextView)
        val colorsResourceId = set.getResourceId(R.styleable.ColorfulTextView_colors, noColorResourceId)
        val colors = getGradientColorFromResource(colorsResourceId)
        val duration = set.getInt(R.styleable.ColorfulTextView_duration, 500)
        set.recycle()
        setAnimator(duration, *colors)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when(visibility) {
            View.VISIBLE -> animationStart()
            View.INVISIBLE,
            View.GONE -> animationPause()
        }
    }

    fun setAnimator(@androidx.annotation.IntRange(from = 100, to = 1000) duration: Int, vararg colors: Int) {
        if (colors.size < 2) {
            throw IllegalArgumentException("colors must be more than two")
        }
        animationRemove()
        windowSize = colors.size
        arrayColor = colors + colors
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            this.duration = duration.toLong()

            addUpdateListener { valueAnimator ->
                val nextGradientColor = getNextGradientColor(valueAnimator.currentPlayTime, duration)
                val shader = getShader(nextGradientColor)
                invalidateGradientColor(shader)
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

    private fun getGradientColorFromResource(colorsResourceId: Int) : IntArray {
        return if (colorsResourceId == noColorResourceId) {
            intArrayOf(Color.YELLOW, Color.BLUE, Color.MAGENTA)
        } else {
            resources.getIntArray(colorsResourceId)
        }
    }

    private fun invalidateGradientColor(shader: Shader) {
        this@ColorfulTextView.paint.shader = shader
        this@ColorfulTextView.invalidate()
    }

    private fun getNextGradientColor(currentPlayTime: Long, duration: Int) : IntArray {
        val fraction = (currentPlayTime % duration) / duration.toFloat()
        val colorIndex = ((currentPlayTime / duration) % windowSize).toInt()
        return IntArray(windowSize) {
            val index = it+colorIndex
            ColorUtils.blendARGB(arrayColor[index], arrayColor[index+1], fraction)
        }
    }

    private fun getShader(colors: IntArray): Shader {
        return LinearGradient(
            0f,
            0f,
            this.paint.measureText(this.text.toString()),
            this.textSize,
            colors,
            null,
            Shader.TileMode.MIRROR
        )
    }

}