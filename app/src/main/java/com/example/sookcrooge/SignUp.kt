package com.example.sookcrooge

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sookcrooge.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignUp : AppCompatActivity() {
    val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = Firebase.auth

        binding.signUpButton.setOnClickListener {
            var isAllQualified = true
            db.collection("users").whereEqualTo("nickname", binding.registerInputNickname.text.toString())
                .get().addOnSuccessListener {
                    if (it.size()==0)
                    {
                        runOnUiThread {
                            binding.registerWarningNickname.visibility = View.INVISIBLE
                        }
                    }
                    else
                    {
                        runOnUiThread {
                            binding.registerWarningNickname.visibility = View.VISIBLE
                        }
                        isAllQualified=false
                    }
                    db.collection("users").whereEqualTo("email", binding.registerInputEmail.text.toString())
                        .get().addOnSuccessListener{document->
                            if (document.size()==0)
                            {
                                runOnUiThread {
                                    binding.registerWarningEmail.visibility = View.INVISIBLE
                                }
                            }
                            else
                            {
                                binding.registerWarningEmail.visibility = View.VISIBLE
                                isAllQualified=false
                            }
                            if (isPasswordQualified())
                            {
                                binding.registerWarningPassword.visibility = View.INVISIBLE
                            }
                            else
                            {
                                binding.registerWarningPassword.visibility = View.VISIBLE
                                isAllQualified=false
                            }
                            if (isPasswordAndPasswordConfirmSame())
                            {
                                binding.registerWarningPasswordConfirm.visibility = View.INVISIBLE
                            }
                            else
                            {
                                binding.registerWarningPasswordConfirm.visibility = View.VISIBLE
                                isAllQualified=false
                            }
                            if (isAllQualified==true)
                            {

                                val name=binding.registerInputName.text.toString()
                                val email= binding.registerInputEmail.text.toString()
                                val password = binding.registerInputPassword.text.toString()
                                val nickname=binding.registerInputNickname.text.toString()
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful)
                                        {
                                            val userUID: String? = auth.currentUser?.uid
                                            val newUser = hashMapOf(
                                                "uid" to userUID,
                                                "email" to email,
                                                "password" to password,
                                                "name" to name,
                                                "nickname" to nickname,
                                                "photoURL" to "null"
                                            )

                                            db.collection("users").document(userUID.toString()).set(newUser).addOnSuccessListener {
                                                val intent= Intent(this, Login::class.java)
                                                startActivity(intent)
                                                auth.signOut()
                                            }
                                                .addOnFailureListener{
                                                    Log.d("jhs", it.toString())
                                                }
                                        }
                                        else
                                        {
                                            Log.d("jhs", "회원 가입 실패")
                                        }
                                    }



                            }
                        }
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