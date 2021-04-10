package com.sergeenko.lookapp.models

data class FakeLook(
        val id: Int,
        val bf: Int,
        val af: Int,
        val isPost: Boolean,
        val posts: List<Img>,
)
