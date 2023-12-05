package com.example.sookcrooge

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.motion.widget.Key.VISIBILITY
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sookcrooge.MessageModel.Companion.MY_MESSAGE
import com.example.sookcrooge.MessageModel.Companion.OTHERS_MESSAGE
import com.example.sookcrooge.databinding.ActivityChattingBinding
import com.example.sookcrooge.databinding.ChattingUsersBinding

class Chatting : AppCompatActivity() {
    val messageList = mutableListOf<MessageModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var datas = mutableListOf<String>("숙크루지1","숙크루지2", "숙크루지3", "숙크루지4")
        binding.userRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.userRecycler.adapter = usersAdapter(datas)

        var itemDatas = mutableListOf<String>("수박", "사과")
        binding.itemRecycler.layoutManager = LinearLayoutManager(this)
        binding.itemRecycler.adapter = AccountAdapter(itemDatas)

        binding.sendButton.setOnClickListener{
            binding.userAccountWindow.visibility= View.GONE
            messageList.add(MessageModel(MY_MESSAGE, "숙크루지",binding.chattingEditText.text.toString()))
            binding.chattingEditText.setText("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.chattingEditText.windowToken, 0)
            binding.messageRecycler.smoothScrollToPosition(binding.messageRecycler.adapter!!.itemCount - 1)
        }

        binding.fold.setOnClickListener {
            binding.userAccountWindow.visibility= View.GONE
        }

        messageList.add(MessageModel(OTHERS_MESSAGE, "숙크루지1","Hello world!"))
        messageList.add(MessageModel(MY_MESSAGE, "숙크루지","안녕"))
        messageList.add(MessageModel(OTHERS_MESSAGE, "숙크루지","안녕!"))

        binding.messageRecycler.layoutManager = LinearLayoutManager(this)
        binding.messageRecycler.adapter = messageAdapter(this, messageList)
    }



}