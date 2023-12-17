package com.example.sookcrooge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sookcrooge.databinding.ActivityChatRoomBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ChatRoomActivity : AppCompatActivity() {
    lateinit var adapter: ChatRoomAdapter
    val fbdb = Firebase.firestore
    var datas: MutableList<String>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.chatRecycler.layoutManager = layoutManager
        fbdb.collection("rooms").get()
            .addOnCompleteListener { task ->
                for (i in task.result!!) {
                    datas?.add(i.data["chatName"].toString())
                    Log.d("ttttttttttttt", i.data["chatName"].toString())
                }

            }
        adapter = datas?.let { ChatRoomAdapter(it) }!!
        binding.chatRecycler.adapter = adapter
        binding.chatRecycler.addItemDecoration(
            DividerItemDecoration(this,LinearLayoutManager.VERTICAL)
        )

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.create_chat -> {
                val dialogView = layoutInflater.inflate(R.layout.chat_dialog,null)
                val alertDialog = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create()
                val chatName = dialogView.findViewById<EditText>(R.id.chat_name).text
                val chatNum= dialogView.findViewById<EditText>(R.id.chat_num).text

                val button1 = dialogView.findViewById<Button>(R.id.positiveButton)
                val button2 = dialogView.findViewById<Button>(R.id.cancel_button)

                button1.setOnClickListener{
                    Log.d("jhs", "test")
                    fbdb.collection("users").whereEqualTo("uid", loginInformation.currentLoginUser!!.uid).get().addOnSuccessListener{
                        val userName=it.documents[0].get("nickname").toString()
                        Log.d("jhs", userName)
                        fbdb.collection("rooms").document().set(Chat(chatName.toString(),chatNum.toString(),userName,"10.23"))
                            .addOnSuccessListener {
                                Toast.makeText(this,"채팅방 개설 성공", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"채팅방 개설 실패", Toast.LENGTH_SHORT).show()
                            }
                        alertDialog.dismiss()
                    }

                }
                button2.setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}