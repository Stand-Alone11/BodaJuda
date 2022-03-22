package com.aeye.nextlabel.temp

import android.os.Bundle
import com.aeye.nextlabel.databinding.TempFragmentBinding
import com.aeye.nextlabel.feature.common.BaseActivity

class TempMainActivity : BaseActivity<TempFragmentBinding>(TempFragmentBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {

    }

}