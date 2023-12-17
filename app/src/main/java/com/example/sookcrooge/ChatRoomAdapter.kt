package com.example.sookcrooge

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.ChatRecyclerBinding

class ChatRoomViewHolder(val binding: ChatRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

class ChatRoomAdapter(val datas: MutableList<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    =ChatRoomViewHolder(ChatRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun getItemCount(): Int =
        datas.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ChatRoomViewHolder).binding
        binding.chatName.text = datas[position]

        if (position%2!=0){
            binding.chatList.setBackgroundColor(Color.parseColor("#e9ecef"))
        }

    }


}