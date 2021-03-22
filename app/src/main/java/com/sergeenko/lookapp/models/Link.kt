package com.sergeenko.lookapp.models

data class Link(
    var active: Boolean = false,
    var label: String? = null,
    var url: String? = null,
)