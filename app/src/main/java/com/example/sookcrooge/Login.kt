package com.example.sookcrooge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.sookcrooge.databinding.ActivityLoginBinding
import com.example.sookcrooge.loginUser.Companion.email
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class Login : AppCompatActivity() {
    lateinit var btnLogin: ImageButton
    lateinit var editTextId: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnResetPassword: Button
    lateinit var btnNaverLogin: Button
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = com.google.firebase.ktx.Firebase.auth
        val activityContext=this
        binding.btnNaverLogin.setOnClickListener {
            NaverIdLoginSDK.logout()
            val oAuthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            val name = result.profile?.name.toString()
                            val id=result.profile?.id.toString()
                            val email=result.profile?.email.toString()
                            db.collection("users").whereEqualTo("uid", id).get().addOnSuccessListener {
                                if (it.size()==0)
                                {
                                    val nickname = makeNewNickname()
                                    val newUser = hashMapOf(
                                        "uid" to id,
                                        "email" to email,
                                        "password" to id,
                                        "name" to name,
                                        "nickname" to nickname,
                                        "photoURL" to "null"
                                    )

                                    db.collection("users").document(id).set(newUser)

                                }
                                loginInformation.setCurrentLoginUserWithUID(id, loginUser.naverLogin)
                                val intent = Intent(activityContext, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }

                        override fun onError(errorCode: Int, message: String) {
                            //
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            //
                        }

                    })
                }

                override fun onError(errorCode: Int, message: String) {
                    val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                    Log.d("jhs", "naverAccessToken : $naverAccessToken")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    //
                }

                fun makeNewNickname():String
                {
                    var nickname=""
                    val str = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                        "1", "2", "3", "4", "5", "6", "7", "8", "9")
                    val range=(0..str.size-1)
                    for (x in 0..9) {
                        val random = range.random()
                        nickname += str[random]
                    }
                    return nickname
                }
            }

            NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
        }

        binding.btnLogin.setOnClickListener {
            val user = binding.loginInputEmail.text.toString()
            val pass = binding.loginInputPassword.text.toString()

            if (user == "" || pass == "") {
                Toast.makeText(this@Login, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                //회원 정보 일치하지 않으면
                db.collection("users").whereEqualTo("email", user).get().addOnSuccessListener{
                    if (it.size()==0)
                    {
                        Toast.makeText(this@Login, "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        val dbPassword=it.documents[0].data?.get("password").toString()
                        if (dbPassword != pass)
                        {
                            Toast.makeText(this@Login, "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                        else
                        {
                            auth.signInWithEmailAndPassword(user, pass)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        loginInformation.setCurrentLoginUserWithUID(auth.currentUser!!.uid, loginUser.email)
                                        val intent=Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }

                        }
                    }
                }

            }
        }
    }
}
