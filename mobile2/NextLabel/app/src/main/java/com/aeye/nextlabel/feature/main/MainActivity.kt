package com.aeye.nextlabel.feature.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityMainBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.aeye.nextlabel.feature.main.home.ProjectViewModel
import com.aeye.nextlabel.feature.user.LoginActivity
import com.aeye.nextlabel.feature.user.UserViewModel
import com.aeye.nextlabel.util.LoginUtil
import com.aeye.nextlabel.util.LoginUtil.isLogin
import com.aeye.nextlabel.util.LoginUtil.logout

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val projectViewModel: ProjectViewModel by viewModels()
    private val leaderBoardViewModel: CommunityViewModel by viewModels()
    private val profileViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 로그인 상태가 아니라면, 로그인 먼저 요청
       if (isLogin()) {
            init()
        } else {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView_main)
        val navController = navHostFragment?.findNavController()
        navController?.let {
            binding.bottomNavMain.setupWithNavController(it)
        }
        LoginUtil.getUserId()
    }
}