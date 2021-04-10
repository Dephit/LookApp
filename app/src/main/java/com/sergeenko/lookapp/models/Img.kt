package com.sergeenko.lookapp.models

import java.io.Serializable

data class Img(
        val w: Int,
        val h: Int,
        val likes: Long,
        val dislikes: Long,
        val comments: Long,
        val dots: List<Pair<Int, Int>>,
        val author: String,
        val text: String,
        val isPost: Boolean,
        val postDate: String,
        val postText: String,
        var isFavorite: Boolean = false,
): Serializable