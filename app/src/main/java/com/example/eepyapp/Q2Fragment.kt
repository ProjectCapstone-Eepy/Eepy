package com.example.eepyapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Q2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Q2Fragment(private val onAnswer: (String) -> Unit) : Fragment(R.layout.fragment_q2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val maleButton = view.findViewById<Button>(R.id.answer1)
        val femaleButton = view.findViewById<Button>(R.id.answer2)

        maleButton.setOnClickListener{
            onAnswer("Laki-Laki")
        }

        femaleButton.setOnClickListener{
            onAnswer("Perempuan")
        }
    }


}