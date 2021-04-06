package com.example.whatsappclone.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.whatsappclone.MainActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*



const val RC_PERMISSION_STORAGE = 123
const val RC_IMAGE_PICK = 100
private const val TAG = "SignUpActivity"

class SignUpActivity : AppCompatActivity() {

    lateinit var downloadUrl: String
    lateinit var resizedDownloadUrl: String
    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }


//    val resizedDownloadUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        progressBar.isVisible = false

        userImgView.setOnClickListener {
            checkForImagePermission()
        }

        nextBtn.setOnClickListener {
            nextBtn.isEnabled = false
            progressBar.isVisible = true
            val name = nameEt.text.toString()


            if (name.isEmpty()){
                nextBtn.isEnabled = true
                progressBar.isVisible = false
                Toast.makeText(this , "Please enter your Name first", Toast.LENGTH_SHORT).show()

            }else if (!::downloadUrl.isInitialized){
                //no image set hence no download url found
                nextBtn.isEnabled = true
                progressBar.isVisible = false
                Toast.makeText(this , "Please Select an Image", Toast.LENGTH_SHORT).show()

            }else{

                val user = User(
                    name,
                    downloadUrl,
                    downloadUrl,
                    auth.uid.toString()
                )
                database.collection("users").document(auth.uid!!).set(user)
                    .addOnSuccessListener{
                        progressBar.isVisible = false
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }.addOnFailureListener {
                        progressBar.isVisible = false
                        nextBtn.isEnabled = true
                    }
            }
        }


    }

    private fun checkForImagePermission() {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {

            val permission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissions(permission,
                RC_PERMISSION_STORAGE
            )

//            val permissionRead = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
//            val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            requestPermissions(permissionRead , 1000)
//            requestPermissions(permissionWrite , 1001)

        } else {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
//        Toast.makeText(this , "going to gallery", Toast.LENGTH_LONG).show()
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(intent,
            RC_IMAGE_PICK
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE_PICK) {
            if (resultCode == Activity.RESULT_OK) {
                val imgUri = data?.data
                userImgView.setImageURI(imgUri)
                if (imgUri != null) {
                    uploadImage(imgUri)
                }



                Log.d(TAG, imgUri.toString())
                Log.d(TAG, imgUri?.path!!)

            }
        }
    }

    private fun uploadImage(uri: Uri) {

        nextBtn.isEnabled = false
        progressBar.isVisible = true

        val storageRef = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = storageRef.putFile(uri)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot , Task<Uri>> {
            if (!it.isSuccessful) {
                throw it.exception!!
            } else {
                return@Continuation storageRef.downloadUrl
            }
        }).addOnCompleteListener {

//            getResizedImageUrl() //TODO : implement the fetching of resized image's url
            progressBar.isVisible = false
            nextBtn.isEnabled = true
            if (it.isSuccessful){
                downloadUrl = it.result.toString()
                Log.d(TAG, "downloadURL : $downloadUrl")
            }else{

            }
        }.addOnFailureListener {
            progressBar.isVisible = false
            userImgView.setImageResource(R.drawable.defaultavatar)
            Toast.makeText(this, "Uploading your Image failed, pls try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getResizedImageUrl() {

        val storageRef = storage.reference.child("uploads/resize/"+auth.uid.toString()+"_400x400")
        storageRef.downloadUrl.addOnSuccessListener {
            resizedDownloadUrl = it.toString()
            Log.d(TAG, "resizedDownloadUrl:$resizedDownloadUrl")
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                Toast.makeText(
                    this,
                    "We need access to gallery to assign a profile picture",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
    }

}

    //private StorageReference mStorageRef;
    //mStorageRef = FirebaseStorage.getInstance().getReference();
    //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
    //StorageReference riversRef = storageRef.child("images/rivers.jpg");
    //
    //riversRef.putFile(file)
    //.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    //    @Override
    //    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
    //        // Get a URL to the uploaded content
    //        Uri downloadUrl = taskSnapshot.getDownloadUrl();
    //    }
    //})
    //.addOnFailureListener(new OnFailureListener() {
    //    @Override
    //    public void onFailure(@NonNull Exception exception) {
    //        // Handle unsuccessful uploads
    //        // ...
    //    }
    //});