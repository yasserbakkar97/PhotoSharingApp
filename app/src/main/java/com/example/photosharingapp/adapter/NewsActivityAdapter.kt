package com.example.photosharingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photosharingapp.R
import com.example.photosharingapp.databinding.RecyclerRowBinding
import com.example.photosharingapp.model.Post
import com.squareup.picasso.Picasso

class NewsActivityAdapter(val postList: ArrayList<Post>) : RecyclerView.Adapter<NewsActivityAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        //val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.tvUserEmail.text = postList[position].userEmail
        holder.binding.tvUserComment.text = postList[position].userComment
        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.ivImage)


    }

}