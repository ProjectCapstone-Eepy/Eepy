package com.example.eepyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        // Fitur Tips

        // Data Tips
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


        // Inisialisasi ViewPager2
        viewpagerTips = findViewById(R.id.vpTips)
        viewpagerTips.adapter = TipsAdapter(tipsList)


        // Fitur Bar Chart
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
