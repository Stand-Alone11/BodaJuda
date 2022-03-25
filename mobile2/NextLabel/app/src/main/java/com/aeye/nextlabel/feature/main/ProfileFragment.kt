package com.aeye.nextlabel.feature.main

import android.os.Bundle
import android.view.View
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentProfileBinding
import com.aeye.nextlabel.feature.common.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

    }
}