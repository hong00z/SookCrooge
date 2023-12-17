package com.example.sookcrooge

data class Chat(var chatName: String,var chatNum: String, var userName: String,var date:String)
{
    var documentID: String? =null
    fun addDocumentID(documentID: String)
    {
        this.documentID = documentID
    }

    constructor(chatName:String, chatNum:String, userName:String, date:String, documentID:String) : this(chatName, chatNum, userName, date) {
        this.documentID=documentID
    }

}
