package com.aeye.nextlabel.feature.user

import android.os.Bundle
import android.util.Log
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityUpdateBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UpdateActivity : BaseActivity<ActivityUpdateBinding>(ActivityUpdateBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        val toolBar = binding.topAppBar
        val profileImage = binding.profileImage
        val btnPasswordUpdate = binding.containedButtonUpdatePassword

        // tool bar 뒤로가기 버튼 생성
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // profile image 원형으로 자르기
        Glide.with(this)
            .load(R.drawable.ic_default_profile_image_without_circle).circleCrop().into(profileImage)

        // password update dialog
        btnPasswordUpdate.setOnClickListener { prepareDialog() }
    }

    private fun prepareDialog() {

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.password_update_dialog_title))
            .setView(R.layout.fragment_dialog_password_update)
            .setNegativeButton(resources.getString(R.string.password_update_dialog_cancel)) { dialog, which ->
                Log.d("DIALOG_CANCEL", "canceled")
            }
            .setPositiveButton(resources.getString(R.string.password_update_dialog_cancel)) { dialog, which ->
                Log.d("DIALOG_OK", "accepted")
            }
            .show()
    }
}