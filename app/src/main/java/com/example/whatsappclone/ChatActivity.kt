package com.example.whatsappclone

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adapters.ChatActivityAdapter
import com.example.whatsappclone.models.*
import com.example.whatsappclone.utils.isSameDayAs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*


//NAME,UID,IMAGE_URL
const val NAME = "name"
const val ID = "uid"
const val PHOTO_URL = "photo"

class ChatActivity : AppCompatActivity() {

    private val friendId by lazy {
        intent.getStringExtra(ID)
    }
    private val friendName by lazy {
        intent.getStringExtra(NAME)
    }
    private val friendPhoto by lazy {
        intent.getStringExtra(PHOTO_URL)
    }
    private val mCurrentUid by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }
    private val db by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser : User
    private val messagesList = mutableListOf<ChatEvent>()
    lateinit var chatAdapter : ChatActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
//        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            //set back to the activity which launched me
            finish()
        }
        //emoji keyboard support
//        EmojiManager.install(GoogleEmojiProvider())

        //TODO make use of the emojiedittext dependency to use the emojis keyboard functionality(emojiedittext)

        Picasso.get()
            .load(friendPhoto)
            .placeholder(R.drawable.defaultavatar)
            .error(R.drawable.defaultavatar)
            .into(userImgView)
        nameTv.text = friendName

//        val emojiPopup = EmojiPopup.Builder.fromRootView(rootRl).build(msgEt)
//        emojiBtn.setOnClickListener{
//            emojiPopup.toggle()
//        }


        //get the details of currently signed in user
        FirebaseFirestore.getInstance().collection("users").document(mCurrentUid)
            .get().addOnSuccessListener {
                currentUser = it.toObject(User::class.java)!!
            }
        chatAdapter = ChatActivityAdapter(messagesList, mCurrentUid)
        msgRv.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter

        }

        listenToMessages()

        chatAdapter.likeBtnClicked = { id: String, status: Boolean ->
            //update msg in database
            getMessages(friendId).child(id).updateChildren(mapOf("liked" to status))
        }

        msgSendBtn.setOnClickListener{
            //store msgs in firebasedatabase
            msgEt.text?.let {
                if (it.isNotEmpty()){
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }


        //mark the current chat as read since it is opened
//        markAsRead()


    }

    private fun listenToMessages() {
        getMessages(friendId)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("TAG", "listener called")
                    val msg = snapshot.getValue(Message::class.java)!!
                    addMessageToUI(msg)

//                markAsRead()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue(Message::class.java)!!
                    var position: Int = 0
                    for (item in messagesList) {
                        if (item is Message) {
                            if (item.msgId == msg.msgId) {
                                position = messagesList.indexOf(item)
                            }
                        }
                    }
                    messagesList[position] = msg
                    chatAdapter.notifyItemChanged(position)

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}

            })

    }

    private fun addMessageToUI(msg: Message) {
        val lastChatEvent = messagesList.lastOrNull()

        if ((lastChatEvent!=null && !lastChatEvent.sentAt.isSameDayAs(msg.sentAt)) || lastChatEvent==null){
            //to add the header
            messagesList.add(
                DateHeader(msg.sentAt, context = this)
            )
        }
        //add the message
        messagesList.add(msg)
//
//        chatAdapter.notifyDataSetChanged()
        chatAdapter.notifyItemInserted(messagesList.size - 1)

        //scroll to bottom of chat if you sent the msg
        if (msg.senderId==mCurrentUid){
            msgRv.scrollToPosition(messagesList.size - 1)
        }


    }

    private fun sendMessage(msg: String) {
        val msgId = getMessages(friendId).push().key
        if (msgId!=null){
            val message = Message(
                msg,
                msgId,
                mCurrentUid
            )                //senderId=mCurrentUid
            getMessages(friendId).child(msgId).setValue(message)        //storing all msgs in messages

            uploadLastMessage(message)                              //storing last msgs in inbox



        }else{
            throw IllegalStateException("Cannot be null")
        }
    }

    private fun uploadLastMessage(message: Message) {

        val inbox = Inbox(
            message.msg,
            friendId,
            friendName,
            friendPhoto,
            count = 0
        )
        getInbox(mCurrentUid, friendId).setValue(inbox).addOnSuccessListener {

            getInbox(friendId, mCurrentUid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Inbox::class.java)
                    val inbox = Inbox(
                        message.msg,
                        mCurrentUid,
                        currentUser.name,
                        currentUser.thumbImageUrl,
                        1
                    )

                    //called only when value is not null
                    value?.let {
                        if (it.from == mCurrentUid) {
                            inbox.count = it.count + 1
                        }
                    }

                    getInbox(friendId, mCurrentUid).setValue(inbox)

                }
            })
        }
    }



    private fun getMessages(friendID: String) =
        db.reference.child("messages/${getId(friendID)}")

    private fun getInbox(toUser: String, fromUser: String) =
        db.reference.child("inbox/$toUser/$fromUser")

    private fun getId(friendID: String) : String{         //ID for the messages
        //for the id to remain same during the back and forth msgs from 2 users
        return if (mCurrentUid>friendID){
            mCurrentUid + friendID
        }else{
            friendID + mCurrentUid
        }
    }

    private fun markAsRead(){
        //set count to 0
        getInbox(mCurrentUid, friendId).child("count").setValue(0)
    }



}