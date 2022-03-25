package com.aeye.nextlabel.feature.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.aeye.nextlabel.databinding.ActivityLoginBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.main.MainActivity
import com.aeye.nextlabel.model.dto.UserForLogin

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
        }
    }

    private fun login() {
        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()

        userViewModel.login(UserForLogin(userId, password))
    }
}