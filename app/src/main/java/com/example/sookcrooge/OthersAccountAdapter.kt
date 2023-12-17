package com.example.sookcrooge

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.MyAccountBookBinding

class OthersViewHolder(val binding: AccountListBinding) : RecyclerView.ViewHolder(binding.root){

}
class OthersAccountAdapter(private val datas: MutableList<accountItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private lateinit var itemClickListener : OnItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        OthersViewHolder(AccountListBinding.inflate(LayoutInflater.from(parent.context), parent,false))


    override fun getItemCount(): Int =
        datas.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val binding = (holder as OthersViewHolder).binding
        binding.accountName.text = datas[position].name
        if (datas[position].date[0]== '0')
        {
            binding.listDay.text=datas[position].date[1].toString()
        }
        else
        {
            binding.listDay.text = datas[position].date
        }
        if (datas[position].type=="spend")
        {
            binding.accountPrice.text="-"+datas[position].cost.toString()+"원"
        }
        else
        {
            binding.accountPrice.text="+"+datas[position].cost.toString()+"원"
            binding.accountPrice.setTextColor(Color.parseColor("#0083E2"))
        }
        binding.smileText.text=datas[position].smile.toString()
        binding.angryText.text=datas[position].angry.toString()
        when(datas[position].tag){
            "공과금"->binding.tagImg.setImageResource(R.drawable.tax_icon)
            "교통비"->binding.tagImg.setImageResource(R.drawable.traffic_icon)
            "식비"->binding.tagImg.setImageResource(R.drawable.meal_icon)
            else->binding.tagImg.setImageResource(R.drawable.etc_icon)
        }
        binding.angryIcon.setOnClickListener {
            itemClickListener.onAngryClick(binding, datas[position])
        }
        binding.smileIcon.setOnClickListener {
            itemClickListener.onSmileClick(binding, datas[position])
        }

    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onSmileClick(binding: AccountListBinding, data: accountItem)
        fun onAngryClick(binding: AccountListBinding, data: accountItem)
    }

}