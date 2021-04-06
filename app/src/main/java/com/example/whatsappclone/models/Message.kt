package com.example.whatsappclone.models

import android.content.Context
import com.example.whatsappclone.utils.formatAsHeader
import java.util.*

interface ChatEvent{
    val sentAt : Date
}

data class Message(
    val msg : String,
    val msgId : String,
    val senderId : String,
    val liked : Boolean = false,
    val type : String = "TEXT",
    val status : Int = 1,                 //checking for msg sent,delivered,read...(NOT IMPLEMENTED)
    override val sentAt: Date = Date()
    ) : ChatEvent{
    constructor() : this("","","",false,"",1,Date())
}

//class for implementing the date headers found in whatsApp while scrolling through chats
data class DateHeader(
    override val sentAt: Date = Date(),
    val context: Context
) : ChatEvent{
    val dateHeader = sentAt.formatAsHeader(context)
}