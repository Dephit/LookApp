package com.sergeenko.lookapp.models

data class CommentResponse(
    var data: List<Comment> = listOf()
)

data class CommentResponse2(
    var data: Comment = Comment()
)