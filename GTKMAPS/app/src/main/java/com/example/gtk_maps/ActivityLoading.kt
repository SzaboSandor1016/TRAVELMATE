package com.example.gtk_maps

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class ActivityLoading : AppCompatActivity() {
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        handler.postDelayed({
            startActivity(Intent(this@ActivityLoading, ActivityMain::class.java))
            finish()
        }, 1800)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending delayed actions to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }
}