package com.example.sookcrooge

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sookcrooge.databinding.AccountBookAddBinding
import org.w3c.dom.Text
import java.util.Date
import kotlin.math.log


class AddCalendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = AccountBookAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var calendar_start = Calendar.getInstance()
        var calendar_end = Calendar.getInstance()
        val today = Calendar.getInstance()
        val year1 = today.get(Calendar.YEAR)
        val month2 = today.get(Calendar.MONTH)
        val day3 = today.get(Calendar.DATE)


        binding.btnLogin.setOnClickListener {
            val dlg = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                @SuppressLint("SetTextI18n")
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    // < 2 > picker에서 선택한 날짜로 설정
                    calendar_start.set(year, month + 1, dayOfMonth)
                    Log.d("test","year : $year, month: $month, day:$dayOfMonth")
                    val myText : TextView = findViewById(R.id.btnInput)
                    myText.text = "$year / $month / $dayOfMonth"
                }
                // < 3 > datepicker가 처음 떴을 때 오늘 날짜가 보이도록 설정
            }, year1, month2, day3)

            dlg.show()
        }

        val spinner: Spinner = binding.typeSpinner
        ArrayAdapter.createFromResource(
            this,
            R.array.tag,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter

        }
    }

}