package com.sergeenko.lookapp.models

data class PostResponse(
    var data: List<Look> = listOf(),
    var meta: Meta,
    var links: Links,
)