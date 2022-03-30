package com.aeye.nextlabel.feature.main.profile

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentProfileBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.user.UserViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::bind, R.layout.fragment_profile) {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var pieChart: PieChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        pieChart = binding.profilePiechart
        setupPieChart()
        loadPieChartData()
    }

    private fun initView() {

    }

    private fun setupPieChart() {
        pieChart.setDrawHoleEnabled(true)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.getDescription().setEnabled(false)
        pieChart.setHighlightPerTapEnabled(true)
        pieChart.setCenterText("승인율")
        pieChart.setCenterTextSize(20f)

        // 범례 설정
        val legend = pieChart.getLegend()
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
//        legend.setOrientation(Legend.LegendOrientation.VERTICAL)
//        legend.setDrawInside(false)
        legend.setEnabled(false)

    }

    private fun loadPieChartData() {
        // 데이터 추가
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(0.6f, "승인"))
        entries.add(PieEntry(0.4f, "거부"))

        // 색 템플릿 추가
        val colors = mutableListOf<Int>()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(colors)

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        pieChart.setData(data)
        pieChart.invalidate()
        // 애니메이션 추가
       pieChart.animateY(1400, Easing.EaseInOutQuad)
    }
}