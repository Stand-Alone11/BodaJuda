package com.aeye.nextlabel.feature.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentLabelBinding
import com.aeye.nextlabel.feature.common.BaseFragment

class LabelFragment : BaseFragment<FragmentLabelBinding>(FragmentLabelBinding::bind, R.layout.fragment_label) {
    private val TAG = "LabelFragment_debuk"

    private val viewModel: ContainerViewModel by viewModels(ownerProducer = {requireParentFragment()})

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        viewModel.imageSavedUri?.let{
            binding.imageViewLabelF.setImageURI(it)
        }
    }
}