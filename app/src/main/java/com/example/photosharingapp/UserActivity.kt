 package com.example.photosharingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.photosharingapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

 class UserActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding
     private lateinit var auth: FirebaseAuth


     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         enableEdgeToEdge()
         binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)
         auth = FirebaseAuth.getInstance()

         val currentUser = auth.currentUser
         if (currentUser != null) {
             val intent = Intent(this, NewsActivity::class.java)
             startActivity(intent)
             finish()

         }

         fun signIn(view: View) {
             val email = binding.emailText.text.toString()
             val password = binding.passwordText.text.toString()

             auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                 if (it.isSuccessful) {
                     val currentUser = auth.currentUser?.email.toString()
                     Snackbar.make(view, "Welcome $currentUser", Snackbar.LENGTH_LONG).show()
                     val intent = Intent(this, NewsActivity::class.java)
                     startActivity(intent)
                     finish()
                 }
             }.addOnFailureListener {
                 Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
             }
         }


         fun signUp(view: View) {
             val email = binding.emailText.text.toString()
             val password = binding.passwordText.text.toString()

             auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                 if (it.isSuccessful) {
                     Snackbar.make(view, "Welcome to Photo Sharing App", Snackbar.LENGTH_LONG)
                         .show()
                     val intent = Intent(this, NewsActivity::class.java)
                     startActivity(intent)
                     finish()
                 }
             }.addOnFailureListener {
                 Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
             }

         }

     }
 }