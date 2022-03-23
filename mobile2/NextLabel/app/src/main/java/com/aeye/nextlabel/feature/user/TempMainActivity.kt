package com.aeye.nextlabel.feature.user

import android.os.Bundle
import androidx.activity.viewModels
import com.aeye.nextlabel.databinding.TempFragmentBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.model.dto.UserJoin
import com.aeye.nextlabel.feature.user.UserViewModel

class TempMainActivity : BaseActivity<TempFragmentBinding>(TempFragmentBinding::inflate) {

    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {

    }

    private fun join() {
        val id = binding.editId.text.toString()
        val password = binding.editPw.text.toString()
        val passwordConformation = binding.editPwConfirmation.text.toString()
        val email = binding.editEmail.text.toString()
        val nickname = binding.editNickname.text.toString()

        userViewModel.join(UserJoin(id, password, passwordConformation, email, nickname))
    }
}