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
 * 2. 색상을 변경해주고 싶을 때는 setAnimation를 호출 하자
 */
package space.jay.colorfultextview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class ColorfulTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatTextView(context, attrs) {

    companion object {
        private const val NO_RESOURCE_ID_COLOR = -1
        private const val DURATION_DEFAULT = 500
    }

    private var isAnimationPausedFromUser: Boolean = false
    private val animator: AnimatorLinear = AnimatorLinear()

    init {
        val set = context.obtainStyledAttributes(attrs, R.styleable.ColorfulTextView)
        val colorsResourceId = set.getResourceId(R.styleable.ColorfulTextView_colorful_colors, NO_RESOURCE_ID_COLOR)
        val colors = getColorsFromResource(colorsResourceId)
        val duration = set.getInt(R.styleable.ColorfulTextView_colorful_duration, DURATION_DEFAULT)
        val direction = TypeDirection.getType(set.getInt(R.styleable.ColorfulTextView_colorful_direction, TypeDirection.RIGHT.value))
        set.recycle()
        setAnimation(direction = direction, duration = duration, colors = colors)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (isAnimationPausedFromUser) return
        when (visibility) {
            View.VISIBLE -> animator.resume()
            View.INVISIBLE,
            View.GONE -> animator.pause()
        }
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        if (isAnimationPausedFromUser) return
        if (screenState == SCREEN_STATE_ON) {
            if (this.isShown) animator.resume()
        } else {
            animator.pause()
        }
    }

    fun setAnimation(
        direction: TypeDirection = TypeDirection.RIGHT,
        duration: Int = DURATION_DEFAULT,
        vararg colors: Int,
    ) {
        animator.setAnimation(view = this, direction = direction, duration = duration, colors = colors)
        animator.start()
    }

    fun animationStart() {
        if (animator.isReady()) {
            isAnimationPausedFromUser = false
            animator.start()
        }
    }

    fun animationResume() {
        isAnimationPausedFromUser = false
        animator.resume()
    }

    fun animationPause() {
        isAnimationPausedFromUser = true
        animator.pause()
    }

    fun animationRemove() {
        isAnimationPausedFromUser = false
        animator.remove()
    }

    private fun getColorsFromResource(colorsResourceId: Int): IntArray {
        return if (colorsResourceId == NO_RESOURCE_ID_COLOR) {
            intArrayOf(Color.YELLOW, Color.BLUE, Color.MAGENTA)
        } else {
            resources.getIntArray(colorsResourceId)
        }
    }
}
