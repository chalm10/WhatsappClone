package com.example.whatsappclone.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.whatsappclone.R
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*


const val PHONE_NUMBER = "phoneNumber"
const val RC_HINT = 123
var RUN_ONCE = true

class LoginActivity : AppCompatActivity() {

    private lateinit var phoneNumber : String
    private lateinit var countryCode : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextPhone.addTextChangedListener {
            nextBtn.isEnabled = it!!.length >9
        }

        //setting up the hint request prompt
        if(RUN_ONCE){
            showPhoneNumberHintRequest()
            RUN_ONCE = false
        }

        nextBtn.setOnClickListener {
            checkNumber()
        }

    }

    private fun showPhoneNumberHintRequest() {
        val credential = Credentials.getClient(this)
        val hintRequest = HintRequest.Builder()
            .setHintPickerConfig(
                CredentialPickerConfig.Builder()
                    .setShowCancelButton(true)
                    .build()
            )
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = credential.getHintPickerIntent(hintRequest)
        startIntentSenderForResult(intent.intentSender,
            RC_HINT,null,0,0,0)

    }

    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + editTextPhone.text.toString()

        notifyUser()

    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number: $phoneNumber\n" +
            "Is this OK or would you like to edit the number?")
                .setPositiveButton("OK") { _, _ ->
                    showOTPActivity()
                }
                .setNegativeButton("Edit"){ dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }
    }

    private fun showOTPActivity() {
        //display the otp activity
        startActivity(Intent(this , OtpActivity::class.java).putExtra(
            PHONE_NUMBER, phoneNumber))
        finish()
        //finishing this activity to remove it from backstack
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                val credential =  data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                val length = ccp.selectedCountryCodeWithPlus.length
                val number = credential?.id?.substring(length)
                editTextPhone.setText(number)
            }
        }
    }
}