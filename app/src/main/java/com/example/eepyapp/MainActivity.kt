package com.example.eepyapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val barChart = findViewById<BarChart>(R.id.sleepGraph)

        // Ambil data dari intent
        val sleepData = intent.getSerializableExtra("sleepData") as? HashMap<String, Int> ?: hashMapOf()

        // Ubah data ke dalam format BarChart
        val entries = sleepData.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val labels = sleepData.keys.toList()

        val dataSet = BarDataSet(entries, "Sleep Trends")
        dataSet.color = getColor(R.color.dark_blue)
        val barData = BarData(dataSet)

        // Konfigurasi BarChart
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)




        // Konfigurasi sumbu X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // Sumbu Y
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        // Konfigurasi Bar Data & Text
        barData.barWidth = 0.2f
        xAxis.textSize = 10f
        barChart.axisLeft.textSize = 16f


        barChart.invalidate() // Refresh grafik
    }
}