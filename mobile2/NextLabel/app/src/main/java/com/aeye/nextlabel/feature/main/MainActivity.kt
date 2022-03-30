package com.aeye.nextlabel.feature.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ActivityMainBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView_main)
        val navController = navHostFragment?.findNavController()
        navController?.let {
            binding.bottomNavMain.setupWithNavController(it)
        }
    }

}