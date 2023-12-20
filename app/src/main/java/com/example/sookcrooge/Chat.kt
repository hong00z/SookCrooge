package com.example.sookcrooge

data class Chat(var chatName: String,var userName: String,var date:String)
{
    var documentID: String? =null

    fun addDocumentID(documentID: String)
    {
        this.documentID = documentID
    }

    constructor(chatName:String, userName:String, date:String, documentID:String) : this(chatName, userName, date) {
        this.documentID=documentID
    }

}
