package com.aeye.nextlabel.feature.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentProfileBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.user.UserViewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

    }
}