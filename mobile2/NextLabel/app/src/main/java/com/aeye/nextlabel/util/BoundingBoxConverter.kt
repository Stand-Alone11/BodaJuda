package com.aeye.nextlabel.util

import kotlin.math.floor

object BoundingBoxConverter {
    fun convertToImageCoor(imageWidth: Int, imageHeight: Int, overlayViewWidth: Int, overlayViewHeight: Int, ltrb: Array<Float>): Array<Int> {
        val left = floor(ltrb[0].toDouble() / overlayViewWidth * imageWidth).toInt()
        val top = floor(ltrb[1].toDouble() / overlayViewHeight * imageHeight).toInt()
        val right = floor(ltrb[2].toDouble() / overlayViewWidth * imageWidth).toInt()
        val bottom = floor(ltrb[3].toDouble() / overlayViewHeight * imageHeight).toInt()
        return arrayOf(left, top, right, bottom)
    }
}