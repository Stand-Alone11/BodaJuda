package com.aeye.nextlabel.feature.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentLoginBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.main.MainActivity
import com.aeye.nextlabel.feature.main.profile.UpdateActivity
import com.aeye.nextlabel.global.FRAGMENT_BUNDLE_KEY
import com.aeye.nextlabel.global.JOIN_FRAGMENT
import com.aeye.nextlabel.global.MOVE_FRAGMENT
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.util.InputValidUtil
import com.aeye.nextlabel.util.LoginUtil.USER_ID
import com.aeye.nextlabel.util.Status

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
//        initLiveDataObserver()
    }

    private fun init() {
        binding.textButtonSignUp.setOnClickListener {
            setFragmentResult(MOVE_FRAGMENT, bundleOf(FRAGMENT_BUNDLE_KEY to JOIN_FRAGMENT))
        }

        binding.containedButtonLogIn.setOnClickListener {
//            if (checkInputForm()) {
//                login()
//            }

            // test: 화면 테스트를 위해 로그인 버튼을 누르면 업데이트 액티비티로 이동(라이브데이터옵저버 주석처리)
            val intent = Intent(requireActivity(), UpdateActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
    
    private fun initLiveDataObserver() {
        // 로그인 요청 결과에 따른 ui 변경 
        userViewModel.loginRequestLiveData.observe(requireActivity()) {
            when(it.status) {
                Status.SUCCESS -> {
                    // 프로필 정보 저장
                    userViewModel.getProfile(USER_ID!!)

                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                Status.LOADING -> {
                    // TODO: showLoading() 
                }
                Status.ERROR -> {
                    // TODO: dismissLoading() 
                }
            }
        }
    }

    private fun login() {
        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()

        userViewModel.login(UserForLogin(userId, password))
    }

    private fun checkInputForm(): Boolean {
        var result = 1

        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()

        if(!InputValidUtil.isValidUserId(userId)) {
            result *= 0
            binding.userId.error = resources.getText(R.string.userIdErrorMessage)
        }
        if(!InputValidUtil.isValidPassword(password)) {
            result *= 0
            binding.password.error = resources.getText(R.string.passwordErrorMessage)
        }

        return when(result) {
            1 -> true
            else -> false
        }
    }
}