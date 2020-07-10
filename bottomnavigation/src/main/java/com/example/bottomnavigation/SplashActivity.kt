package com.example.bottomnavigation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        GlobalScope.launch {
            delay(3000)
            withContext(Dispatchers.Main){
                startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                finish()
            }
        }
    }
}