package com.sergeenko.lookapp.models

import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.File

data class GallaryImage(
        val file: Uri,
        var isSelected: Boolean = false,
        var drawable: Drawable? = null
)
