package com.example.whatsappclone.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.*
import com.example.whatsappclone.models.Inbox
import com.example.whatsappclone.utils.formatAsListItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_list.view.*

private val db by lazy {
    FirebaseDatabase.getInstance()
}
private val mCurrentUid by lazy {
    FirebaseAuth.getInstance().currentUser!!.uid
}

class InboxFragmentAdapter(val list: MutableList<Inbox>) : RecyclerView.Adapter<InboxFragmentAdapter.InboxFragmentViewHolder>()  {

    class InboxFragmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(item: Inbox) {
            with(itemView){
//                if (item.count == 0){
//                    counterTv.isVisible = false
//                }
                counterTv.isVisible = (item.count>0)
                counterTv.text = item.count.toString()
                if (counterTv.isVisible){
                    timeTv.setTextColor(Color.parseColor("#03DAC5"))
                }else{
                    timeTv.setTextColor(Color.parseColor("#808080"))
                }

                timeTv.text = item.time.formatAsListItem(context)
                titleTv.text = item.name
                subTitleTv.text = item.msg
                Picasso.get()
                    .load(item.image)
                    .placeholder(R.drawable.defaultavatar)
                    .error(R.drawable.defaultavatar)
                    .into(userImgView)

                setOnClickListener {

                    //set the count to 0
                    db.reference.child("inbox/$mCurrentUid/${item.from}").child("count").setValue(0)

                    //start the chat activity
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra(ID, item.from)
                    intent.putExtra(NAME, item.name)
                    intent.putExtra(PHOTO_URL, item.image)
                    context.startActivity(intent)
                }



            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxFragmentViewHolder {
        return InboxFragmentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_inbox , parent , false))
    }

    override fun onBindViewHolder(holder: InboxFragmentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}