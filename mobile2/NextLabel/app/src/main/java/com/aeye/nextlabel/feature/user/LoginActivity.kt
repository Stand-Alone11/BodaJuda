package com.aeye.nextlabel.feature.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityLoginBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.main.MainActivity
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.util.InputValidUtil

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        val btnJoin = binding.textButtonJoin  // button join
        val btnLogin = binding.containedButtonLogin  // button login: move to home

        btnJoin.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            if (checkInputForm()) {
//                login()
            }
        }
    }

//    private fun login() {
//        val userId = binding.userId.text.toString()
//        val password = binding.password.text.toString()
//
//        userViewModel.login(UserForLogin(userId, password))
//    }

    private fun checkInputForm(): Boolean {
        var result = 1
        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()

        if(!InputValidUtil.isValidUserId(userId)) {
            result *= 0
            binding.userId.error = resources.getText(R.string.userIdErrorMessage)
        }
        if(!InputValidUtil.isValidPassword(password)) {
            result *= 0
            binding.password.error = resources.getText(R.string.passwordErrorMessage)
        }

        return when(result) {
            1 -> true
            else -> false
        }
    }
}