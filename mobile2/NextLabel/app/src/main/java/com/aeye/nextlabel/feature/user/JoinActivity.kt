package com.aeye.nextlabel.feature.user

import android.content.Intent
import android.os.Bundle
import com.aeye.nextlabel.databinding.ActivityJoinBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.main.MainActivity

class JoinActivity : BaseActivity<ActivityJoinBinding>(ActivityJoinBinding::inflate) {

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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}