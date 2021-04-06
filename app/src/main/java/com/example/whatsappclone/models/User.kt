package com.example.whatsappclone.models

data class User(
    val name: String,
    val imageUrl: String,
    val thumbImageUrl: String,      //TODO to implement the thumb image url
    val uid: String,
    val deviceToken: String,    // for sending notifications to specific devices via firebase later
    val onlineStatus: String,
    val status: String
) {
    /**  Empty [Constructor] is needed for Firebase models     */
    constructor() : this("","","","","","", "")

    constructor(name: String, imageUrl: String,thumbImageUrl: String,uid: String): this(
        name,
        imageUrl,
        thumbImageUrl,
        uid,
        "",
        "",
        "Hey there, I'm using Whatsapp"

    )


}