package com.sergeenko.lookapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sergeenko.lookapp.AppDatabase
import com.sergeenko.lookapp.MainNavigation
import com.sergeenko.lookapp.interfaces.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var repository: Repository
    @Inject lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchCodes()
    }

    private fun fetchCodes() {
        lifecycleScope.launch(Dispatchers.IO) {
            repository.getCountryCodes()
            val sr = db.socialResponseDao().get()
            if (sr != null && !sr.data.new_user) {
                repository.getBrands()
                startActivity(Intent(applicationContext, MenuActivity::class.java))
                finish()
            }else{
                withContext(Main){
                    startActivity(Intent(applicationContext, MainNavigation::class.java))
                    finish()
                }
            }
        }
    }
}