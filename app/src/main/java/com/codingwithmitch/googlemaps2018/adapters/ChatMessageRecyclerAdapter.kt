package com.codingwithmitch.googlemaps2018.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import apps.com.codingwithmitch.googlemaps2018.models.User
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.models.ChatMessage

import com.google.firebase.auth.FirebaseAuth


class ChatMessageRecyclerAdapter(
    messages: ArrayList<ChatMessage>,
    users: ArrayList<User>,
    private val mContext: Context
) : RecyclerView.Adapter<ChatMessageRecyclerAdapter.ViewHolder>() {

    private var mMessages = ArrayList<ChatMessage>()
    private var mUsers = ArrayList<User>()

    override fun getItemCount(): Int =mMessages.size

    init {
        this.mMessages = messages
        this.mUsers = users
    }

   @NonNull
   override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int ) :
           ViewHolder {
       val view: View =
           LayoutInflater.from(parent.context)
               .inflate(R.layout.layout_chat_message_list_item, parent, false)
       return ViewHolder(view)
   }
       override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           if (FirebaseAuth.getInstance().uid == mMessages[position].user?.user_id) {
               holder.username.setTextColor(ContextCompat.getColor(mContext, R.color.green1))
           } else {
               holder.username.setTextColor(ContextCompat.getColor(mContext, R.color.blue2))
           }
           holder.message.setText(mMessages[position].message)
           holder.username.setText(mMessages[position].user?.username)

       }

       inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
           internal var message: TextView
           internal var username: TextView

           init {
               message = itemView.findViewById(R.id.chat_message_message)
               username = itemView.findViewById(R.id.chat_message_username)
           }
       }
   }


















