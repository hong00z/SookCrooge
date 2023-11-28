package com.example.sookcrooge

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.sookcrooge.databinding.ActivityMainBinding
import com.example.sookcrooge.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.signUpButton.setOnClickListener {
            if (isAllConditionQualified())
            {
                //추후 구현: 모든 조건을 만족한다면 DB에 저장.
                finish()
            }

        }

    }
    @SuppressLint("SuspiciousIndentation")
    private fun isPasswordQualified():Boolean
    {
        var hasLetter = false
        var hasDigit = false
        val password = binding.registerInputPassword.text.toString()
        if (password.length < 8)
        return false

        password.forEach{
            if (it.isDigit())
            {
                hasDigit = true
            }
            if (it.isLetter())
            {
                hasLetter = true
            }
        }
        return hasLetter && hasDigit
    }
    private fun isPasswordAndPasswordConfirmSame():Boolean
    {
        val password = binding.registerInputPassword.text.toString()
        val passwordConfirm = binding.registerInputPasswordConfirm.text.toString()
        return password == passwordConfirm
    }

    private fun isAllConditionQualified():Boolean
    {
        var condition = true

        //(추후 구현) 이미 닉네임이 있다면
        binding.registerWarningNickname.visibility = View.VISIBLE

        //(추후 구현) 이미 가입한 메일이라면
        binding.registerWarningEmail.visibility = View.VISIBLE

        if (isPasswordQualified())
        {
            binding.registerWarningPassword.visibility = View.INVISIBLE
        }
        else
        {
            binding.registerWarningPassword.visibility = View.VISIBLE
            condition=false
        }

        if (isPasswordAndPasswordConfirmSame())
        {
            binding.registerWarningPasswordConfirm.visibility = View.INVISIBLE
        }
        else
        {
            binding.registerWarningPasswordConfirm.visibility = View.VISIBLE
            condition = false
        }
        return condition
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}