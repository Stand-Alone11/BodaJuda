package com.aeye.nextlabel.feature.main.profile

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityUpdateBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.main.MainActivity
import com.aeye.nextlabel.feature.user.LoginActivity
import com.aeye.nextlabel.feature.user.UserViewModel
import com.aeye.nextlabel.model.dto.Password
import com.aeye.nextlabel.model.dto.UserForUpdate
import com.aeye.nextlabel.model.dto.UserInfo
import com.aeye.nextlabel.util.InputValidUtil
import com.aeye.nextlabel.util.LoginUtil.getUserInfo
import com.aeye.nextlabel.util.LoginUtil.logout
import com.aeye.nextlabel.util.Status
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UpdateActivity : BaseActivity<ActivityUpdateBinding>(ActivityUpdateBinding::inflate) {

    val GALLERY_REQUEST_CODE = 1
    val BASE_URL = "https://storage.googleapis.com/thirdeye_profile"

    val userViewModel: UserViewModel by viewModels()

    lateinit var absoluteUri: String
    lateinit var userInfo: UserInfo
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        initLiveDataObserver()
    }

    private fun init() {

        val toolBar = binding.topAppBar
        val profileImage = binding.imageProfile
        val btnGallery = binding.imgButtonGallery
        val btnEmailUpdate = binding.containedButtonUpdateEmail
        val btnNicknameUpdate = binding.containedButtonUpdateNickname
        val btnPasswordUpdate = binding.containedButtonUpdatePassword
        val btnSignout = binding.containedButtonSignOut

        userInfo = getUserInfo()

        // tool bar 뒤로가기 버튼 생성
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // url 조합하기
        val profileUrlImg = BASE_URL + userInfo.imgUrl

        if (userInfo.imgUrl.isNullOrBlank()) {
            // profile image 원형으로 자르기
            Glide.with(this)
                .load(R.drawable.ic_default_profile_image).circleCrop().into(profileImage)
        } else {
            Glide.with(this)
                .load(profileUrlImg).circleCrop().into(profileImage)
        }
        
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        btnEmailUpdate.setOnClickListener { update() }
        btnNicknameUpdate.setOnClickListener { update() }

        // password update dialog
        btnPasswordUpdate.setOnClickListener { prepareDialog() }

        btnSignout.setOnClickListener { signout() }
    }

    private fun initLiveDataObserver() {
        userViewModel.leaveRequestLiveData.observe(this@UpdateActivity) {
            when(it.status) {
                Status.SUCCESS -> {
                    // 로그아웃 하면 로그인 페이지로 이동
                    logout()

                    val intent = Intent(this@UpdateActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                Status.LOADING -> {
                    // TODO: showLoading()
                }
                Status.ERROR -> {
                    // TODO: dismissLoading()
                }
            }
        }
    }

    // 프로필 사진
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            val imgUri = data!!.data!!
            absoluteUri = makeAbsoluteUri(imgUri)

            val profileImage = binding.imageProfile
            Glide.with(this).load(imgUri).circleCrop().into(profileImage)
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

    private fun update() {

        userInfo = getUserInfo()

        var email = userInfo.email
        var nickname = userInfo.nickname
        val newEmail = binding.email.text.toString()
        val newNickname = binding.nickname.text.toString()

        if (newEmail.length > 0) {
            email = newEmail
        }
        if (newNickname.length > 0) {
            nickname = newNickname
        }

        // 업데이트 하면 홈 화면으로 이동
        userViewModel.update(UserForUpdate(email!!, nickname!!), absoluteUri)

        val intent = Intent(this@UpdateActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun prepareDialog() {

        val view = layoutInflater.inflate(R.layout.fragment_dialog_password_update, null)
        val password = view.findViewById<EditText>(R.id.password_dialog).text.toString()
        val passwordConfirmation = view.findViewById<EditText>(R.id.password_confirmation_dialog).text.toString()

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.password_update_dialog_title))
            .setView(view)
            .setNegativeButton(resources.getString(R.string.password_update_dialog_cancel)) { dialog, which ->
                Log.d("DIALOG_OK", "canceled")
            }
            .setPositiveButton(resources.getString(R.string.password_update_dialog_ok)) { dialog, which ->
                userViewModel.updatePassword(Password(password, passwordConfirmation))
            }
            .show()
    }

    private fun signout() { userViewModel.signout() }
}