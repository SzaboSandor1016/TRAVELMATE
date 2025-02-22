package com.example.gtk_maps

import android.app.Application
import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyApplication: Application() {

    companion object {
        lateinit var appContext: Context
            private set
        lateinit var factory: ViewModelFactory
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        val repository = DataRepository.getInstance()
        val firebaseAuth = Firebase.auth
        val database = Firebase.database

        factory = ViewModelFactory(repository,firebaseAuth,database)
    }

}