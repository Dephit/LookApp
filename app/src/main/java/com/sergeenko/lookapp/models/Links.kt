package com.sergeenko.lookapp.models

data class Links(
    var first: String = "",
    var last: String = "",
    var next: String? = null,
    var prev: String? = null
)