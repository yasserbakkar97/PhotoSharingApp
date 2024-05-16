package com.example.photosharingapp.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photosharingapp.databinding.ActivitySharingPhotoBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class SharingPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharingPhotoBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    var imageUri: Uri? = null
    var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharingPhotoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()


    }
    fun shareImage(view: View) {

        val uuid = UUID.randomUUID()
        val imageName = "images/$uuid.jpg"

        val reference = storage.reference
        val visualReference = reference.child(imageName)

        if (imageUri != null) {
            visualReference.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                val uploadedImageReference = FirebaseStorage.getInstance().reference.child(imageName)
                uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val currentUserEmail = auth.currentUser!!.email.toString()
                    val userComment = binding.etComment.text.toString()
                    val date = Timestamp.now()
                    //Database
                    val postMap = hashMapOf<String, Any>()
                    postMap.put("downloadUrl", downloadUrl)
                    postMap.put("userEmail", currentUserEmail)
                    postMap.put("userComment", userComment)
                    postMap.put("date", date)

                    database.collection("Post").add(postMap).addOnSuccessListener { task ->
                        println("Post is uploaded")
                        finish()
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                        println("Post upload failed: ${exception.message}")
                    }

                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                println("Image upload failed: ${exception.message}")
            }
        } else {
            println("imageUri is null")
        }
    }

    fun chooseImage(view: View) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            Log.d("permission", "permission denied")
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 ){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){

            imageUri = data.data

            if (imageUri != null){

                if (Build.VERSION.SDK_INT >= 28){
                    val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                    imageBitmap = ImageDecoder.decodeBitmap(source)
                    binding.imageView.setImageBitmap(imageBitmap)
                }

                imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                binding.imageView.setImageBitmap(imageBitmap)
            }


        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}