package com.example.sookcrooge

import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.sookcrooge.databinding.ActivityEditProfileBinding
import com.example.sookcrooge.databinding.DeleteAccountDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback

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
    lateinit var userUID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = Firebase.auth
        if (loginInformation?.loginType == loginUser.naverLogin)
        {
            userUID=loginInformation.currentLoginUser!!.uid
        }
        else
        {
            userUID=auth.currentUser!!.uid
        }
        val docRef = db.collection("users").document(userUID)
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
                db.collection("users").document(userUID).update(map)
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

                if (loginInformation.loginType==loginUser.naverLogin)
                {
                    NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
                        override fun onError(errorCode: Int, message: String) {
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                        }

                        override fun onSuccess() {
                            db.collection("users").document(loginInformation.currentLoginUser!!.uid)
                                .delete()
                        }
                    })
                    deleteAccountDialog.dismiss()
                    val intent = Intent(this, InitialActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    val loginUser = Firebase.auth.currentUser!!
                    loginUser.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            deleteAccountDialog.dismiss()
                            val intent = Intent(this, InitialActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    db.collection("users").document(loginUser.uid)
                        .delete()
                }


            }
        }


    }


    private fun isAllConditionQualified():Boolean
    {
        nickname = binding.editInputNickname.text.toString()
        password = binding.editInputPassword.text.toString()
        var b:Boolean = true

        val task = db.collection("users")
            .whereEqualTo("nickname", nickname).get().addOnSuccessListener { documents ->
                if (documents.size()==0)
                {
                    binding.editWarningNickname.visibility= View.INVISIBLE
                }
                else if (documents.size()==1)
                {
                    for (document in documents) {
                        if (document.id == userUID)
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

}