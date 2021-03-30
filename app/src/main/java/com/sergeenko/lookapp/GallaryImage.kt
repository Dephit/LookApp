package com.sergeenko.lookapp

import android.graphics.drawable.Drawable
import java.io.File

data class GallaryImage(
        val file: File,
        var isSelected: Boolean = false,
        var drawable: Drawable? = null
)
