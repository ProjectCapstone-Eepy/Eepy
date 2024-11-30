package com.example.eepyapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class SurveyActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private val surveyResponses = mutableMapOf<String, Any>()
    private val sleepData = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                navigateToHome()
            }
        )

        viewPager.adapter = SurveyPagerAdapter(this, fragments)
    }

    private fun saveSleepData(date: String, sleep: Int) {
        sleepData[date] = sleep
        Log.d("SurveyActivity", "Sleep Data: $sleepData")
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
        intent.putExtra("sleepData", HashMap(sleepData))
        intent.putExtra("responses", HashMap(surveyResponses))
        startActivity(intent)
        finish()
    }
}
