package com.example.eepyapp
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.eepyapp.data.retrofit.ApiConfig
import com.example.eepyapp.data.response.SleepDurationRequest
import com.example.eepyapp.data.response.SleepDurationResponse
import com.example.eepyapp.data.response.SleepQualityRequest
import com.example.eepyapp.data.response.SleepQualityResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
                sendDataToApi() // Kirim data ke API
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

    private fun sendDataToApi() {
        val gender = surveyResponses["gender"] as Int
        val age = surveyResponses["age"] as Int
        val physicalActivity = surveyResponses["physicalActivity"] as Int * 10
        val stressLevel = surveyResponses["stressLevel"] as Int
        val sleepDuration = surveyResponses["sleepDuration"] as Int

        val qualityRequest = SleepQualityRequest(
            gender = gender,
            age = age,
            physicalActivity = physicalActivity,
            stressLevel = stressLevel,
            sleepDuration = sleepDuration
        )

        val durationRequest = SleepDurationRequest(
            gender = gender,
            age = age,
            physicalActivity = physicalActivity,
            stressLevel = stressLevel
        )

        // Predict Sleep Quality
        ApiConfig.getApiService().predictSleepQuality(qualityRequest)
            .enqueue(object : Callback<SleepQualityResponse> {
                override fun onResponse(call: Call<SleepQualityResponse>, response: Response<SleepQualityResponse>) {
                    if (response.isSuccessful) {
                        val prediction = response.body()?.prediction ?: 0f
                        savePredictionToPreferences("quality", prediction)
                        Log.d("SurveyActivity", "Sleep Quality Prediction: $prediction")
                    } else {
                        Log.e("SurveyActivity", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<SleepQualityResponse>, t: Throwable) {
                    Log.e("SurveyActivity", "Failed to connect to API: ${t.message}")
                }
            })

        // Predict Sleep Duration
        ApiConfig.getApiService().predictSleepDuration(durationRequest)
            .enqueue(object : Callback<SleepDurationResponse> {
                override fun onResponse(call: Call<SleepDurationResponse>, response: Response<SleepDurationResponse>) {
                    if (response.isSuccessful) {
                        val prediction = response.body()?.prediction ?: 0f
                        savePredictionToPreferences("duration", prediction)
                        Log.d("SurveyActivity", "Sleep Duration Prediction: $prediction")
                    } else {
                        Log.e("SurveyActivity", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<SleepDurationResponse>, t: Throwable) {
                    Log.e("SurveyActivity", "Failed to connect to API: ${t.message}")
                }
            })
    }

    private fun savePredictionToPreferences(key: String, value: Float) {
        val sharedPreferences = getSharedPreferences("EepyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }
}
