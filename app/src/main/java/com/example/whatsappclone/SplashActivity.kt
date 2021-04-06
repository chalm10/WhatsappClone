package com.example.whatsappclone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappclone.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * ACTIVITY USED TO CHECK FOR USER LOGGED IN OR NOT
 * AND TO LAUNCH THE APPROPRIATE ACTIVITY
 */
private const val TAG = "SplashActivity"
class SplashActivity : AppCompatActivity() {

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser==null){
            startActivity(Intent(this, LoginActivity::class.java))
            Log.d(TAG, "new user , id= ${auth.uid}")
        }else{
            startActivity(Intent(this, MainActivity::class.java))
            Log.d(TAG, "old user with  , id= ${auth.uid}")
        }

//        if (auth.currentUser!=null&&!auth.currentUser!!.displayName.isNullOrEmpty()){
//            startActivity(Intent(this, MainActivity::class.java))
//            Log.d(TAG, "old user with  , id= ${auth.uid}")
//        }else{
//            startActivity(Intent(this, LoginActivity::class.java))
//            Log.d(TAG, "new user , id= ${auth.uid}")
//        }
        finish()
    }

}