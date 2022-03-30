package com.aeye.nextlabel.feature.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentJoinBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.main.MainActivity
import com.aeye.nextlabel.global.BUNDLE_KEY_TO_MOVE
import com.aeye.nextlabel.global.FRAGMENT_BUNDLE_KEY
import com.aeye.nextlabel.global.LOGIN_FRAGMENT
import com.aeye.nextlabel.global.MOVE_FRAGMENT
import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.util.InputValidUtil
import com.aeye.nextlabel.util.Status

class JoinFragment : BaseFragment<FragmentJoinBinding>(FragmentJoinBinding::bind, R.layout.fragment_join) {
    private val userViewModel: UserViewModel by activityViewModels()
    lateinit var userForLogin: UserForLogin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initLiveDataObserver()
    }

    private fun init() {
        binding.textButtonLogin.setOnClickListener {
            setFragmentResult(MOVE_FRAGMENT, bundleOf(FRAGMENT_BUNDLE_KEY to LOGIN_FRAGMENT))
        }

        binding.containedButtonJoin.setOnClickListener {
            if (checkInputForm()) {
                join()
            }
        }
    }

    private fun initLiveDataObserver() {
        userViewModel.joinRequestLiveData.observe(requireActivity()) {
            when(it.status) {
                Status.SUCCESS -> {
                    // 회원가입에 성공하면 로그인 함께 수행
                    userViewModel.login(userForLogin)

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

    private fun join() {
        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()
        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

        // 로그인 준비
        userForLogin = UserForLogin(userId, password)
        userViewModel.join(UserForJoin(userId, password, email, nickname))
    }

    private fun checkInputForm(): Boolean {
        var result = 1

        val userId = binding.userId.text.toString()
        val password = binding.password.text.toString()
        val passwordConfirmation = binding.passwordConfirmation.text.toString()
        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

        if(!InputValidUtil.isValidUserId(userId)) {
            result *= 0
            binding.userId.error = resources.getText(R.string.userIdErrorMessage)
        }
        if(!InputValidUtil.isValidPassword(password)) {
            result *= 0
            binding.password.error = resources.getText(R.string.passwordErrorMessage)
        }
        if(password != passwordConfirmation) {
            result *= 0
            binding.passwordConfirmation.error = resources.getText(R.string.passwordConfirmErrorMessage)
        }
        if(!InputValidUtil.isValidEmail(email)) {
            result *= 0
            binding.email.error = resources.getText(R.string.emailErrorMessage)
        }
        if(!InputValidUtil.isValidNickname(nickname)) {
            result *= 0
            binding.nickname.error = resources.getText(R.string.nicknameErrorMessage)
        }

        return when(result) {
            1 -> true
            else -> false
        }
    }
}