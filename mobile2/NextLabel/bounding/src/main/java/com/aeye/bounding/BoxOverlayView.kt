package com.aeye.bounding

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

/**
 * @param context
 * @param radius touch area radius in dp, default 24dp
 */
class BoxOverlayView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0, private val radius: Int = 24): View(context, attributeSet, defStyleAttr) {
    private val TAG = "BoxOverlayView_debuk"

    /** 부모를 기준으로한 한계 좌표 */
    private var mLeftLimit = 0
    private var mTopLimit = 0
    private var mRightLimit = 0
    private var mBottomLimit = 0

    /** 박스영역 */
    private var mRect = RectF()

    /** 박스 영역을 다루는 Handler */
    private val mBoxHandler = BoxHandler(mRect, radius)

    /** draw */
    private val mBoxPaint: Paint = Paint().apply {
        strokeWidth = 2f
        color = Color.WHITE
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val mBackgroundPaint: Paint = Paint().apply {
        color = Color.argb(119, 0, 0, 0)
    }

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

    /** mRect와 radius를 mBoxHandler에 전달하여 터치 이벤트 시작 */
    private fun onActionDown(eventX: Float, eventY: Float) {
        mBoxHandler.onTouchDown(eventX, eventY)?.let {
            mRect = it
        }
    }

    private fun onActionUp() {

    }

    private fun onActionMove(eventX: Float, eventY: Float) {
        mBoxHandler.onTouchMove(eventX, eventY)?.let {
            mRect = it
        }
    }

    /** 부모를 기준으로 한 한계좌표 set */
    fun setLimits(left: Int, top: Int, right: Int ,bottom: Int) {
        mLeftLimit = left
        mTopLimit = top
        mRightLimit = right
        mBottomLimit = bottom

        mBoxHandler.setLimit(mLeftLimit, mTopLimit, mRightLimit, mBottomLimit)
    }

    /** dp to pixel */
    val Int.dp: Int
        get() {
            val metrics = resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
                .toInt()
        }

    interface BoxChangedListener {
        fun onChanged()
    }
}