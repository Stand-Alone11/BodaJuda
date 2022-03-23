package com.aeye.nextlabel.feature.main

import android.os.Bundle
import android.view.View
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentHomeBinding
import com.aeye.nextlabel.feature.common.BaseFragment

class HomeFragment: BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

    }
}