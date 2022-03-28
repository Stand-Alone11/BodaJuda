package com.aeye.bounding

import android.graphics.RectF
import android.util.Log

/**
 * 바운딩 박스를 다루는 핸들러
 */

/**
 * @param rectF 바운딩 박스
 */
class BoxHandler(rectF: RectF, private val radius: Int) {
    private val TAG = "BoxHandler_debuk"

    /** mRect는 항상 최신 Rect로 유지 */
    private var mRect = rectF

    /** BoxOverlayView의 좌표를 기준으로한 touch event 좌표*/
    private var mTouchX = -1f
    private var mTouchY = -1f

    /** mRect의 한계 좌표 */
    private var mLeftLimit: Int = -1
    private var mTopLimit: Int = -1
    private var mRightLimit: Int = -1
    private var mBottomLimit: Int = -1

    /** RectF의 최소 크기 */
    private var mMinWidth = 60
    private var mMinHeight = 60

    /** onTouchDown에서 touchEvent의 유효성을 나타내는 Boolean */
    private var isValidTouchEvent = true

    private var mType = Type.DRAG

    /** 한계좌표 설정 */
    fun setLimit(leftLimit: Int, topLimit: Int, rightLimit: Int, bottomLimit: Int) {
        mLeftLimit = leftLimit
        mTopLimit = topLimit
        mRightLimit = rightLimit
        mBottomLimit = bottomLimit
    }

    /** BoxOverlayView에서 Touch가 시작될 때 호출되는 함수
     * radius와 mRect, touch 좌표를 가지고 유효한 이벤트인지 판단? Type 선정 : return null
     * Type 선정
     *
     */
    fun onTouchDown(tX: Float, tY: Float): RectF? {
        isValidTouchEvent = isValidEvent(tX, tY)
        if(isValidTouchEvent) {
            updateTouchCoor(tX, tY)
            setTypeByTouchPointAndRect()
            Log.d(TAG, "onTouchDown: type ${mType.name}")
            return mRect
        }
        return null
    }

    /**
     * 새로운 Rect를 반환하기 위한 함수
     * BoxOverlayView에서 Touch가 이동할 때 호출되는 함수
     * isValidTouchEvent 와 mType으로 새로운 Rect 반환
     */

    fun onTouchMove(tX: Float, tY: Float): RectF? {
        if(isValidTouchEvent) {
            val dx = tX - mTouchX
            val dy = tY - mTouchY
            mRect = when(mType) {
                // 네 꼭짓점이 boundary를 벗어나지 않을 때만 drag 가능
                Type.DRAG -> {
                    return if(mRect.left + dx > mLeftLimit && mRect.right + dx < mRightLimit && mRect.top + dy > mTopLimit && mRect.bottom + dy < mBottomLimit) {
                        RectF(mRect.left + dx, mRect.top + dy, mRect.right + dx, mRect.bottom + dy)
                    } else {
                        mRect
                    }
                }
                Type.ADJUST_LEFT -> {
                    return if(isInLeftLimit(dx) && isInMinWidthOnLeft(dx)) {
                        RectF(mRect.left + dx, mRect.top, mRect.right, mRect.bottom)
                    } else {
                        mRect
                    }
                }
                Type.ADJUST_TOP -> {
                    return if(isInTopLimit(dy) && isInMinHeightOnTop(dy)) {
                        RectF(mRect.left, mRect.top + dy, mRect.right, mRect.bottom)
                    } else {
                        mRect
                    }
                }
                Type.ADJUST_RIGHT -> {
                    return if(isInRightLimit(dx) && isInMinWidthOnRight(dx)) {
                        RectF(mRect.left, mRect.top, mRect.right + dx, mRect.bottom)
                    } else {
                        mRect
                    }
                }
                Type.ADJUST_BOTTOM -> {
                    return if(isInBottomLimit(dy) && isInMinHeightOnBottom(dy)) {
                        RectF(mRect.left, mRect.top, mRect.right, mRect.bottom + dy)
                    } else {
                        mRect
                    }
                }
//                Type.ADJUST_TL -> {
//                    mRect
//                }
//                Type.ADJUST_TR ->{
//                    mRect
//                }
//                Type.ADJUST_BR -> {
//                    mRect
//                }
//                Type.ADJUST_BL -> {
//                    mRect
//                }
            }
            updateTouchCoor(tX, tY)
            return mRect
        }
        return null
    }

    private fun isInLeftLimit(dx: Float): Boolean = (mRect.left + dx > mLeftLimit)
    private fun isInTopLimit(dy: Float): Boolean = (mRect.top + dy > mTopLimit)
    private fun isInRightLimit(dx: Float): Boolean = (mRect.right + dx < mRightLimit)
    private fun isInBottomLimit(dy: Float): Boolean = (mRect.bottom + dy < mBottomLimit)

    private fun isInMinWidthOnLeft(dx: Float): Boolean = (mRect.left + dx < mRect.right - mMinWidth)
    private fun isInMinHeightOnTop(dy: Float): Boolean = (mRect.top + dy < mRect.bottom - mMinHeight)
    private fun isInMinWidthOnRight(dx: Float): Boolean = (mRect.right + dx > mRect.left + mMinWidth)
    private fun isInMinHeightOnBottom(dy: Float): Boolean = (mRect.bottom + dy > mRect.top + mMinHeight)

    /** 최신 정보인 mRect와 touchEvent 좌표, radius를 가지고 유효한 touchEvent인지 판단 */
    private fun isValidEvent(tX: Float, tY: Float): Boolean {
        return (tX >= mRect.left - radius && tX < mRect.right && tY >= mRect.top - radius && tY < mRect.bottom - radius)
    }

    private fun updateTouchCoor(tX: Float, tY: Float) {
        mTouchX = tX
        mTouchY = tY
    }

    /** 우선순위 1. 코너 2. 변 3. 안쪽 순으로 판단.
     * 1. 4코너의 범위 이내? -> adjust corner
     * 2. 4변의 범위 이내? -> adjust side
     * 3. drag
     *
     * |------------------|
     * |                  |
     * |    |-------|     |
     * |    |       |     |
     * |    |-------|     |
     * |                  |
     * |------------------|
     */

    private fun setTypeByTouchPointAndRect() {
        // 코너 판단을 위한 좌표
        val cornerX = arrayOf(mRect.left, mRect.right, mRect.right, mRect.left)
        val cornerY = arrayOf(mRect.top, mRect.top, mRect.bottom, mRect.bottom)
        val typeCorner = arrayOf(Type.ADJUST_TL, Type.ADJUST_TR, Type.ADJUST_BR, Type.ADJUST_BL)

        // 0:TL 1:TR 2:BR 3:BL
        for(i in 0..3) {
            if(isInRadius(cornerX[i] - radius, cornerX[i] + radius, cornerY[i] - radius, cornerY[i] + radius)) {
                mType = typeCorner[i]
                return
            }
        }

        // edge 판단
        if(isInRadius(mRect.left + radius, mRect.right - radius, mRect.top - radius, mRect.top + radius)) {
            mType = Type.ADJUST_TOP
            return
        }

        if(isInRadius(mRect.right - radius, mRect.right + radius, mRect.top + radius, mRect.bottom - radius)) {
            mType = Type.ADJUST_RIGHT
            return
        }

        if(isInRadius(mRect.left + radius, mRect.right - radius, mRect.bottom - radius, mRect.bottom + radius)) {
            mType = Type.ADJUST_BOTTOM
            return
        }

        if(isInRadius(mRect.left - radius, mRect.left + radius, mRect.top + radius, mRect.bottom - radius)) {
            mType = Type.ADJUST_LEFT
            return
        }

        // 드래그 판단
        if(isInRadius(mRect.left + radius, mRect.right - radius, mRect.top + radius, mRect.bottom - radius)) {
            mType = Type.DRAG
            return
        }
    }

    // 0:TL 1:TR 2:BR 3:BL
    private fun isInRadius(xMin: Float, xMax: Float, yMin: Float, yMax: Float): Boolean {
        return (mTouchX >= xMin && mTouchX < xMax && mTouchY >= yMin && mTouchY < yMin)
    }

    enum class Type {
        DRAG
        , ADJUST_LEFT
        , ADJUST_TOP
        , ADJUST_RIGHT
        , ADJUST_BOTTOM
        , ADJUST_TL
        , ADJUST_TR
        , ADJUST_BR
        , ADJUST_BL
    }
}