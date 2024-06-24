package com.example.journalapp.database

import com.google.firebase.Timestamp



class Journal {

    var title: String? = null
    var thoughts: String? = null
    var imageUrl: String? = null
    var userId: String? = null
    var timeAdded: Timestamp? = null
    var userName: String? = null

    // When using firebase, always create empty constructor
    constructor()
    constructor(
        title: String?,
        thoughts: String?,
        imageUrl: String?,
        userId: String?,
        timeAdded: Timestamp?,
        userName: String?
    ) {
        this.title = title
        this.thoughts = thoughts
        this.imageUrl = imageUrl
        this.userId = userId
        this.timeAdded = timeAdded
        this.userName = userName
    }
}
