package com.example.sookcrooge

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sookcrooge.databinding.ChatListBinding
import com.example.sookcrooge.databinding.ChatRecyclerBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore
import java.time.LocalDate

class ChatActivity : AppCompatActivity() {
    val db = Firebase.firestore

    class MyFragmentPagerAdapter(activity: FragmentActivity):
            FragmentStateAdapter(activity){
            val fragments: List<Fragment>
            init {
                fragments= listOf(ChatFragment1(),ChatFragment2())
            }
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tabTitles = listOf("모든 채팅방","내 채팅방")

        setSupportActionBar(binding.allChatToolbar)
        //viewPager 코드
        binding.viewpager.adapter = MyFragmentPagerAdapter(this)

        //tablayout
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.tabs.setTabTextColors(Color.parseColor("#FFFFFFFF"),
            Color.parseColor("#FFC61A"))

        setSupportActionBar(binding.allChatToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.create_chat -> {
                val dialogView = layoutInflater.inflate(R.layout.chat_dialog,null)
                val alertDialog = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create()
                val chatName = dialogView.findViewById<EditText>(R.id.chat_name).text
                val chatNum= dialogView.findViewById<EditText>(R.id.chat_num).text
                val userName = "asd123@naver.com"
                val button1 = dialogView.findViewById<Button>(R.id.positiveButton)
                val button2 = dialogView.findViewById<Button>(R.id.cancel_button)
                val currentDate = LocalDate.now()

                button1.setOnClickListener{
                    alertDialog.dismiss()
                    val chattingRoom=Chat(chatName.toString(),chatNum.toString(),userName,currentDate.toString())
                    db.collection("rooms")
                        .add(chattingRoom)
                        .addOnSuccessListener {
                            chattingRoom.addDocumentID(it.id)
                            it.update("documentID", it.id).addOnSuccessListener {
                                Log.d("TEST", "DocumentSnapshot successfully written!")
                            }
                        }
                        .addOnFailureListener { e -> Log.w("TEST", "Error writing document", e) }
                }
                button2.setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.show()
            }
            // 뒤로가기
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }





}