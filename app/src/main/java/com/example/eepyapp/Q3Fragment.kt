package com.example.eepyapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*


class Q3Fragment(private val onAnswer: (Pair<String, Int>) -> Unit) : Fragment(R.layout.fragment_q3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sleepInput = view.findViewById<EditText>(R.id.input_sleep)
        val nextButton = view.findViewById<Button>(R.id.next_button)

        nextButton.setOnClickListener {
            val sleep = sleepInput.text.toString().toIntOrNull()
            if (sleep != null) {
                // Mendapatkan tanggal hari ini dalam format "dd-MM-yyyy"
                val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                onAnswer(Pair(currentDate, sleep)) // Kirim tanggal dan durasi tidur
            } else {
                sleepInput.error = "Masukan format jam yang benar!"
            }
        }
    }
}