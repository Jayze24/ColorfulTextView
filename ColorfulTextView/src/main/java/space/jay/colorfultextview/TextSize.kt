package space.jay.colorfultextview

import android.widget.TextView

internal class TextSize {

    var width: Float = 0f
        private set
    var height: Float = 0f
        private set

    fun measureText(textView: TextView) {
        width = textView.paint.measureText(textView.text.toString())
        height = textView.textSize
    }
}