package com.codingwithmitch.googlemaps2018.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingwithmitch.googlemaps2018.R


import java.util.ArrayList

class UserRecyclerAdapter(users: ArrayList<User>) :
    RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>() {

    private var mUsers = ArrayList<User>()


    init {
        mUsers = users
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_user_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.username.setText(mUsers[position].username)
        holder.email.setText(mUsers[position].email)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var username: TextView
        internal var email: TextView

        init {
            username = itemView.findViewById(R.id.username)
            email = itemView.findViewById(R.id.email)
        }


    }

}






