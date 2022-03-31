package com.aeye.nextlabel.feature.labeling

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.aeye.nextlabel.databinding.ActivityLabelingBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.model.dto.Label
import com.aeye.nextlabel.util.BoundingBoxConverter
import com.aeye.nextlabel.util.Status

class LabelingActivity : BaseActivity<ActivityLabelingBinding>(ActivityLabelingBinding::inflate) {
    private val TAG = "LabelingActivity_debuk"
    private val labelingViewModel: LabelingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        initLiveDataObserver()
    }

    private fun init() {
        val imgUri = intent.getParcelableExtra<Uri>("imgUri")
        Log.d(TAG, "init: ${imgUri}")

        imgUri?.let {
            binding.boxImageViewLabeling.setImage(it)
        }?: finish()

//        binding.buttonLabelingSubmit.setOnClickListener {
//            val boundingBoxCoor = setBoundingBox() // ltrb
//            val name = binding.editTextLabelingProductName.text.toString()
//            val manufacturer = binding.editTextLabelingManufacturer.text.toString()
//            getAbsolutePath(imgUri!!)?.let {
//                labelingViewModel.uploadLabel(Label(name, manufacturer, boundingBoxCoor[0], boundingBoxCoor[1], boundingBoxCoor[2], boundingBoxCoor[3]), it)
//            }
//        }
    }

    private fun initLiveDataObserver() {
        labelingViewModel.uploadLabelResponse.observe(this) {
            when(it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "initLiveDataObserver: upload success")
                }
                Status.LOADING -> {
                    // TODO: showloading
                }
                Status.ERROR -> {
                    Log.d(TAG, "initLiveDataObserver: upload fail")
                }
            }
        }
    }

    private fun getAbsolutePath(uri: Uri): String? {
        val cursor = contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DATA), null, null, null)
        var absolutPath: String? = null
        cursor?.use {
            if(it.moveToFirst()) absolutPath = it.getString(0)
        }
        return absolutPath
    }

    private fun setBoundingBox(): Array<Int> {
        val ltrb = binding.boxImageViewLabeling.getRectCoor()
        val imageWidth = binding.boxImageViewLabeling.imageWidth
        val imageHeight = binding.boxImageViewLabeling.imageLength
        val viewLTRB = binding.boxImageViewLabeling.limitTRB
        if (imageWidth != null && imageHeight != null) {
            return BoundingBoxConverter.convertToImageCoor(imageWidth, imageHeight, viewLTRB[2] - viewLTRB[0], viewLTRB[3] - viewLTRB[1], ltrb)
        }
        return emptyArray()
    }
}