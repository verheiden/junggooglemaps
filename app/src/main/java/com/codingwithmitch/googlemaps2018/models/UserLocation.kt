package com.codingwithmitch.googlemaps2018.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class UserLocation : Parcelable {
    var geo_point : GeoPoint ? = null
    @ServerTimestamp
    var timestamp: Date? = null
    var user : User? = null

    constructor(user : User, geo_point: GeoPoint, timestamp : Date){
        this.user = user
        this.geo_point = geo_point
        this.timestamp = timestamp
    }
    constructor() {}
    constructor(`in`: Parcel)
    {
        user = `in`.readParcelable(User::class.java.classLoader)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        writeToParcel(dest, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserLocation> {
        override fun newArray(size: Int): Array<UserLocation?> {
            return arrayOfNulls(size)
        }

        override fun createFromParcel(source: Parcel): UserLocation {
            return UserLocation(source)
        }
    }
}