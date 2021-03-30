package com.sergeenko.lookapp

import android.app.Application
import android.widget.Toast
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application(){

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            Toast.makeText(
                applicationContext,
                "VK com.sergeenko.lookapp.models.Token is expired",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate() {
        super.onCreate()

        initFB()
        initVK()
        EmojiManager.install(GoogleEmojiProvider())
    }

    private fun initVK() {
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private fun initFB() {
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}