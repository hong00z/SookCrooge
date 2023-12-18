package com.example.sookcrooge

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sookcrooge.databinding.FragmentChat2Binding
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment2.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment2 : Fragment() {
    val db = Firebase.firestore
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding2 = FragmentChat2Binding.inflate(layoutInflater,container,false)
        val userName=loginInformation.currentLoginUser!!.nickname.toString()
        val chatData = mutableListOf<Chat>()
        db.collection("rooms")
            .addSnapshotListener { value, e ->

                if (e != null) {
                    Log.w("CHAT", "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                                if (dc.document.data["userName"].toString().contains(userName))
                                {
                                    val currentData = Chat(
                                        dc.document.data["chatName"].toString(),
                                        dc.document.data["chatNum"].toString(),
                                        dc.document.data["userName"].toString(),
                                        dc.document.data["date"].toString())
                                    currentData.addDocumentID(dc.document.data["documentID"].toString())
                                    chatData.add(currentData)
                                    val layoutManager = LinearLayoutManager(activity)
                                    binding2.recyclerChat2.layoutManager = layoutManager
                                    val adapter = ChatAdapter(chatData)
                                    binding2.recyclerChat2.adapter = adapter
                                }
                        }

                        else -> {}
                    }
                }
            }

        return binding2.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}