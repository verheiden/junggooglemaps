package com.codingwithmitch.googlemaps2018.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.codingwithmitch.googlemaps2018.R
import java.util.ArrayList

class ChatroomRecyclerAdapter(
    chatrooms: ArrayList<Chatroom>,
    private val mChatroomRecyclerClickListener: ChatroomRecyclerClickListener
) : RecyclerView.Adapter<ChatroomRecyclerAdapter.ViewHolder>() {

    private val mChatrooms = ArrayList<Chatroom>()

    val itemCount: Int
        get() = mChatrooms.size

    init {
        this.mChatrooms = chatrooms
    }

    @NonNull
    fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_chatroom_list_item, parent, false)


        return ViewHolder(view, mChatroomRecyclerClickListener)
    }

    fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.chatroomTitle.setText(mChatrooms.get(position).getTitle())
    }

    inner class ViewHolder(
        itemView: View,
        internal var clickListener: ChatroomRecyclerClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var chatroomTitle: TextView

        init {
            chatroomTitle = itemView.findViewById(R.id.chatroom_title)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener.onChatroomSelected(getAdapterPosition())
        }
    }

    interface ChatroomRecyclerClickListener {
        fun onChatroomSelected(position: Int)
    }
}











