package com.aeye.nextlabel.feature.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityJoinBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.main.MainActivity
import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.util.InputValidUtil

class JoinActivity : BaseActivity<ActivityJoinBinding>(ActivityJoinBinding::inflate) {

    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        val btnLogin = binding.textButtonLogin  // button login
        val btnJoin = binding.containedButtonJoin  // button join: move to home

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnJoin.setOnClickListener {
            if (checkInputForm()) {
                join()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun join() {
        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()
        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

        userViewModel.join(UserForJoin(userId, password, email, nickname))
    }

    private fun checkInputForm(): Boolean {
        var result = 1

        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()
        val passwordConfirmation = binding.passwordConfirmation.text.toString()
        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

        if(!InputValidUtil.isValidUserId(userId)) {
            result *= 0
            binding.userId.error = resources.getText(R.string.userIdErrorMessage)
        }
        if(!InputValidUtil.isValidPassword(password)) {
            result *= 0
            binding.password.error = resources.getText(R.string.passwordErrorMessage)
        }
        if(password != passwordConfirmation) {
            result *= 0
            binding.passwordConfirmation.error = resources.getText(R.string.passwordConfirmErrorMessage)
        }
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
}