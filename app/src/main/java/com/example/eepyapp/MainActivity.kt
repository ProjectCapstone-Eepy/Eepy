package com.example.eepyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var viewpagerTips: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tampilkan Waktu Tidur Ideal
        val tvSleepTime = findViewById<TextView>(R.id.tvSleepTime)
        tvSleepTime.text = loadSleepDuration()

        // Tampilkan Kualitas Tidur
        val tvSleepQuality = findViewById<TextView>(R.id.tvSleepQuality)
        tvSleepQuality.text = loadSleepQualityDescription()

        // Inisialisasi ViewPager2 untuk Tips
        val tipsList = listOf(
            "Lakukan olahraga atau aktivitas ringan di siang hari untuk membantu tubuh lebih rileks saat malam.",
            "Mandi dan pastikan tubuh nyaman sebelum tidur.",
            "Ciptakan suasana kamar yang sejuk dan tenang, seperti meredupkan lampu untuk relaksasi.",
            "Hindari distraksi seperti notifikasi ponsel agar tidur lebih nyenyak.",
            "Kurangi konsumsi kafein di sore atau malam hari agar tidur lebih berkualitas.",
            "Nikmati makan malam secukupnya dan hindari makanan berat menjelang tidur.",
            "Baca buku favorit untuk membantu pikiran rileks sebelum tidur.",
            "Yakinkan diri bahwa sekarang adalah saatnya istirahat agar tubuh pulih dengan optimal.",
            "Lakukan teknik pernapasan mendalam seperti meditasi untuk merilekskan tubuh dan pikiran.",
            "Biarkan imajinasi berjalan bebas, pikirkan hal-hal menyenangkan untuk mengantarkan tidur.",
            "Akhiri hari dengan melakukan sesuatu yang memuaskan agar tidur terasa lebih tenang."
        )
        viewpagerTips = findViewById(R.id.vpTips)
        viewpagerTips.adapter = TipsAdapter(tipsList)

        // Bar Chart untuk Sleep Trends
        val barChart = findViewById<BarChart>(R.id.sleepGraph)
        val sleepData = loadSleepData()
        val entries = sleepData.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val labels = sleepData.keys.toList()
        val dataSet = BarDataSet(entries, "Sleep Trends")
        val isDarkMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
        dataSet.color = if (isDarkMode) getColor(R.color.light_blue) else getColor(R.color.dark_blue)
        val barData = BarData(dataSet)

        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.textColor = if (isDarkMode) getColor(R.color.white) else getColor(R.color.black)
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.textColor = if (isDarkMode) getColor(R.color.white) else getColor(R.color.black)
        barChart.axisRight.isEnabled = false
        barData.barWidth = 0.2f
        xAxis.textSize = 10f
        leftAxis.textSize = 16f
        barChart.invalidate()

        // Tombol untuk Update Survey
        val btnUpdateSurvey = findViewById<Button>(R.id.btnUpdateSurvey)
        btnUpdateSurvey.setOnClickListener {
            val intent = Intent(this, SurveyActivity::class.java)
            intent.putExtra("isUpdating", true)
            startActivity(intent)
        }
    }

    private fun loadSleepData(): HashMap<String, Int> {
        val sharedPreferences = getSharedPreferences("EepyPreferences", Context.MODE_PRIVATE)
        val sleepDataString = sharedPreferences.getString("sleepData", "{}") ?: "{}"
        return Gson().fromJson(sleepDataString, HashMap::class.java) as HashMap<String, Int>
    }

    private fun loadSleepDuration(): String {
        val sharedPreferences = getSharedPreferences("EepyPreferences", Context.MODE_PRIVATE)
        val duration = sharedPreferences.getFloat("duration", -1f)
        return if (duration != -1f) {
            val hours = duration.toInt()
            val minutes = ((duration - hours) * 60).toInt()
            "${hours}H ${minutes}M"
        } else {
            "Belum ada data waktu tidur ideal."
        }
    }

    private fun loadSleepQualityDescription(): String {
        val sharedPreferences = getSharedPreferences("EepyPreferences", Context.MODE_PRIVATE)
        val sleepQuality = sharedPreferences.getFloat("quality", -1f)
        return if (sleepQuality != -1f) {
            getSleepQualityDescription(sleepQuality)
        } else {
            "Belum ada data kualitas tidur yang tersedia."
        }
    }

    private fun getSleepQualityDescription(quality: Float): String {
        return when {
            quality < 5 -> "Kualitas tidur Anda kurang baik. Disarankan untuk memperbaiki pola tidur."
            quality < 8 -> "Kualitas tidur Anda cukup baik, namun masih dapat ditingkatkan."
            quality <= 10 -> "Kualitas tidur Anda sangat baik! Pertahankan pola tidur seperti ini."
            else -> "Data kualitas tidur tidak valid."
        }
    }
}
