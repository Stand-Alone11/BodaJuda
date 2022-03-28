package com.aeye.bounding

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

/**
 * BoxImageView가 BoundBox를 설정할 영역을 만든다.
 * setImage는 필수
 */

class BoxImageView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr){

    private val TAG = "BoxImageView_debuk"

    private var exif: ExifInterface? = null

    /** image height */
    var imageLength: Int? = null

    /** image width */
    var imageWidth: Int? = null

    /** overlayViewWidth */
    private val mLimitLTRB = emptyArray<Int>()
    val limitTRB: Array<Int>
        get() = mLimitLTRB

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val v: View = inflater.inflate(R.layout.box_image_view, this, true)
    private var imageView: ImageView = v.findViewById(R.id.imageView_bound)
    private var mBoxOverlayView: BoxOverlayView = v.findViewById(R.id.boxOverlayView)

    private fun getExif(uri: Uri): ExifInterface? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            object: ExifInterface(inputStream!!) {}
        } catch (e: Exception) {
            Log.d(TAG, "getExif: ${e.message}")
            null
        }
    }

    fun setImage(uri: Uri) {
        imageView.setImageURI(uri)
        exif = getExif(uri)
        imageLength = exif?.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)?.toInt()
        imageWidth = exif?.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)?.toInt()
        Log.d(TAG, "setImage: width $imageWidth length $imageLength")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(TAG, "onLayout: $left, $top, $right, $bottom")

        setLimitArray(left, top, right, bottom)
        mBoxOverlayView.setLimits(left, top, right, bottom)
    }

    fun getRectCoor(): Array<Float> {
        val rect = mBoxOverlayView.getRect()
        return arrayOf(rect.left, rect.top, rect.right, rect.bottom)
    }

    private fun setLimitArray(left: Int, top: Int, right: Int, bottom: Int) {
        mLimitLTRB[0] = left
        mLimitLTRB[1] = top
        mLimitLTRB[2] = right
        mLimitLTRB[3] = bottom
    }
}