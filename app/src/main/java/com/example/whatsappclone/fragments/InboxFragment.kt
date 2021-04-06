package com.example.whatsappclone.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.adapters.InboxFragmentAdapter
import com.example.whatsappclone.models.Inbox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_inbox.*


class InboxFragment : Fragment() {

    private val db by lazy {
        FirebaseDatabase.getInstance()
    }
    private val mCurrentUid by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }
    val list = mutableListOf<Inbox>()
    lateinit var madapter: InboxFragmentAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        madapter = InboxFragmentAdapter(list)
        inboxRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = madapter
        }


        val query = db.reference.child("inbox").child(mCurrentUid).orderByKey()
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TAG", "listener called")
                val chat = snapshot.getValue(Inbox::class.java)!!
                list.add(chat)
                madapter.notifyItemInserted(list.size - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chat = snapshot.getValue(Inbox::class.java)!!
                var position: Int = 0
                for (item in list) {
                    if (item.from == chat.from) {
                        position = list.indexOf(item)
                    }
                }
                //to make the most recent chats get on top
                val firstChat = list[0]
                list[position] = firstChat
                list[0] = chat
                madapter.notifyItemChanged(position)
                madapter.notifyItemChanged(0)
//
//                list[position] = chat
//                madapter.notifyItemChanged(position)

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //havent yet implemented deletion of chats, but still i deleted from firebasedatabase directly hence had to implement it temporarily
                val chat = snapshot.getValue(Inbox::class.java)!!
                var position: Int = 0
                for (item in list) {
                    if (item.from == chat.from) {
                        position = list.indexOf(item)
                    }
                }
                list.removeAt(position)
                madapter.notifyItemRemoved(position)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })


    }
}
