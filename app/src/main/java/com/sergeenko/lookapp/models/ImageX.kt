package com.sergeenko.lookapp.models

data class ImageX(
    var id: Int = 0,
    var marks: List<Any> = listOf(),
    var total: Int = 0,
    var url: String = ""
)