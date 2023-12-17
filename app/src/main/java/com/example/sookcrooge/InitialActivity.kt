package com.example.sookcrooge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.sookcrooge.databinding.InitialPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthIntent
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.NidOAuthLoginState
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.oauth.view.NidOAuthLoginButton.Companion.launcher
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlin.math.sign

class InitialActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    var initTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding =InitialPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        NaverIdLoginSDK.initialize(this, "JJIzOz0M6gCf44GU3Prp", "wXreQPjMH3", "숙크루지")
        auth = Firebase.auth
        if (auth.currentUser!=null)
        {
            loginInformation.setCurrentLoginUserWithUID(auth.currentUser!!.uid, loginUser.email)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnSign.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            val intent2 = Intent(this, Login::class.java)
            startActivity(intent2)
        }
        val activityContext=this
        binding.btnNaver.setOnClickListener{
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

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis() - initTime > 3000){
                Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 \n 종료됩니다.", Toast.LENGTH_SHORT).show()
                initTime = System.currentTimeMillis()
                return true //false면 back버튼 기능 수행.
            }
            else
            {
                ActivityCompat.finishAffinity(this)
                System.runFinalization()
                System.exit(0)
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}