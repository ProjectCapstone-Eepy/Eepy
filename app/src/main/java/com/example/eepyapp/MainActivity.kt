package com.example.eepyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi BarChart
        val barChart = findViewById<BarChart>(R.id.sleepGraph)

        // Load Sleep Data from SharedPreferences
        val sleepData = loadSleepData()

        // Convert Sleep Data to Bar Chart Entries
        val entries = sleepData.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val labels = sleepData.keys.toList()
        val dataSet = BarDataSet(entries, "Sleep Trends")

        // Deteksi tema saat ini
        val isDarkMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES

        // Warna bar sesuai tema
        dataSet.color = if (isDarkMode) getColor(R.color.light_blue) else getColor(R.color.dark_blue)

        val barData = BarData(dataSet)

        // Configure Legend (Yang text bawah)
        val legend = barChart.legend
        legend.textColor = if (isDarkMode) getColor(R.color.white) else getColor(R.color.black)

        // Configure Bar Chart
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)

        // Configure X Axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.textColor = if (isDarkMode) getColor(R.color.white) else getColor(R.color.black)

        // Configure Y Axis
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.textColor = if (isDarkMode) getColor(R.color.white) else getColor(R.color.black)
        barChart.axisRight.isEnabled = false

        // Configure Bar Data & Text
        barData.barWidth = 0.2f
        xAxis.textSize = 10f
        xAxis.textColor = if (isDarkMode) getColor(R.color.white) else getColor(R.color.black)
        leftAxis.textSize = 16f

        barChart.invalidate() // Refresh grafik

        // Tombol untuk Update Survey
        val btnUpdateSurvey = findViewById<Button>(R.id.btnUpdateSurvey)
        btnUpdateSurvey.setOnClickListener {
            val intent = Intent(this, SurveyActivity::class.java)
            intent.putExtra("isUpdating", true) // Flag untuk pembaruan
            startActivity(intent)
        }
    }

    private fun loadSleepData(): HashMap<String, Int> {
        val sharedPreferences = getSharedPreferences("EepyPreferences", Context.MODE_PRIVATE)
        val sleepDataString = sharedPreferences.getString("sleepData", "{}") ?: "{}"
        return Gson().fromJson(sleepDataString, HashMap::class.java) as HashMap<String, Int>
    }
}
