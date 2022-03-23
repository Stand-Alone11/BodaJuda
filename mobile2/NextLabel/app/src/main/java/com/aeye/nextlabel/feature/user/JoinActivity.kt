package com.aeye.nextlabel.feature.user

import android.content.Intent
import android.os.Bundle
import com.aeye.nextlabel.databinding.ActivityJoinBinding
import com.aeye.nextlabel.feature.common.BaseActivity

class JoinActivity : BaseActivity<ActivityJoinBinding>(ActivityJoinBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        // button login
        val btnLogin = binding.btnLogin

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}