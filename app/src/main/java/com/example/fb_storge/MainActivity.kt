package com.example.fb_storge

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URL


class MainActivity : AppCompatActivity() {
    lateinit var storageReference:StorageReference
    lateinit var databaseReference:DatabaseReference
    lateinit var ref:StorageReference
    lateinit var et:EditText
    var imgUri: Uri? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
          et=findViewById(R.id.editTextTextPersonName)
         storageReference =FirebaseStorage.getInstance().reference
         databaseReference=FirebaseDatabase.getInstance().getReference("Uploads")
        findViewById<Button>(R.id.button).setOnClickListener {
            if(!et.text.isEmpty()) {
                selectFiles()
            }else {
                et.setError("Please put your fiel name")
            }
        }
        findViewById<Button>(R.id.button2).setOnClickListener {
            download()
        }

    }

    private fun download() {
        et=findViewById(R.id.editTextTextPersonName)
        var Fname=et.text.toString()
        storageReference=FirebaseStorage.getInstance().getReference()
        ref=storageReference.child("$Fname.pdf")
        ref.downloadUrl.addOnCompleteListener {
            var url =it.result.toString()
          downloadFile(this,"$Fname",".pdf",DIRECTORY_DOWNLOADS,url)
          Toast.makeText(this,"Download Succeded",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
        }

    }

    private fun downloadFile(context: Context,fileName:String,fileExtention:String,distansDirctory:String,url:String) {
        var downloadmanager =context.applicationContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        var uri = Uri.parse(url)
        var request = DownloadManager.Request(uri)
        request.setNotificationVisibility (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir (context, distansDirctory, fileName + fileExtention);

        downloadmanager.enqueue (request)
    }

    private fun selectFiles() {
     var intent = Intent()
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT

        galleryIntent.type = "application/pdf"
        startActivityForResult(galleryIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==1&&resultCode== RESULT_OK&&data!=null&&data.data!=null){
            var dialog=ProgressDialog(this)
            dialog.setMessage("Uploading..")
            dialog.show()
            imgUri=data.data!!
            Toast.makeText(this, imgUri.toString(), Toast.LENGTH_SHORT).show()
            val filepath =storageReference.child(findViewById<EditText>(R.id.editTextTextPersonName).text.toString()
                    + "." + "pdf")
            Toast.makeText(this, filepath.getName(), Toast.LENGTH_SHORT).show()
            filepath.putFile(imgUri!!).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                    //************************************************
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    dialog.dismiss()
                    val uri = task.result
                    val myurl: String
                    myurl = uri.toString()
                    Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    dialog.dismiss()
                    Toast.makeText(this, "UploadedFailed", Toast.LENGTH_SHORT).show()

                }
            }

            }
        }
    }







