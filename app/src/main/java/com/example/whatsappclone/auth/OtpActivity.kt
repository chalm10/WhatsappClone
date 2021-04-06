package com.example.whatsappclone.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.whatsappclone.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

private const val TAG = "otpactivity"

class OtpActivity : AppCompatActivity(), View.OnClickListener {

//    var progressBar : ProgressBar? = null

    private lateinit var phoneNumber : String
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mverificationId : String? = null
    var resendToken :PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        progressBar.isVisible = false
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)!!
        initViews()
        startVerification()
        verificationBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)



    }

    private fun startVerification() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks

        showTimer()
        progressBar.isVisible = true



    }

    private fun initViews() {
//        verifyTv.text = "Verify $phoneNumber"
//        or
        verifyTv.text = getString(R.string.verify_number,phoneNumber)
        setSpannableString()

        //callbacks to hear results from the verification
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                progressBar.isVisible = false


                val otp = credential.smsCode
                sentCodeEt.setText(otp)

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
                Log.d(TAG, e.localizedMessage!!.toString())
                progressBar.isVisible = false


                notifyUserAndRetry("OTP Verification failed , please try again!!")

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                progressBar.isVisible = false

                // Save verification ID and resending token so we can use them later
                mverificationId = verificationId
                resendToken = token

                //we create the credential here in verificationBtn.SetOnClickListener{}
                Snackbar.make(ll ,"Enter the OTP Sent", Snackbar.LENGTH_LONG ).show()
                // ...
            }
        }


    }

        private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        //start with the profile activity
                        progressBar.isVisible = false
//                        val user = task.result?.user
//                        Toast.makeText(this , "SUCCESSFUL SIGN IN", Toast.LENGTH_SHORT).show()
                        showSignUpActivity()

                        // ...
                    } else {
                        // Sign in failed, display a message
                        progressBar.isVisible = false
                        notifyUserAndRetry("Phone Number Verification Failed, please try again!!")
                    }
                }
        }


    override fun onClick(v : View){
        when(v){
            verificationBtn -> {
                val otp = sentCodeEt.text.toString()
                if(otp.length==6 && mverificationId!=null){
                    progressBar.isVisible = true

                    val credential = PhoneAuthProvider.getCredential(mverificationId!!,otp)
                    signInWithPhoneAuthCredential(credential)
                }
            }

            resendBtn -> {
                if(resendToken!=null){
                    showTimer()
                    //resending code needs a resendToken
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        this, // Activity (for callback binding)
                        callbacks, // OnVerificationStateChangedCallbacks
                        resendToken)
                    Snackbar.make(ll ,"Resending OTP ", Snackbar.LENGTH_LONG ).show()
                    progressBar.isVisible = true


                }else{
                    Toast.makeText(this , "Sorry you cant request code now , please wait..", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun notifyUserAndRetry(text: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(text)
                .setPositiveButton("OK") { dialog , _ ->
                    showLoginActivity()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }

    }

    private fun showTimer() {
        resendBtn.isEnabled = false
        object : CountDownTimer(60000 , 1000){
            override fun onFinish() {
                counterTv.isVisible = false
                resendBtn.isEnabled = true
            }

            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished/1000
                counterTv.isVisible = true
                counterTv.text = "Seconds remaining: $seconds"
            }
        }.start()
    }

    private fun setSpannableString() {
        val span = SpannableString("Waiting to automatically detect an SMS sent to $phoneNumber  Wrong Number?")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //revert back to login activity
                showLoginActivity()
                //to remove it from backstack, we could also use flags in Intent.flags()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan, span.length-13,span.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span
    }

    private fun showLoginActivity(){
        startActivity(Intent(this@OtpActivity , LoginActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        ))
    }
    private fun showSignUpActivity() {
        startActivity(Intent(this , SignUpActivity::class.java))
        finish()
    }


}