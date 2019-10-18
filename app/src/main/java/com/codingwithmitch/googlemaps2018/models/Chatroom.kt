package com.codingwithmitch.googlemaps2018.models

import android.os.Parcel
import android.os.Parcelable

class Chatroom : Parcelable {

    var title: String? = null
    var chatroom_id: String? = null


    constructor(title: String, chatroom_id: String) {
        this.title = title
        this.chatroom_id = chatroom_id
    }

    constructor() {

    }

    protected constructor(`in`: Parcel) {
        title = `in`.readString()
        chatroom_id = `in`.readString()
    }

    override fun toString(): String {
        return "Chatroom{" +
                "title='" + title + '\''.toString() +
                ", chatroom_id='" + chatroom_id + '\''.toString() +
                '}'.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(chatroom_id)
    }

    companion object  CREATOR: Parcelable.Creator<Chatroom>
    {
            override fun createFromParcel(`in`: Parcel): Chatroom {
                return Chatroom(`in`)
            }

            override fun newArray(size: Int): Array<Chatroom?> {
                return arrayOfNulls(size)
            }
    }
}