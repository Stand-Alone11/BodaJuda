package com.aeye.nextlabel.feature.main.home

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentHomeBinding
import com.aeye.nextlabel.feature.camera.CameraActivity
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.global.PROJECT_EXTRA
import com.aeye.nextlabel.model.dto.Project
import com.aeye.nextlabel.util.Status

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
        projectAdapter = ProjectAdapter().apply {
            itemClickListener = object: ProjectAdapter.ItemClickListener {
                override fun onClick(view: View) {
                    startCameraActivity(view.tag as Project)
                }
            }
        }
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

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = recyclerView.adapter!!.itemCount-1

                    // 스크롤이 끝에 도달했는지 확인
                    if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL) && lastVisibleItemPosition == itemTotalCount) {
                        projectViewModel.getProject()
                    }
                }
            })
        }
    }

    private fun startCameraActivity(project: Project) {
        val intent = Intent(requireActivity(), CameraActivity::class.java).apply {
            putExtra(PROJECT_EXTRA, project)
        }
        startActivity(intent)
    }

    private fun initLiveDataObserver() {
        projectViewModel.projectResponseLiveData.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.LOADING -> {
                    // TODO: showLoading
                }
                else -> {
                    // TODO: dismissLoading
                }
            }
        }

        projectViewModel.projectListLiveData.observe(viewLifecycleOwner) {
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