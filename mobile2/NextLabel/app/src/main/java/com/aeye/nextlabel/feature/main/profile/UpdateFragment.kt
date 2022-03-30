package com.aeye.nextlabel.feature.main.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.activityViewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentUpdateBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.user.LoginActivity
import com.aeye.nextlabel.feature.user.UserViewModel
import com.aeye.nextlabel.model.dto.UserForUpdate
import com.aeye.nextlabel.util.InputValidUtil

class UpdateFragment : BaseFragment<FragmentUpdateBinding>(FragmentUpdateBinding::bind, R.layout.fragment_update) {

    lateinit var activity: LoginActivity
    lateinit var absoluteUri: String

    val GALLERY_REQUEST_CODE = 1
    val userViewModel: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = context as LoginActivity

        init()
    }

    private fun init() {
        val btnGallery = binding.imageButton
        val btnUpdate = binding.containedButtonUpdate
        val btnLeave = binding.containedButtonSignout

        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        btnUpdate.setOnClickListener {
            if (checkInputForm()) {
                update()
            }
        }

        btnLeave.setOnClickListener { signout() }
    }

    private fun update() {
        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

        userViewModel.update(UserForUpdate(email, nickname), absoluteUri)
    }

    private fun signout() = userViewModel.signout()

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

            val option = BitmapFactory.Options()
            option.inSampleSize = 4

            val inputStream = activity.contentResolver.openInputStream(imgUri)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
            inputStream!!.close()

            binding.imageView.setImageBitmap(bitmap)
        }
    }

    fun makeAbsoluteUri(path: Uri?): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = activity.contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)

        return result!!
    }
}