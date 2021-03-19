package com.sergeenko.lookapp.models

import java.io.Serializable

data class Comment(
    var comments: List<Comment> = listOf(),
    var created: String = "",
    var id: Int = 0,
    var text: String = "",
    var isPost: Boolean = false,
    var user: Data = Data()
): Serializable