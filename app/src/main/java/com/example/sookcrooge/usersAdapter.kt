package com.example.sookcrooge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.ActivityChattingBinding
import com.example.sookcrooge.databinding.ChattingUsersBinding

class userViewHolder(val binding: ChattingUsersBinding) : RecyclerView.ViewHolder(binding.root)
{

}
class usersAdapter(val datas: MutableList<String>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        userViewHolder(
            ChattingUsersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = datas.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as userViewHolder).binding
        binding.userName.text = datas[position]
    }
}