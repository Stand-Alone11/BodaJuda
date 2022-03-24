package com.aeye.bounding

import android.graphics.RectF

/**
 * 바운딩 박스를 다루는 핸들러
 */

/**
 * @param rectF 바운딩 박스
 */
class BoxHandler(rectF: RectF, private val radius: Int) {

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