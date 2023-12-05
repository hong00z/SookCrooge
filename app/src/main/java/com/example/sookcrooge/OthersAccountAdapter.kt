package com.example.sookcrooge

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.AccountListBinding
import com.example.sookcrooge.databinding.MyAccountBookBinding

class OthersViewHolder(val binding: AccountListBinding) : RecyclerView.ViewHolder(binding.root){
    var smile=false
    var angry=false
    //추후 구현: 클릭하면 파이어베이스에 angry, smile 1씩 올라가게 구현
    init {
        binding.smileIcon.setOnClickListener{
            if (smile)
            {
                return@setOnClickListener
            }
            binding.smileText.text = (binding.smileText.text.toString().toInt()+1).toString()
            smile=true
        }
        binding.angryIcon.setOnClickListener{
            if (angry)
            {
                return@setOnClickListener
            }
            binding.angryText.text = (binding.angryText.text.toString().toInt()+1).toString()
            angry=true
        }
        binding.angryText.setOnClickListener{
            Log.d("jhs", "angry click")
        }
    }
}
class OthersAccountAdapter(val datas: MutableList<accountItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
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

    }


}