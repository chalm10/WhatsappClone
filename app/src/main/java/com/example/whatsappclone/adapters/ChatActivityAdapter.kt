package com.example.whatsappclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.models.ChatEvent
import com.example.whatsappclone.models.DateHeader
import com.example.whatsappclone.models.Message
import com.example.whatsappclone.utils.formatAsTime
import kotlinx.android.synthetic.main.item_list_chatmessage_sent.view.*
import kotlinx.android.synthetic.main.item_list_date_header.view.*

class ChatActivityAdapter(val list: MutableList<ChatEvent> , val mCurrentUid : String ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MessageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    }

    class DateViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    }

    var likeBtnClicked : ((id : String , status : Boolean) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            TEXT_MESSAGE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_chatmessage_received,parent,false)
                return MessageViewHolder(view)
            }
            TEXT_MESSAGE_SENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_chatmessage_sent,parent,false)
                return MessageViewHolder(view)
            }
            DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_date_header,parent,false)
                return DateViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.empty_view,parent,false)
                return MessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(val item = list[position]){

            is DateHeader -> {
                holder.itemView.dateHeaderTv.text = item.dateHeader
            }
            is Message -> {
                holder.itemView.apply {
                    messageTv.text = item.msg
                    timeTv.text = item.sentAt.formatAsTime()
                    if (item.liked){
                        likeBtn.isVisible = item.liked
                    }else{
                        likeBtn.isVisible = item.liked
                    }

                    materialCardView.setOnClickListener(object : OnDoubleClickListener(){
                        override fun onDoubleClick(view: View?) {
                            likeBtnClicked?.invoke(item.msgId , true )
                        }
                    })
                    likeBtn.setOnClickListener {
                        likeBtnClicked?.invoke(item.msgId , false)
                    }

                }
            }
        }

    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when(val event = list[position]){
            is Message -> {
                if (event.senderId==mCurrentUid)
                    TEXT_MESSAGE_SENT
                else
                    TEXT_MESSAGE_RECEIVED
            }
            is DateHeader -> {
                DATE_HEADER
            }
            else -> {
                UNSUPPORTED
            }
        }
    }

    companion object{
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }


}
abstract class OnDoubleClickListener : View.OnClickListener {
    var lastClickTime : Long = 0   //time of first click
    override fun onClick(view: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime-lastClickTime< DOUBLE_CLICK_TIME_DELTA){
            onDoubleClick(view)
            lastClickTime = 0
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(view : View?)

    companion object{
        private const val DOUBLE_CLICK_TIME_DELTA : Long = 400  //milliseconds
    }
}

