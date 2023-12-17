package com.example.sookcrooge

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sookcrooge.MessageModel.Companion.MY_MESSAGE
import com.example.sookcrooge.MessageModel.Companion.OTHERS_MESSAGE
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.ActivityChattingBinding
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.StringTokenizer


class Chatting : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    val messageList = mutableListOf<MessageModel>()
    private lateinit var userNickname:String
    private var accountWindowUserName:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        val binding=ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth=Firebase.auth
        lateinit var userUID:String
        if (loginInformation?.loginType == loginUser.naverLogin)
        {
            userUID=loginInformation.currentLoginUser!!.uid
        }
        else
        {
            userUID=auth.currentUser!!.uid
        }
        val userQuery = db.collection("users").whereEqualTo("uid", userUID).get()
            .addOnSuccessListener{
                it.forEach{document->
                    userNickname=document["nickname"].toString()
                }
                var prevChattingLogDate:String? = null
                val chatting = db.collection("chattingRoom_shdknm1hks").orderBy("time")
                chatting.addSnapshotListener{ snapshot, e ->
                    for (document in snapshot!!.documentChanges) {
                        if (document.type == DocumentChange.Type.ADDED)
                        {
                            var timeStamp = document.document.data["time"] as Timestamp
                            val timeStampLong=timeStamp.seconds*1000+32400000

                            val chattingDate = SimpleDateFormat("YYYYMMdd").format(timeStampLong)
                            val dateCheck : Boolean = prevChattingLogDate==null || chattingDate != prevChattingLogDate
                            if (document.document.data["nickname"].toString()==userNickname)
                            {
                                messageList.add(MessageModel(MY_MESSAGE, document.document.data["nickname"].toString(),
                                    document.document.data["text"].toString(), timeStampLong, dateCheck))
                            }
                            else
                            {
                                messageList.add(MessageModel(OTHERS_MESSAGE, document.document.data["nickname"].toString(),
                                    document.document.data["text"].toString(), timeStampLong, dateCheck))
                            }
                            prevChattingLogDate=chattingDate
                        }

                    }
                    binding.messageRecycler.layoutManager = LinearLayoutManager(this)
                    binding.messageRecycler.adapter = messageAdapter(this, messageList)
                }

            }
        var userAccountWindowDatas = mutableListOf<userItem>()

        val chattingUsersQuery = db.collection("rooms").document("shdknm1hks")
        chattingUsersQuery.addSnapshotListener{snapshots, e ->
            userAccountWindowDatas.clear()
            val chattingUsers = snapshots!!.data?.get("userName").toString()
            val st = StringTokenizer(chattingUsers, ",")
            while (st.hasMoreTokens())
            {
                val nickname=st.nextToken()
                db.collection("users").whereEqualTo("nickname", nickname).get().addOnSuccessListener{
                    val photoURL=it.documents[0].data?.get("photoURL").toString()
                    if (photoURL=="null")
                    {
                        userAccountWindowDatas.add(userItem(nickname, null))
                    }
                    else
                    {
                        userAccountWindowDatas.add(userItem(nickname, photoURL))
                    }

                    runOnUiThread{
                        binding.userRecycler.adapter = usersAdapter(this, userAccountWindowDatas)
                        usersAdapter(this, userAccountWindowDatas).notifyDataSetChanged()
                    }

                    (binding.userRecycler.adapter as usersAdapter).setItemClickListener(object: usersAdapter.OnItemClickListener{
                        override fun onClick(v: View, position: Int) {
                            if (binding.userAccountWindow.visibility == View.GONE)
                            {
                                binding.userAccountWindow.visibility= View.VISIBLE
                                accountWindowUserName=userAccountWindowDatas[position].nickname
                            }
                            else
                            {
                                if (userAccountWindowDatas[position].nickname==accountWindowUserName)
                                {
                                    binding.userAccountWindow.visibility= View.GONE
                                }
                                else
                                {
                                    accountWindowUserName=userAccountWindowDatas[position].nickname
                                }
                            }
                            var itemDatas = mutableListOf<accountItem>()
                            var today=System.currentTimeMillis()
                            today+=32400000
                            today-=today%86400000
                            val todayDate=Date(today)
                            val todayTimestamp=Timestamp(todayDate)


                            db.collection("users").whereEqualTo("nickname", accountWindowUserName).get().addOnSuccessListener {
                                val othersUserUID=it.documents[0].data?.get("uid").toString()
                                db.collection("users").document(othersUserUID).collection("accountBook").whereGreaterThanOrEqualTo("date", todayTimestamp).orderBy("date").get()
                                    .addOnSuccessListener {documents->
                                        if (documents.size()==0)
                                        {
                                            binding.noSpendOrSaveText.visibility=View.VISIBLE
                                            binding.noSpendOrSaveText.text=accountWindowUserName+"님은" +
                                                    " 오늘의 소비/절약이 없습니다."
                                            binding.itemRecycler.visibility=View.GONE

                                        }
                                        else
                                        {
                                            binding.noSpendOrSaveText.visibility=View.GONE
                                            binding.itemRecycler.visibility=View.VISIBLE
                                            for (document in documents)
                                            {
                                                var timeStamp = document.data["date"] as com.google.firebase.Timestamp
                                                val timeStampLong=timeStamp.seconds*1000+32400
                                                val month= SimpleDateFormat("MM").format(timeStampLong)
                                                val day= SimpleDateFormat("dd").format(timeStampLong)

                                                val newItem = accountItem(document.id, document.data["name"].toString(), document.data["cost"].toString().toInt(),
                                                    (month.toInt()-1).toString(), day, document.data["type"].toString(),
                                                    document.data["angry"].toString().toInt(), document.data["smile"].toString().toInt(), document.data["tag"].toString())
                                                itemDatas.add(newItem)
                                            }
                                            binding.itemRecycler.adapter = OthersAccountAdapter(itemDatas)
                                            (binding.itemRecycler.adapter as OthersAccountAdapter).setItemClickListener(object: OthersAccountAdapter.OnItemClickListener{
                                                override fun onSmileClick(binding: AccountListBinding, data: accountItem) {
                                                    if (userNickname==accountWindowUserName)
                                                    {
                                                        return
                                                    }
                                                    binding.smileText.text= (data.smile+1).toString()
                                                    db.collection(othersUserUID).document(data.documentID).update("smile", (data.smile+1))
                                                }
                                                override fun onAngryClick(binding: AccountListBinding, data: accountItem)
                                                {
                                                    if (userNickname==accountWindowUserName)
                                                    {
                                                        return
                                                    }
                                                    binding.angryText.text= (data.angry+1).toString()
                                                    db.collection(othersUserUID).document(data.documentID).update("angry", (data.angry+1))
                                                }
                                            })
                                        }
                                        runOnUiThread{
                                            OthersAccountAdapter(itemDatas).notifyDataSetChanged()
                                        }
                                    }

                            }


                        }
                    }
                    )
                }


            }



        }
        binding.userRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.userRecycler.adapter = usersAdapter(this, userAccountWindowDatas)

        binding.sendButton.setOnClickListener{
            if (binding.chattingEditText.text.toString().isEmpty())
            {
                return@setOnClickListener
            }
            val currentTime=now()
            val newMessage = hashMapOf(
                "nickname" to userNickname,
                "text" to binding.chattingEditText.text.toString(),
                "time" to currentTime,
            )
            db.collection("chattingRoom_shdknm1hks")
                .add(newMessage)
            binding.userAccountWindow.visibility= View.GONE
            binding.chattingEditText.setText("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.chattingEditText.windowToken, 0)
            binding.messageRecycler.smoothScrollToPosition(binding.messageRecycler.adapter!!.itemCount - 1)
        }

        binding.fold.setOnClickListener {
            binding.userAccountWindow.visibility= View.GONE
        }

        binding.chattingEditText.setOnFocusChangeListener { v, hasFocus ->
            binding.userAccountWindow.visibility= View.GONE
        }


        binding.moveToAccount.setOnClickListener {

            if (userNickname==accountWindowUserName)
            {
                val userQuery = db.collection("users").whereEqualTo("nickname", accountWindowUserName).get().addOnSuccessListener{
                    val intent = Intent(this, CalendarActivity::class.java)
                    intent.putExtra("uid", it.documents[0].id)
                    intent.putExtra("nickname", it.documents[0].data?.get("nickname").toString())
                    startActivity(intent)
                }
            }
            else
            {
                val userQuery = db.collection("users").whereEqualTo("nickname", accountWindowUserName).get().addOnSuccessListener{
                    val intent = Intent(this, OthersCalendar::class.java)
                    intent.putExtra("uid", it.documents[0].id)
                    intent.putExtra("nickname", it.documents[0].data?.get("nickname").toString())
                    startActivity(intent)
                }
            }

        }
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