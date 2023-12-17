package com.example.sookcrooge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sookcrooge.databinding.ChattingUsersBinding
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class userViewHolder(val binding: ChattingUsersBinding) : RecyclerView.ViewHolder(binding.root)
{

}
class usersAdapter(val context: Context, val datas: MutableList<userItem>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var itemClickListener : OnItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        userViewHolder(ChattingUsersBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        )

    override fun getItemCount(): Int = datas.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as userViewHolder).binding
        binding.userName.text = datas[position].nickname
        if (datas[position].photoURL!=null)
        {
            val imgRef = Firebase.storage.reference.child(datas[position].photoURL!!)
            imgRef.downloadUrl.addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Glide.with(context).load(task.result).into(binding.userImage)
                }
            }
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

}

class userItem(nickname:String, photoURL:String?)
{
    val nickname: String
    val photoURL: String?
    init {
        this.nickname=nickname
        this.photoURL=photoURL
    }
}