package com.example.sookcrooge


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.integrity.internal.aa
import java.text.SimpleDateFormat

data class MessageModel(val type: Int, val userName: String, val messageText: String, val timeStampLong: Long) {
    companion object {
        const val MY_MESSAGE = 0
        const val OTHERS_MESSAGE = 1
    }
}
class messageAdapter(val context: Context, private val dataset: List<MessageModel>): RecyclerView.Adapter<messageAdapter.messageViewHolder>() {

    class messageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.username)
        val messageText: TextView = view.findViewById(R.id.messageText)
        val hour: TextView=view.findViewById(R.id.hour)
        val min: TextView=view.findViewById(R.id.min)
    }
    override fun getItemViewType(position: Int): Int {
        return dataset[position].type
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): messageViewHolder {

        val view: View?
        return when (viewType) {
            MessageModel.MY_MESSAGE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.my_message, parent, false)
                messageViewHolder(view)
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.others_message, parent, false)
                messageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: messageViewHolder, position: Int) {
        val item = dataset[position]
        holder.userName.text = item.userName
        holder.messageText.text = item.messageText
        holder.hour.text= SimpleDateFormat("HH").format(item.timeStampLong)
        holder.min.text= SimpleDateFormat("mm").format(item.timeStampLong)
    }

    override fun getItemCount(): Int {
        return dataset.size }

}
