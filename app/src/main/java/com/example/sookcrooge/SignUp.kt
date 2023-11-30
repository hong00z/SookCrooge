package com.example.sookcrooge

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sookcrooge.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.Tasks
import com.google.android.play.integrity.internal.f
import com.google.android.play.integrity.internal.t
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.checkerframework.framework.qual.DefaultFor
import java.util.concurrent.ExecutionException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class SignUp : AppCompatActivity() {
    val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    var hasQueryResult: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = Firebase.auth

        binding.signUpButton.setOnClickListener {
            if (isAllConditionQualified())
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
                                "nickname" to nickname
                            )

                            db.collection("users")
                                .document(userUID!!).set(newUser)
                                .addOnSuccessListener {
                                    Log.d("jhs", "저장 완료")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("jhs", "Error adding document", e)
                                }

                        }
                        else
                        {
                            Log.d("jhs", "회원 가입 실패")
                        }
                    }
                //finish()
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
        var testCondition : Deferred<Boolean>
        CoroutineScope(Dispatchers.IO).launch{
            val hasSameString = getTokenResult("nickname", binding.registerInputNickname.text.toString())
            if (hasSameString==true)
            {
                runOnUiThread {
                    binding.registerWarningNickname.visibility = View.VISIBLE
                }
                condition = false
            }
            else
            {
                runOnUiThread {
                    binding.registerWarningNickname.visibility = View.INVISIBLE
                }
            }
            val hasSame = getTokenResult("email", binding.registerInputEmail.text.toString())
            if (hasSame==true)
            {
                runOnUiThread {
                    binding.registerWarningEmail.visibility = View.VISIBLE
                }
                condition = false
            }
            else
            {
                runOnUiThread {
                    binding.registerWarningEmail.visibility = View.INVISIBLE
                }
            }
        }
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

    private suspend fun getTokenResult (field: String, findItem: String) = suspendCoroutine<Boolean> { continuation ->
        val task = db.collection("users")
            .whereEqualTo(field, findItem)
            .get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.size()==0)
                {
                    continuation.resume(false)
                }
                else
                {
                    continuation.resume(true)
                }

            }
        }
    }
    private fun hasDataOnDB(field: String, findItem: String): Boolean {

        CoroutineScope(Dispatchers.IO).launch{
            val tempToken = getTokenResult(field, findItem)
        }
        return hasQueryResult
    }

}