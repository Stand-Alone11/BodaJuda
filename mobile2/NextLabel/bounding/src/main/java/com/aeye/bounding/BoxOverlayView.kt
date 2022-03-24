package com.aeye.bounding

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

/**
 * @param context
 * @param radius touch area radius in dp, default 24dp
 */
class BoxOverlayView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0, private val radius: Int = 24): View(context, attributeSet, defStyleAttr) {
    private val TAG = "BoxOverlayView_debuk"

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (isEnabled) {
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    onActionDown(event.x, event.y)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    onActionUp()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    onActionMove(event.x, event.y)
                    parent.requestDisallowInterceptTouchEvent(true)
                    true
                }
                else -> false
            }
        } else {
            false
        }
    }

    private fun onActionDown(eventX: Float, eventY: Float) {

    }

    private fun onActionUp() {

    }

    private fun onActionMove(eventX: Float, eventY: Float) {

    }

    /** dp to pixel */
    val Int.dp: Int
        get() {
            val metrics = resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
                .toInt()
        }
}