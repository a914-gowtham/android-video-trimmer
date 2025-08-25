package com.gowtham.library.utils

import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View

object ViewUtil {

    val Number.toPx get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics).toInt()

    @JvmStatic
    fun dpToPx(dp: Int): Int{

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics).toInt()
    }
    @JvmStatic
    fun systemGestureExclusionRects(viewRoot: View, thumbnailViewer: View) {
        var rendered= false
        viewRoot.post {
            viewRoot.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (rendered){
                    return@addOnLayoutChangeListener
                }
                rendered= true
                val rects = mutableListOf<Rect>()
                rects.add(
                    Rect(
                        0,
                        thumbnailViewer.top-20.toPx,
                        viewRoot.width,
                        thumbnailViewer.bottom+ 20.toPx
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    viewRoot.systemGestureExclusionRects = rects
                }
            }
        }
    }
}