package com.codingwithmitch.googlemaps2018.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class ChatMessage {

    var user: User? = null
    var message: String? = null
    var message_id: String? = null
    @ServerTimestamp
    var timestamp: Date? = null

    constructor(user: User, message: String, message_id: String, timestamp: Date) {
        this.user = user
        this.message = message
        this.message_id = message_id
        this.timestamp = timestamp
    }

    constructor() {

    }

    override fun toString(): String {
        return "ChatMessage{" +
                "user=" + user +
                ", message='" + message + '\''.toString() +
                ", message_id='" + message_id + '\''.toString() +
                ", timestamp=" + timestamp +
                '}'.toString()
    }
}
