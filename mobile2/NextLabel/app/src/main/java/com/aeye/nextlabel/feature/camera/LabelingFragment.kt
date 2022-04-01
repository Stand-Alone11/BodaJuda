package com.aeye.nextlabel.feature.camera

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentLabelingBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.labeling.LabelingViewModel
import com.aeye.nextlabel.util.BoundingBoxConverter
import com.aeye.nextlabel.util.Status
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LabelingFragment : BaseFragment<FragmentLabelingBinding>(FragmentLabelingBinding::bind, R.layout.fragment_labeling) {
    private val TAG = "LabelingActivity_debuk"
    private val labelingViewModel: LabelingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        val btnSubmit = binding.imageButtonLabelingSubmit
        btnSubmit.setOnClickListener { prepareDialog() }
    }

    private fun prepareDialog() {
        val imgUri = labelingViewModel.imageUrl

        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("과자이름")
            .setMessage("사진을 전송하시겠습니까?")
            .setPositiveButton("전송") { dialog, which ->
                Log.d("DIALOG_SUBMIT", "submited")
                val boundingBoxCoor = setBoundingBox() // ltrb
                getAbsolutePath(imgUri!!)?.let {
                    Log.d("확인", "업로드 수행")
//                labelingViewModel.uploadLabel(Label(name, manufacturer, boundingBoxCoor[0], boundingBoxCoor[1], boundingBoxCoor[2], boundingBoxCoor[3]), it)
                }
            }
            .show()
    }

    private fun init() {
        val imgUri = labelingViewModel.imageUrl
        Log.d(TAG, "init: ${imgUri}")

        imgUri?.let {
            binding.boxImageViewLabeling.setImage(it)
        }?: return
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
        val cursor = requireActivity().contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DATA), null, null, null)
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