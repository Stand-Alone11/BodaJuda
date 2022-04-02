package com.aeye.nextlabel.feature.camera

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentLabelingBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.labeling.LabelingViewModel
import com.aeye.nextlabel.model.dto.Label
import com.aeye.nextlabel.util.BoundingBoxConverter
import com.aeye.nextlabel.util.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.Exception

class LabelingFragment : BaseFragment<FragmentLabelingBinding>(FragmentLabelingBinding::bind, R.layout.fragment_labeling) {
    private val TAG = "LabelingActivity_debuk"
    private val labelingViewModel: LabelingViewModel by activityViewModels()
    private var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initLiveDataObserver()
    }

    private fun prepareDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("${labelingViewModel.project?.provider} ${labelingViewModel.project?.title}")
            .setMessage("사진을 전송하시겠습니까?")
            .setPositiveButton("전송") { dialog, which ->
                Log.d("DIALOG_SUBMIT", "submited")
                uploadLabel()
            }
            .show()
    }

    private fun init() {
        val imgUri = labelingViewModel.imageUrl
        Log.d(TAG, "init: ${imgUri}")

        imgUri?.let {
            binding.boxImageViewLabeling.setImage(it)
        }?: requireActivity().finish()

        labelingViewModel.project?: requireActivity().finish()

        setToolbar()
    }

    private fun setToolbar() {
        binding.toolbarLabeling.apply {
            setNavigationIcon(R.drawable.ic_back)

            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }

            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.label_submit -> {
                        prepareDialog()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun showLoading() {
        isLoading = true
        try {
            binding.frameLayoutLabelingLoading.visibility = View.VISIBLE    
        } catch (e: Exception) {
            Log.d(TAG, "showLoading: $e")
        }
    }

    private fun dismissLoading() {
        isLoading = false
        binding.frameLayoutLabelingLoading.visibility = View.GONE
    }

    private fun initLiveDataObserver() {
        labelingViewModel.uploadLabelResponse.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.SUCCESS -> {
                    updateUiOnSuccess()
                    Log.d(TAG, "initLiveDataObserver: upload success")
                }
                Status.LOADING -> {
                    showLoading()
                }
                Status.ERROR -> {
                    updateUiOnError()
                    Log.d(TAG, "initLiveDataObserver: upload fail")
                }
            }
        }
    }

    private fun updateUiOnSuccess() {
        if(isLoading) {
            dismissLoading()
            Toast.makeText(requireContext(), "소중한 데이터를 제공해 주셔서 감사합니다.", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
        }
    }

    private fun updateUiOnError() {
        if(isLoading) {
            dismissLoading()
            Toast.makeText(requireContext(), "전송에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadLabel() {
        val imgUri = labelingViewModel.imageUrl
        val boundingBoxCoor = setBoundingBox() // ltrb

        labelingViewModel.project?.let { project ->
            getAbsolutePath(imgUri!!)?.let {
                Log.d("확인", "업로드 수행")
                val label = Label(project.title, project.provider, boundingBoxCoor[0], boundingBoxCoor[1], boundingBoxCoor[2], boundingBoxCoor[3])
                labelingViewModel.uploadLabel(label, it, project.id)
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