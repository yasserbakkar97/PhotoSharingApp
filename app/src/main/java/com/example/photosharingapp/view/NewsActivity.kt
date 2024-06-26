package com.example.photosharingapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photosharingapp.model.Post
import com.example.photosharingapp.R
import com.example.photosharingapp.adapter.NewsActivityAdapter
import com.example.photosharingapp.databinding.ActivityNewsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter: NewsActivityAdapter
    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getData()

        var layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager
        recyclerViewAdapter = NewsActivityAdapter(postList)
        binding.recyclerview.adapter = recyclerViewAdapter


    }
    fun getData(){
        
        database.collection("Post").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
            if (error != null){
                Toast.makeText(applicationContext, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else{
                if (value != null){
                    if (!value.isEmpty){

                        postList.clear()
                        val documents = value.documents
                        for (document in documents){
                            val comment = document.get("userComment") as String
                            val email = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post = Post(email, comment, downloadUrl)
                            postList.add(post)
                        }

                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sharingPhoto) {
            val intent = Intent(this, SharingPhotoActivity::class.java)
            startActivity(intent)
        }else if(item.itemId == R.id.signOut){
            auth.signOut()
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()

        }
        return super.onOptionsItemSelected(item)
    }

}