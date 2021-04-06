package com.example.whatsappclone.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.adapters.PeoplesFragmentAdapter
import com.example.whatsappclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_peoples.*

private const val TAG = "PeoplesFragment"
class PeoplesFragment : Fragment() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db by lazy {
        FirebaseFirestore.getInstance().collection("users")
    }
    private val list = arrayListOf<User>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_peoples , container , false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        peoplesRv.layoutManager = LinearLayoutManager(context)
        val madapter = PeoplesFragmentAdapter(list)
        peoplesRv.adapter = madapter

        val query = db.orderBy("name",Query.Direction.ASCENDING)

        //TODO :- implement pagination on static queries
        query.get()
            .addOnSuccessListener {
                
                for (document in it){
//                    val user = document.toObject(User::class.java)
                    Log.d(TAG , document.id)
                    val map = document.data
                    val user = User(map["name"] as String,
                        map["imageUrl"] as String,
                        map["thumbImageUrl"].toString(),
                        map["uid"].toString(), map["deviceToken"].toString(),
                        map["onlineStatus"].toString(), map["status"].toString()
                    )
                    //do not display the own user in the peoples list
                    if (auth.uid != user.uid){
                        list.add(user)
                    }
                }
                madapter.notifyDataSetChanged()
            }

//        db.collection("users")
////            .orderBy("name", Query.Direction.ASCENDING)
//            .addSnapshotListener { snapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
//                if (e != null) {
//                    Log.d(TAG, "Listen failed")
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null) {
//                    list.clear()
//                    for (document in snapshot) {
//                        Log.d(TAG, document.id)
//                        val map = document.data
//                        val user = User(
//                            map["name"] as String,
//                            map["imageUrl"] as String,
//                            map["thumbImageUrl"].toString(),
//                            map["uid"].toString(), map["deviceToken"].toString(),
//                            map["onlineStatus"].toString(), map["status"].toString()
//                        )
//                        list.add(user)
//                    }
//                    madapter.notifyDataSetChanged()
//                }
//                Log.d(TAG, list.toString())
//            }
//
//        //TODO: detach the realtime listener to "users" collection

    }


}

/**
 * pagination is implemented in displaying the full list of all users in a recycler view
 * assuming there are 1000 users each with atleast 10kb of uploaded data
 * fetching this 1000*10 kb of data in one single call will cause in [Network error] (too much data to be downloaded at a time)
 * hence data is sorted into different pages,supposing one call returning 20 users at a time
 *
 */
/**IMPLEMENTATION:-
 * an onScrollListener is attached to the recycler view and whenever the list is scrolled, the last position
 * of the list is found and if it is say =20 here, we fetch the next page of users
 */
/**
 * we could also use the jetpack library for pagination
 */
/**
 * we could also use the FireBase Firestore UI here since we are implementing firebase
 * (not recommended)
 */
