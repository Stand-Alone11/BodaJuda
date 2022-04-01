package com.aeye.nextlabel.feature.main.profile

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityUpdateBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.main.MainActivity
import com.aeye.nextlabel.feature.user.UserViewModel
import com.aeye.nextlabel.model.dto.UserForUpdate
import com.aeye.nextlabel.util.InputValidUtil
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UpdateActivity : BaseActivity<ActivityUpdateBinding>(ActivityUpdateBinding::inflate) {

    lateinit var absoluteUri: String

    val GALLERY_REQUEST_CODE = 1
    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {

        val toolBar = binding.topAppBar
        val profileImage = binding.imageProfile
        val btnGallery = binding.imgButtonGallery
        val btnEmailUpdate = binding.containedButtonUpdateEmail
        val btnNicknameUpdate = binding.containedButtonUpdateNickname
        val btnPasswordUpdate = binding.containedButtonUpdatePassword
        val btnSignout = binding.containedButtonSignOut

        // tool bar 뒤로가기 버튼 생성
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // profile image 원형으로 자르기
        Glide.with(this)
            .load(R.drawable.ic_default_profile_image).circleCrop().into(profileImage)

        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        // password update dialog
        btnPasswordUpdate.setOnClickListener { prepareDialog() }

        btnEmailUpdate.setOnClickListener {
            if (checkInputForm()) {
                update()
            }
        }

        btnNicknameUpdate.setOnClickListener {
            if (checkInputForm()) {
                update()
            }
        }

        btnSignout.setOnClickListener { signout() }
    }

    private fun update() {
        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

        userViewModel.update(UserForUpdate(email, nickname), absoluteUri)
    }

    private fun signout() {
        userViewModel.signout()

        val intent = Intent(this@UpdateActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkInputForm(): Boolean {
        var result = 1

        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

        if(!InputValidUtil.isValidEmail(email)) {
            result *= 0
            binding.email.error = resources.getText(R.string.emailErrorMessage)
        }
        if(!InputValidUtil.isValidNickname(nickname)) {
            result *= 0
            binding.nickname.error = resources.getText(R.string.nicknameErrorMessage)
        }

        return when(result) {
            1 -> true
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            val imgUri = data!!.data!!
            absoluteUri = makeAbsoluteUri(imgUri)

            binding.imageProfile.setImageURI(imgUri)
        }
    }

    fun makeAbsoluteUri(path: Uri?): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)

        return result!!
    }

    private fun prepareDialog() {

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.password_update_dialog_title))
            .setView(R.layout.fragment_dialog_password_update)
            .setNegativeButton(resources.getString(R.string.password_update_dialog_cancel)) { dialog, which ->
                Log.d("DIALOG_CANCEL", "canceled")
            }
            .setPositiveButton(resources.getString(R.string.password_update_dialog_ok)) { dialog, which ->
                Log.d("DIALOG_OK", "accepted")
            }
            .show()
    }
}