package com.sergeenko.lookapp.models

import com.google.gson.Gson

class PostBodyItemConverter {
    fun readContent(readString: String?): Any? {
        return try {
            Gson().fromJson(readString, Look::class.java)
        }catch (e: Exception){
            Gson().fromJson(readString, String::class.java)
        }
    }

}
