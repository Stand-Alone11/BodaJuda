package com.aeye.nextlabel.temp

import android.os.Bundle
import com.aeye.nextlabel.databinding.TempFragmentLabelingBinding
import com.aeye.nextlabel.feature.common.BaseActivity

class TempMainActivity : BaseActivity<TempFragmentLabelingBinding>(TempFragmentLabelingBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {

    }

}