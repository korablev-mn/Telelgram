package com.example.telelgram.models

data class User(
    val id: String = "",   // var неизменяемое
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty"
)