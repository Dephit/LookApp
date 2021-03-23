package com.sergeenko.lookapp.models

data class CommentResponse(
    var data: List<Comment> = listOf(),
    val meta: Meta = Meta(),
    val links: Links = Links()
)

data class CommentResponse2(
    var data: Comment = Comment()
)