package com.aeye.bounding

import android.content.Context
import android.view.View

class BoxOverlayView @JvmOverloads constructor(context: Context): View(context) {
    private val mMoveHandler: BoxHandler by lazy { BoxHandler() }



}