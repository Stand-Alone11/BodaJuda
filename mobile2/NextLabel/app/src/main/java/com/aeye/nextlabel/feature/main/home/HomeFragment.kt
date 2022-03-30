package com.aeye.nextlabel.feature.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentHomeBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: FragmentStateAdapter
    private lateinit var tabLayout: TabLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        viewPager = binding.viewPagerHome
        viewPagerAdapter = HomeViewPagerAdapter()
        viewPager.adapter = viewPagerAdapter

        tabLayout = binding.tabLayoutHome
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabLayoutText = arrayOf("프로젝트 설명", "기여하기")
            tab.text = tabLayoutText[position]
        }.attach()
    }

    inner class HomeViewPagerAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int = 2

        // TODO: 뷰페이저 위치에 맞는 프래그먼트 세팅
        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> DescriptionFragment()
                else -> CameraFragment()
            }
        }
    }
}