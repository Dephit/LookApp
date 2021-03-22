package com.sergeenko.lookapp.models

data class Meta(
    var current_page: Int = 0,
    var from: Int = 0,
    var last_page: Int = 0,
    var links: List<Link> = listOf(),
    var path: String = "",
    var per_page: Int = 0,
    var to: Int = 0,
    var total: Int = 0
)