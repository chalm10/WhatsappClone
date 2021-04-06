package com.example.whatsappclone.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.*
import com.example.whatsappclone.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_list.view.*

//click on an itemview handled via extension functions

class PeoplesFragmentAdapter(val list: ArrayList<User> ) : RecyclerView.Adapter<PeoplesFragmentAdapter.PeoplesFragmentViewHolder>() {


    class PeoplesFragmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(user: User ) {                         //fun bind(user:User, onClick:(name:String , photo:String , id:String)->Unit){}

            with(itemView) {
                titleTv.text = user.name
                subTitleTv.text = user.status
                counterTv.isVisible = false
                timeTv.isVisible = false
                Picasso.get()
                    .load(user.thumbImageUrl)
                    .placeholder(R.drawable.defaultavatar)
                    .error(R.drawable.defaultavatar)
                    .into(userImgView)

                setOnClickListener {
//                    onClick.invoke(user.name , user.thumbImageUrl , user.uid)

                    val intent = Intent(context,ChatActivity::class.java)
                    intent.putExtra(ID, user.uid)
                    intent.putExtra(NAME, user.name)
                    intent.putExtra(PHOTO_URL, user.thumbImageUrl)
                    context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeoplesFragmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list , parent,false)
        return PeoplesFragmentViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PeoplesFragmentViewHolder, position: Int) {

        holder.bind(list[position]) //{ name: String, photo: String, id: String ->
//            val intent = Intent( , ChatActivity::class.java)
//            intent.putExtra("UID", id)
//            intent.putExtra("NAME", name)
//            intent.putExtra("IMAGE_URL", photo)
//            startActivity(intent)
//        }

    }


}
