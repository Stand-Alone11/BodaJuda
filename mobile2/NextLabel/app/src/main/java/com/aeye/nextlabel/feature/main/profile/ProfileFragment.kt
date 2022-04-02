package com.aeye.nextlabel.feature.main.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentProfileBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.user.UserViewModel
import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.network.response.ProfileResponse
import com.aeye.nextlabel.util.LoginUtil
import com.aeye.nextlabel.util.Status
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {
    private val TAG = "ProfileFragment_debuk"
    private val userViewModel: UserViewModel by activityViewModels()
    lateinit var user: ProfileResponse
    private lateinit var pieChart: PieChart
    private lateinit var historyAdapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        initView()
        initLiveDataObserver()
        requestData()
    }

    private fun requestData() {
        LoginUtil.USER_ID?.let {
            userViewModel.getProfileFirst(it)
        }
    }

    private fun initView() {
        pieChart = binding.profilePiechart
        setupPieChart()

        historyAdapter = HistoryAdapter()
        binding.recyclerViewProfileF.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        binding.imageButtonProfileFEdit.setOnClickListener {
            startActivity(Intent(requireActivity(), UpdateActivity::class.java))
        }

        setToolbar()
    }

    private fun setToolbar() {
        binding.toolbarProfileF.apply {
            setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when(it.itemId) {
                    R.id.refresh_profile-> {
                        LoginUtil.USER_ID?.let { id ->
                            userViewModel.getProfile(id)
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initLiveDataObserver() {
        userViewModel.profileRequestLiveData.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.SUCCESS -> {
                    user = it.data!!
                    setUserData(user)
                }
                Status.ERROR -> {
                    // TODO: user profile error 처리
                }
                Status.LOADING -> {
                    // TODO: loading 처리
                }
            }
        }
    }

    private fun setUserData(data: ProfileResponse) {
        Log.d(TAG, "setUserData: ")
        val imageView = binding?.imageViewProfileF
        Glide.with(requireActivity()).load("${ApplicationClass.IMAGE_BASE_URL}${data.imgUrl}").circleCrop().into(imageView)
        binding.textViewProfileFName.text = data.nickname
        binding.textViewProfileFEmail.text = data.email

        loadPieChartData(data)
        loadRecentProject(data)
    }

    private fun loadRecentProject(data: ProfileResponse) {
        historyAdapter.setData(data.historyList)
    }

    private fun setupPieChart() {
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 80f
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isHighlightPerTapEnabled = true
        pieChart.setCenterTextSize(20f)

        // 범례 설정
        val legend = pieChart.getLegend()
        legend.isEnabled = false
    }

    private fun loadPieChartData(data: ProfileResponse) {
        // textView에 세팅
        binding.textViewProfileFAccepted.text = data.imageAccept.toString()
        binding.textViewProfileFWaiting.text = data.imageWait.toString()
        binding.textViewProfileFRejected.text = data.imageDeny.toString()
        binding.textViewProfileFSubmitted.text = data.imageTotal.toString()

        // 데이터 추가
        val entries = mutableListOf<PieEntry>()
        data.imageTotal.let {
            if(it != 0) {
                val acceptedRate = data.imageAccept.toFloat().div(it)
                val waitingRate = data.imageWait.toFloat().div(it)
                val deniedRate = data.imageDeny.toFloat().div(it).toFloat()

                entries.add(PieEntry(acceptedRate)) // 승인
                entries.add(PieEntry(waitingRate)) // 대기
                entries.add(PieEntry(deniedRate)) // 반려

                pieChart.centerText = "${(acceptedRate * 100).toInt()}%"
            }
        }

        // 색 템플릿 추가
        val colors = mutableListOf<Int>()
        colors.add(ContextCompat.getColor(requireContext(), R.color.approved_color))
        colors.add(ContextCompat.getColor(requireContext(), R.color.awaiting_color))
        colors.add(ContextCompat.getColor(requireContext(), R.color.denied_color))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors

        val chartData = PieData(dataSet)
        chartData.setDrawValues(false)
        pieChart.data = chartData

        pieChart.invalidate()
        // 애니메이션 추가
       pieChart.animateY(1400, Easing.EaseInOutQuad)
    }
}