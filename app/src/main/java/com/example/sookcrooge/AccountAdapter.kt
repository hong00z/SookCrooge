package com.example.sookcrooge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.MyAccountBookBinding

class MyViewHolder(val binding: AccountListBinding) : RecyclerView.ViewHolder(binding.root)
class AccountAdapter(val datas: MutableList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(AccountListBinding.inflate(LayoutInflater.from(parent.context), parent,false))

    override fun getItemCount(): Int =
        datas.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.accountName.text = datas[position]
    }


}