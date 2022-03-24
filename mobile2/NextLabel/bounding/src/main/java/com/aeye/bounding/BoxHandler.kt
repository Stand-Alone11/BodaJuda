package com.aeye.bounding

/**
 * 바운딩 박스를 다루는 핸들러
 */

/**
 * @param box 바운딩 박스
 * @param touchX 터치 이벤트의 x 좌표
 * @param touchY 터치 이벤트의 y 좌표
 */
class BoxHandler(box: Box, touchX: Float, touchY: Float) {
    /** 상단 왼쪽 좌표 first:x, second:y*/
    private val boxPointTL = Pair(0F, 0F)

    /** 상단 오른쪽 좌표 */
    private val boxPointTR = Pair(0F, 0F)

    /** 하단 왼쪽 좌표 */
    private var pointBL = Pair(0F, 0F)

    /** 상단 오른쪽 좌표 */
    private var pointBR = Pair(0F, 0F)



}