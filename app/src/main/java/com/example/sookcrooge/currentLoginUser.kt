package com.example.sookcrooge

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class loginUser(uid: String)
{
    val uid:String
    var nickname:String?
    init
    {
        this.uid=uid
        this.nickname=null
    }
    companion object {
        const val logout = 0
        const val naverLogin = 1
        const val email=2
    }
}

object loginInformation {
    var currentLoginUser: loginUser?
    var loginType: Int
    private val db = Firebase.firestore
    init {
        currentLoginUser=null
        loginType=loginUser.logout
    }

    fun setCurrentLoginUserWithUID(uid: String, loginType: Int)
    {
        this.loginType=loginType
        currentLoginUser=loginUser(uid)
        db.collection("users").whereEqualTo("uid", uid).get().addOnSuccessListener{
            for (document in it)
            {
                val nickname = document.data["nickname"].toString()
                currentLoginUser!!.nickname=nickname
            }
        }

    }
}