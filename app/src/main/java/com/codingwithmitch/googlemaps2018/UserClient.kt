package com.codingwithmitch.googlemaps2018

import android.app.Application
import apps.com.codingwithmitch.googlemaps2018.models.User

class UserClient : Application() {

    var  user: User?
      get() = user
      set(other : User?) { this.user = other }
    init { user = null}

}