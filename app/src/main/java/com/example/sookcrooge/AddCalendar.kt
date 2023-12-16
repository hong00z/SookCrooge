package com.example.sookcrooge

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sookcrooge.databinding.AccountBookAddBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.w3c.dom.Text
import java.util.Date
import kotlin.math.log


class AddCalendar : AppCompatActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AccountBookAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lateinit var spinnerType:String
        lateinit var radioType: String
        var calendar_start = Calendar.getInstance()
        val userName="asd123@naver.com"
        var calendar_end = Calendar.getInstance()
        lateinit var dateText: String
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
                    val myText:TextView = findViewById(R.id.btnInput)
                    myText.text = "$year.$month.$dayOfMonth"
                    dateText = myText.text.toString()
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
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@AddCalendar,"분류를 선택해주세요",Toast.LENGTH_SHORT).show()

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        spinnerType="식비"
                    }
                    1 -> {
                        spinnerType="쇼핑"
                    }
                    2 -> {
                        spinnerType="교통비"
                    }
                    3 -> {
                        spinnerType="정기결제"
                    }
                    4 -> {
                        spinnerType="공과금"
                    }
                    5-> {
                        spinnerType="통신비"
                    }
                    6->{
                        spinnerType="경조사비"
                    }
                    7 ->{
                        spinnerType="데이트"
                    }
                    8 ->{
                        spinnerType="기타"
                    }


                }
            }
        }

        val radio: RadioGroup = findViewById(R.id.radio_group)
        radio.setOnCheckedChangeListener{_,id ->
            when(id){
                R.id.account_type1->{
                    radioType = "저축"
                }
                R.id.account_type2 -> {
                    radioType="지출"
                }

            }

        }


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnOk.setOnClickListener {
            val amount: String = findViewById<EditText?>(R.id.price_amount).text.toString()
            val memo: String = findViewById<EditText?>(R.id.memo).text.toString()
            Log.d("add_test","$radioType, $amount, $memo,$spinnerType,$dateText ")
            Toast.makeText(this@AddCalendar,"가계부 입력 성공",Toast.LENGTH_SHORT).show()

            db.collection("accounts").document()
                .set(Account(userName,radioType,amount,spinnerType,memo,dateText))
                .addOnSuccessListener { Log.d("TEST", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("TEST", "Error writing document", e) }
            val intent = Intent(this,CalendarActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            // 뒤로가기
            android.R.id.home -> {
                val intent = Intent(this,CalendarActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}



