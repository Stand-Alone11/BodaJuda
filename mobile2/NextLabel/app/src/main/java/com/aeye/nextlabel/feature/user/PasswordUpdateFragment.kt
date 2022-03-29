package com.aeye.nextlabel.feature.user

import android.os.Bundle
import android.view.View
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentHomeBinding
import com.aeye.nextlabel.databinding.FragmentPasswordUpdateBinding
import com.aeye.nextlabel.feature.common.BaseFragment

class PasswordUpdateFragment : BaseFragment<FragmentPasswordUpdateBinding>(
    FragmentPasswordUpdateBinding::bind, R.layout.fragment_password_update
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

    }
}