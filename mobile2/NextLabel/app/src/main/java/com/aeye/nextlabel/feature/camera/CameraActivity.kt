package com.aeye.nextlabel.feature.camera

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityCameraBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.labeling.LabelingViewModel
import com.aeye.nextlabel.feature.user.JoinFragment
import com.aeye.nextlabel.feature.user.LoginFragment
import com.aeye.nextlabel.global.*
import com.aeye.nextlabel.model.dto.Project

class CameraActivity : BaseActivity<ActivityCameraBinding>(ActivityCameraBinding::inflate) {
    private val TAG = "CameraActivity_debuk"
    private val  labelingViewModel: LabelingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentExtra()
        init()
    }

    private fun getIntentExtra() {
        intent.getParcelableExtra<Project>(PROJECT_EXTRA)?.let {
            labelingViewModel.project = it
        }
    }

    fun init() {
        setFragment()
        supportFragmentManager.setFragmentResultListener(MOVE_FRAGMENT, this) { _, bundle ->
            val transaction = supportFragmentManager.beginTransaction()
            when(bundle[FRAGMENT_BUNDLE_KEY]) {
                LABELING_FRAGMENT -> {
                    transaction.add(R.id.constraintLayout_camera, LabelingFragment()).addToBackStack(null).commit()
                }
            }
        }
    }
    private fun setFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.constraintLayout_camera, CameraFragment()).addToBackStack(null).commit()
        Log.d(TAG, "setFragment: ${supportFragmentManager.backStackEntryCount}")
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount < 2) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}