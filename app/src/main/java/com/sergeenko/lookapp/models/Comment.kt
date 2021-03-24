package com.sergeenko.lookapp.models

import java.io.Serializable

data class Comment(
    var comments: List<Comment> = listOf(),
    var created: String = "",
    var parent_id: Int? = null,
    var id: Int = 0,
    var text: String = "",
    var has_comments: Boolean =  true,
    var count_comments: Int = 1,
    var isPost: Boolean = false,
    var isSelected: Int = 0,
    var user: Data = Data()
): Serializable