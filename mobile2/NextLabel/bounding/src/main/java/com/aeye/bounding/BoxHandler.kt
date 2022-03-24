package com.aeye.bounding

import android.graphics.RectF

/**
 * 바운딩 박스를 다루는 핸들러
 */

/**
 * @param rectF 바운딩 박스
 */
class BoxHandler(rectF: RectF, private val radius: Int) {

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