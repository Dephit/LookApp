package com.sergeenko.lookapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainNavigation : BaseActivity() {

    lateinit var vkCallback: VKAuthCallback

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_navigation)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun vkLogin(manageVkResponse: VKAuthCallback) {
        vkCallback = manageVkResponse
        VK.login(this, arrayListOf(VKScope.EMAIL, VKScope.PHOTOS, VKScope.OFFLINE))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(this::vkCallback.isInitialized) {
            if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback = vkCallback)) {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}