package com.aeye.nextlabel.feature.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentUpdateBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.model.dto.Password
import com.aeye.nextlabel.model.dto.UserForUpdate
import com.aeye.nextlabel.util.InputValidUtil

class UpdateFragment : BaseFragment<FragmentUpdateBinding>(FragmentUpdateBinding::bind, R.layout.fragment_update) {

    val userViewModel: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val btnUpdate = binding.containedButtonUpdate
        val btnLeave = binding.containedButtonSignout

        btnUpdate.setOnClickListener {
            if (checkInputForm()) {
                update()
            }
        }

        btnLeave.setOnClickListener { signout() }
    }

    private fun update() {
        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

        userViewModel.update(UserForUpdate(email, nickname))
    }

    private fun signout() = userViewModel.signout()

    private fun checkInputForm(): Boolean {
        var result = 1

        val email = binding.email.text.toString()
        val nickname = binding.nickname.text.toString()

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