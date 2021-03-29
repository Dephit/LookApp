package com.sergeenko.lookapp

import java.io.File

data class GallaryImage(
        val file: File,
        var isSelected: Boolean = false
)
