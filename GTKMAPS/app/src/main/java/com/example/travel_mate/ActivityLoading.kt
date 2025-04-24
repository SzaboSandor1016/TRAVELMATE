/*
package com.example.travel_mate

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.travel_mate.databinding.ActivityLoadingBinding

class ActivityLoading : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding

    private val handler = Handler()
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO REMOVE THIS IF A
        // java.lang.NoSuchMethodError: No virtual method getInsetsController()Landroid/view/WindowInsetsController;
        // in class Landroid/view/Window; or its super classes (declaration of 'android.view.Window' appears in /system/framework/framework.jar!classes3.dex)
        // OCCURS
        // FROM HERE
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            //val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(
                0,0,0,0
            )
            WindowInsetsCompat.CONSUMED
        }
        //TODO TO HERE

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
}*/
