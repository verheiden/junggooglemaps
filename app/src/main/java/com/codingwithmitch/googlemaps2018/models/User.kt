package com.codingwithmitch.googlemaps2018.models

import android.os.Parcel
import android.os.Parcelable

class User : Parcelable {

    var email: String? = null
    var user_id: String? = null
    var username: String? = null
    var avatar: String? = null
        set(avatar: String?) {this.avatar = avatar}

    constructor(email: String, user_id: String, username: String, avatar: String) {
        this.email = email
        this.user_id = user_id
        this.username = username
        this.avatar = avatar
    }

    constructor() {}

    protected constructor(input: Parcel) {
        email = input.readString()
        user_id = input.readString()
        username = input.readString()
        avatar = input.readString()
    }
    companion object CREATOR : Parcelable.Creator<User>
    {
        override fun createFromParcel(parcel : Parcel): User {
            return User(parcel)
        }

        override fun newArray(size : Int ) : Array<User?>{
            return arrayOfNulls(size)
        }
    }


    override fun toString(): String {
        return "User{" +
                "email='" + email + '\''.toString() +
                ", user_id='" + user_id + '\''.toString() +
                ", username='" + username + '\''.toString() +
                ", avatar='" + avatar + '\''.toString() +
                '}'.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(email)
        dest.writeString(user_id)
        dest.writeString(username)
        dest.writeString(avatar)
    }

}
