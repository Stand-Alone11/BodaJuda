package com.aeye.nextlabel.feature.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentLabelBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.util.BoundingBoxConverter

class LabelFragment : BaseFragment<FragmentLabelBinding>(FragmentLabelBinding::bind, R.layout.fragment_label) {
    private val TAG = "LabelFragment_debuk"

    private val viewModel: ContainerViewModel by viewModels(ownerProducer = {requireParentFragment()})

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        viewModel.imageSavedUri?.let{
            binding.boxImageViewLabelF.setImage(it)
        }

        binding.buttonLabelFSubmit.setOnClickListener {
            val boundingBoxCoor = setBoundingBox()
            Log.d(TAG, "init: ${boundingBoxCoor}")
            val name = binding.editTextLabelFProductName.text.toString()
            val manufacturer = binding.editTextLabelFManufacturer.text.toString()
        }
    }

    private fun setBoundingBox(): Array<Int> {
        val ltrb = binding.boxImageViewLabelF.getRectCoor()
        val imageWidth = binding.boxImageViewLabelF.imageWidth
        val imageHeight = binding.boxImageViewLabelF.imageLength
        val viewLTRB = binding.boxImageViewLabelF.limitTRB
        if (imageWidth != null && imageHeight != null) {
            return BoundingBoxConverter.convertToImageCoor(imageWidth, imageHeight, viewLTRB[2] - viewLTRB[0], viewLTRB[3] - viewLTRB[1], ltrb)
        }
        return emptyArray()
    }
}