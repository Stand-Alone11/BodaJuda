package com.aeye.nextlabel.feature.main.profile

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
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

        // 승인율 파이 차트
        pieChart = binding.profilePiechart
        setupPieChart()
        loadPieChartData()

        // 최근 프로젝트
        val card1 = binding.cardProfileProject1
        val card2 = binding.cardProfileProject2
        val card3 = binding.cardProfileProject3
        val card4 = binding.cardProfileProject4
        val card5 = binding.cardProfileProject5
        val snack1 = binding.textViewProfileProjectSnack1
        val snack2 = binding.textViewProfileProjectSnack2
        val snack3 = binding.textViewProfileProjectSnack3
        val snack4 = binding.textViewProfileProjectSnack4
        val snack5 = binding.textViewProfileProjectSnack5
        val target1 = binding.textViewProfileProjectTarget1
        val target2 = binding.textViewProfileProjectTarget2
        val target3 = binding.textViewProfileProjectTarget3
        val target4 = binding.textViewProfileProjectTarget4
        val target5 = binding.textViewProfileProjectTarget5
        val progressBar1 = binding.progressBarProfile1
        val progressBar2 = binding.progressBarProfile2
        val progressBar3 = binding.progressBarProfile3
        val progressBar4 = binding.progressBarProfile4
        val progressBar5 = binding.progressBarProfile5

        // 기본값은 안보이기
        card1.isVisible = false
        card2.isVisible = false
        card3.isVisible = false
        card4.isVisible = false
        card5.isVisible = false

        // 설정
        card1.isVisible = true
        snack1.text = "꼬깔콘"
        target1.text = "500"
        progressBar1.progress = 95

//        card2.isVisible = true
        snack2.text = "새우깡"
        target2.text = "600"
        progressBar2.progress = 80

        card3.isVisible = true
        snack3.text = "매운새우깡"
        target3.text = "700"
        progressBar3.progress = 70

        card4.isVisible = true
        snack4.text = "허니버터칩"
        target4.text = "800"
        progressBar4.progress = 50

        card5.isVisible = true
        snack5.text = "꼬북칩"
        target5.text = "900"
        progressBar5.progress = 30

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