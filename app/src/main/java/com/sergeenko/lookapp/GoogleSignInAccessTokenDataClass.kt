package com.sergeenko.lookapp

data class GoogleSignInAccessTokenDataClass(
        val access_token: String,
        val expires_in: Int,
        val id_token: String,
        val token_type: String
)
