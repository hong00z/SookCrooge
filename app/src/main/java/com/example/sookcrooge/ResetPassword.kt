package com.example.sookcrooge

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sookcrooge.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Timer
import java.util.TimerTask


class ResetPassword : AppCompatActivity() {
    val binding: ActivityResetPasswordBinding by lazy {
        ActivityResetPasswordBinding.inflate(layoutInflater)
    }
    private var hasTimer = false
    private var isVerified= false
    private var verificationCode = ""
    var timer = Timer()
    val db = Firebase.firestore
    lateinit var userUID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.resetPWDNumberConfirmButton.setOnClickListener{

            val inputNumberText = binding.resetPWDInputNumber.text.toString()
            binding.resetPWDWarningNumber.visibility = View.VISIBLE
            if (verificationCode == inputNumberText)
            {
                binding.resetPWDWarningNumber.text = "인증이 완료되었습니다."
                binding.resetPWDWarningNumber.setTextColor(Color.parseColor("#50BE00"))
                isVerified = true
                binding.resetPWDInputNumber.isEnabled=false
                timer.cancel()
            }
        }

        binding.sendVerificationCode.setOnClickListener{
            //추후 구현: 가입된 이메일인지 체크 후 아니라면 해당 error 메시지 띄우기
            if (isVerified)
            {
                return@setOnClickListener
            }
            db.collection("users")
                .whereEqualTo("email", binding.resetPWDInputEmail.text.toString())
                .get()
                .addOnSuccessListener {documents->
                    if (documents.size() == 0)
                    {
                        binding.resetPWDWarningEmail.visibility = View.VISIBLE
                    }
                    else
                    {
                        binding.resetPWDWarningEmail.visibility = View.INVISIBLE
                        class mailSenderThread():Thread(){
                            override fun run()
                            {
                                val receiverEmail = binding.resetPWDInputEmail.text.toString()
                                val sender = EmailSender(receiverEmail, verificationCode)
                                sender.sendMail()
                            }
                        }
                        val testThread = mailSenderThread()
                        testThread.start()

                        setTimer()
                    }
                }


        }
        binding.reSendVerificationCode.setOnClickListener {
            if (isVerified)
            {
                return@setOnClickListener
            }
            createVerificationCode()
            class mailSenderThread():Thread(){
                override fun run()
                {
                    val receiverEmail = binding.resetPWDInputEmail.text.toString()
                    val sender = EmailSender(receiverEmail, verificationCode)
                    sender.sendMail()
                }
            }
            val testThread = mailSenderThread()
            testThread.start()
        }

        binding.resetPWDButton.setOnClickListener{
            var isAllQualified = true
            if (!isVerified)
            {
                binding.resetPWDWarningNumber.visibility=View.VISIBLE
                binding.resetPWDWarningNumber.text = "인증이 완료되지 않았습니다."
            }
            if (isPasswordQualified())
            {
                binding.resetPWDWarningPassword.visibility= View.INVISIBLE
            }
            else
            {
                binding.resetPWDWarningPassword.visibility= View.VISIBLE
                isAllQualified=false
            }
            if (isPasswordAndPasswordConfirmSame())
            {
                binding.resetPWDWarningPasswordConfirm.visibility= View.INVISIBLE
            }
            else
            {
                binding.resetPWDWarningPasswordConfirm.visibility= View.VISIBLE
                isAllQualified=false
            }
            if (isAllQualified==true)
            {
                val newPassword = binding.resetPWDInputPassword.text.toString()
                if (loginInformation?.loginType == loginUser.naverLogin)
                {
                    userUID=loginInformation.currentLoginUser!!.uid
                }
                else
                {
                    val user = Firebase.auth.currentUser
                    userUID=user!!.uid
                    user!!.updatePassword(newPassword)
                }


                var map= mutableMapOf<String,Any>()
                map["password"] =newPassword
                db.collection("users").document(userUID).update(map)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            Log.d("jhs", "업데이트됨")
                        }
                    }
                finish()
            }
        }


    }

    private fun isPasswordQualified():Boolean
    {
        var hasLetter = false
        var hasDigit = false
        val password = binding.resetPWDInputPassword.text.toString()
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
        val password = binding.resetPWDInputPassword.text.toString()
        val passwordConfirm = binding.resetPWDInputPasswordConfirm.text.toString()
        return password == passwordConfirm
    }

    private fun createVerificationCode()
    {
        verificationCode=""
        val str = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "1", "2", "3", "4", "5", "6", "7", "8", "9")
        val range=(0..str.size-1)
        for (x in 0..7) {
            val random = range.random()
            verificationCode += str[random]
        }
    }
    private fun setTimer()
    {
        if (hasTimer)
        {
            return
        }
        hasTimer = true
        var min=10
        var sec=60
        val timerTask = object: TimerTask(){

            override fun run() {
                sec--
                if (min !=0 && sec==0)
                {
                    sec = 60
                }
                else if (sec==59)
                {
                    min--
                }
                runOnUiThread{
                    if (sec==60)
                    {
                        binding.timerSec.text = "00"
                    }
                    else if (sec <= 9)
                    {
                        binding.timerSec.text= "0" + sec.toString()
                    }
                    else
                    {
                        binding.timerSec.text = sec.toString()
                    }
                    binding.timerMin.text = "0" + min.toString()
                }
                if (min==0 && sec==0)
                {
                    hasTimer = false
                    timer.cancel()
                }
            }
        }
        timer.schedule(timerTask, 0, 1000)
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