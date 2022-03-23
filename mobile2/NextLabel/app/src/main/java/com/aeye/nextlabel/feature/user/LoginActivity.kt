package com.aeye.nextlabel.feature.user

import android.os.Bundle
import com.aeye.nextlabel.databinding.ActivityLoginBinding
import com.aeye.nextlabel.feature.common.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {

    }
}