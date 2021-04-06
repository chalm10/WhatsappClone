package com.example.whatsappclone.models

import java.util.*

data class Inbox(
    val msg : String,           //latest msg to display on the chats fragment
    val from : String,          //uid of the friend
    var name : String,          //name of friend
    var image : String,
    var count : Int = 0,
    val time : Date = Date()
    ) {
    constructor():this("","","","",0, Date())
}