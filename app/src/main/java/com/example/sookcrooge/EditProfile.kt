package com.example.sookcrooge

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sookcrooge.databinding.ActivityEditProfileBinding
import com.example.sookcrooge.databinding.DeleteAccountDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EditProfile : AppCompatActivity() {
    val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }

    private lateinit var name:String
    private lateinit var nickname:String
    private lateinit var password:String

    val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth
    private lateinit var userPassword:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = Firebase.auth
        val user = Firebase.auth.currentUser!!

        val docRef = db.collection("users").document(user.uid)
        docRef.get().addOnSuccessListener{document->
            if (document != null)
            {
                binding.editInputNickname.setText(document.data?.get("nickname").toString())
                binding.editInputName.setText(document.data?.get("name").toString())
                binding.editInputEmail.setText(document.data?.get("email").toString())
                userPassword = document.data?.get("password").toString()
            }
        }

        binding.editProfileButton.setOnClickListener{
            if (isAllConditionQualified())
            {
                name = binding.editInputName.text.toString()

                var map= mutableMapOf<String,Any>()
                map["name"] = name
                map["nickname"] = nickname
                if (user != null) {
                    db.collection("users").document(user.uid).update(map)
                }
                finish()
            }

        }

        binding.deleteAccountButton.setOnClickListener{
            val dialogBinding = DeleteAccountDialogBinding.inflate(layoutInflater)
            val deleteAccountDialog = AlertDialog.Builder(this).run {
                setTitle("회원 탈퇴")
                setMessage("정말로 탈퇴하시겠습니까?")
                setView(dialogBinding.root)
                show()

            }
            dialogBinding.deleteAccountNo.setOnClickListener {
                deleteAccountDialog.dismiss()
            }
            dialogBinding.deleteAccountYes.setOnClickListener {
                val loginUser = Firebase.auth.currentUser!!
                loginUser.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        deleteAccountDialog.dismiss()
                    }
                }
                db.collection("users").document(loginUser.uid)
                    .delete()

            }
        }


    }


    private fun isAllConditionQualified():Boolean
    {
        nickname = binding.editInputNickname.text.toString()
        password = binding.editInputPassword.text.toString()
        var b:Boolean = true

        val user=Firebase.auth.currentUser!!
        val task = db.collection("users")
            .whereEqualTo("nickname", nickname).get().addOnSuccessListener { documents ->
                if (documents.size()==0)
                {
                    binding.editWarningNickname.visibility= View.INVISIBLE
                }
                else if (documents.size()==1)
                {
                    for (document in documents) {
                        if (document.id == user.uid)
                        {
                            binding.editWarningNickname.visibility= View.INVISIBLE
                        }
                        else
                        {
                            binding.editWarningNickname.visibility= View.VISIBLE
                            b = false
                        }
                    }
                }
                else
                {
                    binding.editWarningNickname.visibility= View.VISIBLE
                    b = false
                }
            }

        if (userPassword == password)
        {
            binding.editWarningPassword.visibility= View.INVISIBLE
        }
        else
        {
            b = false
            binding.editWarningPassword.visibility= View.VISIBLE
        }
        return false
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

    private suspend fun getResultFromDB (field: String, findItem: String) = suspendCoroutine<Boolean> { continuation ->
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
}