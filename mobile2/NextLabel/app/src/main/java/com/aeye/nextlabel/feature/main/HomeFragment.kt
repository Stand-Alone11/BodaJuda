package com.aeye.nextlabel.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aeye.nextlabel.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {

    val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
//    lateinit var activity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity = context as MainActivity
        return binding.root
    }
}