package com.aeye.nextlabel.feature.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityMainBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.user.LoginActivity
import com.aeye.nextlabel.util.LoginUtil.isLogin

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView_main)
        val navController = navHostFragment?.findNavController()
        navController?.let {
            // TODO: 로그인 상태라면 이동 허용, otherwise 로그인 요청
            if (isLogin()) {
                binding.bottomNavMain.setupWithNavController(it)
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}