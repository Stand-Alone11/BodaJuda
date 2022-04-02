package com.aeye.nextlabel.feature.camera

import android.os.Bundle
import androidx.activity.viewModels
import com.aeye.nextlabel.databinding.ActivityCameraBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.labeling.LabelingViewModel
import com.aeye.nextlabel.global.PROJECT_EXTRA
import com.aeye.nextlabel.model.dto.Project

class CameraActivity : BaseActivity<ActivityCameraBinding>(ActivityCameraBinding::inflate) {
    private val TAG = "CameraActivity_debuk"
    private val  labelingViewModel: LabelingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentExtra()
    }

    private fun getIntentExtra() {
        intent.getParcelableExtra<Project>(PROJECT_EXTRA)?.let {
            labelingViewModel.project = it
        }
    }
}