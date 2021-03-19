package com.sergeenko.lookapp.models

data class AuthMessage(
    var message: String = "",
    var ok: Boolean = false,
    var status: Int = 0,
    val data: List<String>
)