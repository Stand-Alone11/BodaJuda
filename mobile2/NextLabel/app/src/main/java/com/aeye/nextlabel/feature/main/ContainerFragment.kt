package com.aeye.nextlabel.feature.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ContainerFragmentBinding
import com.aeye.nextlabel.feature.common.BaseFragment

class ContainerFragment : BaseFragment<ContainerFragmentBinding>(ContainerFragmentBinding::bind, R.layout.container_fragment) {
    private lateinit var viewModel: ContainerViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ContainerViewModel::class.java]
    }
}