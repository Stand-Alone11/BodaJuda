package com.aeye.nextlabel.feature.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aeye.nextlabel.databinding.ActivityMainBinding
import com.aeye.nextlabel.feature.common.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: FragmentStateAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        viewPager = binding.viewPagerMain
        viewPagerAdapter = MainViewPagerAdapter()
        viewPager.adapter = viewPagerAdapter

        tabLayout = binding.tabLayoutMain
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabLayoutText = arrayOf("프로젝트 설명", "기여하기")
            tab.text = tabLayoutText[position]
        }.attach()
    }

    inner class MainViewPagerAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int = 2

        // TODO: 뷰페이저 위치에 맞는 프래그먼트 세팅
        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> HomeFragment()
                else -> CameraFragment()
            }
        }
    }


//    fun settingHomeFragment() {
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.area_fragment, HomeFragment())
//        fragmentTransaction.commit()
//    }
//
//    fun settingButton() {
//        val labelingBtn = findViewById<Button>(R.id.btn_labeling)
//
//        labelingBtn?.setOnClickListener {
//            val fragmentTransaction = supportFragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.area_fragment, CameraFragment())
//            fragmentTransaction.commit()
//        }
//    }
//
//    fun settingNavBar() {
//        val homeTxt = findViewById<TextView>(R.id.nav_home)
//        val labelingTxt = findViewById<TextView>(R.id.nav_labeling)
//
//        homeTxt?.setOnClickListener {
//            val fragmentTransaction = supportFragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.area_fragment, HomeFragment())
//            fragmentTransaction.commit()
//        }
//        labelingTxt?.setOnClickListener {
//            val fragmentTransaction = supportFragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.area_fragment, CameraFragment())
//            fragmentTransaction.commit()
//        }
//    }
}