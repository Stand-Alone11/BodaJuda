package com.aeye.nextlabel.feature.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.ContainerFragmentBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.global.BUNDLE_KEY_TO_MOVE
import com.aeye.nextlabel.global.REQUEST_KEY_TO_MOVE

class ContainerFragment : BaseFragment<ContainerFragmentBinding>(ContainerFragmentBinding::bind, R.layout.container_fragment) {
    private lateinit var viewModel: ContainerViewModel
    private var fragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResultListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this)[ContainerViewModel::class.java]
        childFragmentManager.beginTransaction().add(R.id.frame_layout_container, CameraFragment()).commit()
    }

    private fun setResultListener() {
        childFragmentManager.setFragmentResultListener(REQUEST_KEY_TO_MOVE, requireActivity()) { key, bundle ->
            bundle.getString(BUNDLE_KEY_TO_MOVE)?.let {
                if(it == "NEXT") {
                    moveNext()
                }
            }
        }
    }

    // TODO: 적절한 프래그먼트 할당
    private fun moveNext() {
        when(fragmentIndex) {
            0 -> {
                childFragmentManager.beginTransaction().replace(R.id.frame_layout_container, CameraFragment()).addToBackStack(null).commit()
                fragmentIndex++
            }
            1 -> {
                childFragmentManager.beginTransaction().replace(R.id.frame_layout_container, CameraFragment()).addToBackStack(null).commit()
                fragmentIndex++
            }
            else -> {}
        }
    }

}