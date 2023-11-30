package com.example.sookcrooge

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.ChatListBinding
import com.example.sookcrooge.databinding.ChatRecyclerBinding
import com.example.sookcrooge.databinding.FragmentChat1Binding

class ChatViewHolder(val binding: ChatRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
class ChatAdapter (val datas: MutableList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ChatViewHolder(ChatRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int =
        datas.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ChatViewHolder).binding
        binding.chatName.text = datas[position]

        if (position%2!=0){
            binding.chatList.setBackgroundColor(Color.parseColor("#e9ecef"))
        }


    }

}

