package com.aeye.nextlabel.feature.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.HistoryItemBinding
import com.aeye.nextlabel.model.dto.History
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class HistoryAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = listOf<History>()

    inner class ViewHolder(val binding: HistoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.project = history.project
            binding.history = history
            setPieChart(history)
        }

        private fun setPieChart(history: History) {
            val pieChart = binding.pieChartHistoryItem
            pieChart.isDrawHoleEnabled = true
            pieChart.holeRadius = 80f
            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.isHighlightPerTapEnabled = true
            pieChart.setCenterTextSize(12f)

            // 데이터 추가
            val entries = mutableListOf<PieEntry>()

            entries.add(PieEntry(history.accepted.toFloat() / history.total)) // 승인
            entries.add(PieEntry(1 - history.accepted.toFloat() / history.total)) // 나머지

            pieChart.centerText = "${(history.accepted.toFloat() / history.total * 100).toInt()}%"

            // 색 템플릿 추가
            val colors = mutableListOf<Int>()
            colors.add(ContextCompat.getColor(binding.root.context, R.color.approved_color))
            colors.add(ContextCompat.getColor(binding.root.context, R.color.awaiting_color))

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = colors

            val chartData = PieData(dataSet)
            chartData.setDrawValues(false)
            pieChart.data = chartData

            val legend = pieChart.getLegend()
            legend.isEnabled = false

            pieChart.invalidate()
            // 애니메이션 추가
            pieChart.animateY(1400, Easing.EaseInOutQuad)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    fun setData(list: List<History>) {
        items = list
        notifyDataSetChanged()
    }
}