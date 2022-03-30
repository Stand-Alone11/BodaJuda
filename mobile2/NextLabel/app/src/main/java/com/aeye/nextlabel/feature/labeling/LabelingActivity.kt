package com.aeye.nextlabel.feature.labeling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.aeye.nextlabel.databinding.ActivityLabelingBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.util.BoundingBoxConverter

class LabelingActivity : BaseActivity<ActivityLabelingBinding>(ActivityLabelingBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        val imgUri = intent.getStringExtra("imgUri")?.toUri()

        imgUri?.let {
            binding.boxImageViewLabeling.setImage(it)
        }?: finish()


        binding.buttonLabelingSubmit.setOnClickListener {
            val boundingBoxCoor = setBoundingBox()
            val name = binding.editTextLabelingProductName.text.toString()
            val manufacturer = binding.editTextLabelingManufacturer.text.toString()
        }
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