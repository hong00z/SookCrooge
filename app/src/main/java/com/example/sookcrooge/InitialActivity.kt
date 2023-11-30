package com.example.sookcrooge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sookcrooge.databinding.InitialPageBinding
import kotlin.math.sign

class InitialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding =InitialPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSign.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }



    }
}