package com.sergeenko.lookapp.models

data class User(
    var avatar: Any? = null,
    var email: String = "",
    var id: Int = 0,
    var new_user: Boolean = false,
    var phone: Any? = null,
    var profile: Any? = null,
    var username: String = ""
)