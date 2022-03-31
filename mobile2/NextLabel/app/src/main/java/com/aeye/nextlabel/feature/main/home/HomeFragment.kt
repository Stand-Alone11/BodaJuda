package com.aeye.nextlabel.feature.main.home

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentHomeBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.util.Status
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {
    private lateinit var projectAdapter: ProjectAdapter
    private val projectViewModel: ProjectViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initLiveDataObserver()
        requestProjects()
    }

    private fun requestProjects() {
        if(!projectViewModel.isFirstLoaded) {
            projectViewModel.getProject()
        }
    }

    private fun init() {
        // TODO: Adapter ClickListener 구현
        projectAdapter = ProjectAdapter()
        binding.recyclerViewHomeF.apply {
            adapter = projectAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(object: RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    if(parent.getChildAdapterPosition(view) == 0) {
                        outRect.top = outRect.top + 8.dp
                    }
                }
            })
        }
    }

    private fun initLiveDataObserver() {
        projectViewModel.projectResponseLiveData.observe(requireActivity()) {
            when(it.status) {
                Status.LOADING -> {
                    // TODO: showLoading
                }
                else -> {
                    // TODO: dismissLoading
                }
            }
        }

        projectViewModel.projectListLiveData.observe(requireActivity()) {
            // TODO: dismissLoading
            projectAdapter.addProject(it)
        }
    }

    val Int.dp: Int
        get() {
            val metrics = resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
        }
}