package com.aeye.nextlabel.feature.user

import android.os.Bundle
import com.aeye.nextlabel.databinding.ActivityProfileBinding
import com.aeye.nextlabel.feature.common.BaseActivity

class ProfileActivity : BaseActivity<ActivityProfileBinding>(ActivityProfileBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {

    }
}