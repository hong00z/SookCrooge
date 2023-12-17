package com.example.sookcrooge

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build.VERSION_CODES.M
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.MyAccountBookBinding

class MyViewHolder(val binding: AccountListBinding) : RecyclerView.ViewHolder(binding.root)

class AccountAdapter(val m_datas: MutableList<String>,val p_datas: MutableList<String>,
    val c_datas: MutableList<String>, val t_datas: MutableList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(AccountListBinding.inflate(LayoutInflater.from(parent.context), parent,false))

    override fun getItemCount(): Int =
        m_datas.size


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        val imageView = binding.root.findViewById<ImageView>(R.id.tag_img)
        binding.accountName.text = m_datas[position]
        if(c_datas[position] == "save"){
            binding.accountPrice.setTextColor(Color.parseColor("#0000FF"))
            binding.accountPrice.textSize = 20F
            binding.accountPrice.text = "+"+p_datas[position]
        }else if(c_datas[position] == "spend"){
            binding.accountPrice.textSize = 20F
            binding.accountPrice.text ="-"+p_datas[position]
        }
        when(t_datas[position]){
            "식비" ->imageView.setImageResource(R.drawable.meal_icon)
            //공과금, 등등 추가하기
        }







    }



}