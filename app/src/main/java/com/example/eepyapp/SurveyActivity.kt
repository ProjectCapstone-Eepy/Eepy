package com.example.eepyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class SurveyActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private val surveyResponses = mutableMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Periksa apakah ini mode pembaruan
        val isUpdating = intent.getBooleanExtra("isUpdating", false)

        // Jika bukan pembaruan dan survey sudah dilakukan hari ini, langsung ke Home
        if (!isUpdating && isSurveyDoneToday()) {
            navigateToHome()
            return
        }

        setContentView(R.layout.activity_survey)
        viewPager = findViewById(R.id.viewPager)

        val fragments = listOf(
            Q1Fragment { age ->
                saveResponse("age", age)
                navigateToNextPage()
            },
            Q2Fragment { gender ->
                saveResponse("gender", gender)
                navigateToNextPage()
            },
            Q4Fragment { physicalActivity ->
                saveResponse("physicalActivity", physicalActivity)
                navigateToNextPage()
            },
            Q5Fragment { stressLevel ->
                saveResponse("stressLevel", stressLevel)
                navigateToNextPage()
            },
            Q3Fragment { response ->
                saveSleepData(response.first, response.second)
                saveSurveyDate()
                navigateToHome()
            }
        )

        viewPager.adapter = SurveyPagerAdapter(this, fragments)
    }

    private fun saveSleepData(date: String, sleep: Int) {
        val sharedPreferences = getSharedPreferences("EepyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Ambil data sebelumnya
        val sleepDataString = sharedPreferences.getString("sleepData", "{}") ?: "{}"
        val sleepDataMap = Gson().fromJson(sleepDataString, HashMap::class.java) as HashMap<String, Int>

        // Tambahkan data baru atau perbarui
        sleepDataMap[date] = sleep
        editor.putString("sleepData", Gson().toJson(sleepDataMap))
        editor.apply()

        Log.d("SurveyActivity", "Sleep Data Updated: $sleepDataMap")
    }

    private fun navigateToNextPage() {
        viewPager.currentItem = viewPager.currentItem + 1
    }

    private fun saveResponse(key: String, value: Any) {
        surveyResponses[key] = value
        Log.d("SurveyActivity", "Current Responses: $surveyResponses")
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isSurveyDoneToday(): Boolean {
        val sharedPreferences = getSharedPreferences("EepyPreferences", Context.MODE_PRIVATE)
        val lastDate = sharedPreferences.getString("lastSurveyDate", null)
        val currentDate = getCurrentDate()
        return lastDate == currentDate
    }

    private fun saveSurveyDate() {
        val sharedPreferences = getSharedPreferences("EepyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("lastSurveyDate", getCurrentDate())
        editor.apply()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
