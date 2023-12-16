package com.example.sookcrooge

import android.os.Build.VERSION_CODES.M
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.MyAccountBookBinding

class MyViewHolder(val binding: AccountListBinding) : RecyclerView.ViewHolder(binding.root)
class AccountAdapter(val m_datas: MutableList<String>,val p_datas: MutableList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(AccountListBinding.inflate(LayoutInflater.from(parent.context), parent,false))

    override fun getItemCount(): Int =
        m_datas.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.accountName.text = m_datas[position]
        binding.accountPrice.text = p_datas[position]
    }



}