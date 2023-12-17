package com.example.sookcrooge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast



class Login : AppCompatActivity() {
    lateinit var btnLogin: ImageButton
    lateinit var editTextId: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnResetPassword: Button
    lateinit var btnNaverLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin!!.setOnClickListener {
            val user = editTextId!!.text.toString()
            val pass = editTextPassword!!.text.toString()


            if (user == "" || pass == "") {
                Toast.makeText(this@Login, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                //회원 정보 일치하지 않으면
                Toast.makeText(this@Login, "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}