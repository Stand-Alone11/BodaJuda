package com.aeye.nextlabel.util

object InputValidUtil {
    val idRegex = "^(?=.*[a-zA-Z])(?=.*[0-9]).{2,20}$".toRegex()
    val emailRegex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$".toRegex()
    val nicknameRegex = "^[가-힣a-zA-Z]{2,8}$".toRegex()

    val passRegex1 = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,14}$".toRegex()  // 영문, 숫자
    val passRegex2 = "^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,14}$".toRegex()  //영문, 특문
    val passRegex3 = "^(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,14}$".toRegex()  //특문, 숫자
    val passRegexes = listOf(passRegex1, passRegex2, passRegex3)

    fun isValidUserId(userId: String): Boolean {
        val result = userId.matches(idRegex)
        return result
    }

    fun isValidEmail(email: String): Boolean {
        val result = email.matches(emailRegex)
        return result
    }

    fun isValidNickname(nickname: String): Boolean {
        val result = nickname.matches(nicknameRegex)
        return result
    }

    fun isValidPassword(password: String): Boolean {
        for(passRegex in passRegexes) {
            if (password.matches(passRegex))
                return true
        }
        return false
    }
}