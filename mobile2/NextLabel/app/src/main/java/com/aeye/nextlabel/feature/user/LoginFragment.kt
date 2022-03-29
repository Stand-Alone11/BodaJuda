package com.aeye.nextlabel.feature.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentLoginBinding
import com.aeye.nextlabel.feature.common.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private val userViewModel: UserViewModel by activityViewModels()


}