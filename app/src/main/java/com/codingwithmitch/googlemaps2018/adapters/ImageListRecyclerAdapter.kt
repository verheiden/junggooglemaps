package com.codingwithmitch.googlemaps2018.adapters

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.googlemaps2018.R
import java.util.*

class ImageListRecyclerAdapter(private val mContext : Context,
     images : ArrayList<Int>,
     private val mImageListRecyclerClickListener: ImageListRecyclerClickListener):
     RecyclerView.Adapter<ImageListRecyclerAdapter.ViewHolder>()
    {
      override fun getItemCount(): Int = mImages.size

    private var mImages = ArrayList<Int>()

    init {
        mImages = images
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_image_list_item, parent, false)
        return ViewHolder(view, mImageListRecyclerClickListener)
    }

    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int)
    {
        val requestOptions  = RequestOptions()
            .placeholder(R.drawable.cwm_logo)
            .error(R.drawable.cwm_logo)
        Glide.with(mContext)
            .setDefaultRequestOptions(requestOptions)
            .load(mImages[position])
            .into(holder.image)
    }
    inner class ViewHolder(itemView : View,
        internal var mClickListener : ImageListRecyclerClickListener):
        RecyclerView.ViewHolder(itemView), View.OnClickListener{
        internal var image : ImageView
        init {
            image = itemView.findViewById(R.id.image)
            itemView.setOnClickListener(this)
        }
        override fun onClick(v : View)
        {
            mClickListener.onImageSelected(adapterPosition)
        }
    }
    interface ImageListRecyclerClickListener {
        fun onImageSelected(position: Int)
    }
}

