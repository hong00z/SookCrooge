package com.example.sookcrooge

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sookcrooge.databinding.ChatRecyclerBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChatViewHolder(val binding: ChatRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
class ChatAdapter (val datas: MutableList<Chat>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val db = Firebase.firestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ChatViewHolder(ChatRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int =
        datas.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ChatViewHolder).binding
        binding.chatName.text = datas[position].chatName
        holder.itemView.setOnClickListener {
            datas[position].documentID?.let { it1 -> Log.d("jhs", it1) }
            val intent = Intent(holder.itemView.context,Chatting::class.java)
            val temp = db.collection("rooms").whereEqualTo("documentID", datas[position].documentID).get().addOnSuccessListener{
                val userNames=it.documents[0].data?.get("userName").toString()
                if (!userNames.contains(loginInformation.currentLoginUser!!.nickname.toString()))
                {
                    Log.d("jhs", userNames)
                    val newUserName=userNames+","+loginInformation.currentLoginUser!!.nickname.toString()
                    datas[position].documentID?.let { it1 -> db.collection("rooms").document(it1).update("userName", newUserName) }
                }
            }
            intent.putExtra("documentID", datas[position].documentID)
            intent.putExtra("chatName", datas[position].chatName)
            startActivity(holder.itemView.context,intent,null)
        }
        holder.itemView.setOnLongClickListener {

            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("채팅방 나가기")

            // 대화상자의 메시지를 설정
            builder.setMessage("채팅방을 나가시겠습니까?")

            // 대화상자의 확인 버튼을 설정
            builder.setPositiveButton("확인") { dialog, which ->
                val item = datas[position]
                db.collection("rooms").whereEqualTo("chatName",item)
                    .addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            Log.w("chatErr", "listen:error $e")
                            return@addSnapshotListener
                        }
                        for (document in snapshots!!.documentChanges) {
                            if (document.type == DocumentChange.Type.ADDED || document.type == DocumentChange.Type.MODIFIED) {

                                db.collection("rooms").document(document.document.id).delete()
                            }
                        }
                    }

            }

            // 대화상자의 취소 버튼을 설정
            builder.setNegativeButton("취소") { dialog, which ->

            }

            builder.show()

            true
        }



        if (position%2!=0){
            binding.chatList.setBackgroundColor(Color.parseColor("#e9ecef"))
        }
        else{
            binding.chatList.setBackgroundColor(Color.parseColor("#ffffff"))
        }


    }


}

