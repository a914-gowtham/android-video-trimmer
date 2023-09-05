package com.gowtham.library.utils

import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.core.view.doOnLayout

object ViewUtil {

    val Number.toPx get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics).toInt()

    @JvmStatic
    fun systemGestureExclusionRects(viewRoot: View) {
        viewRoot.post {
            viewRoot.apply {
                doOnLayout {
                    // updating exclusion rect
                    val rects = mutableListOf<Rect>()
                    rects.add(Rect(0,resources.displayMetrics.heightPixels-(120.toPx),width,resources.displayMetrics.heightPixels-(55.toPx)))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        systemGestureExclusionRects = rects
                    }
                }
            }
        }
    }
}