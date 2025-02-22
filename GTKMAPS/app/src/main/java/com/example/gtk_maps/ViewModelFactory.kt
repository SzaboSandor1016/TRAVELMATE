package com.example.gtk_maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ViewModelFactory(private val repository: DataRepository, private val firebaseAuth: FirebaseAuth, private val database: FirebaseDatabase): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ViewModelFragmentPlaceDetails::class.java) -> {
                    ViewModelFragmentPlaceDetails() as T
            }
            modelClass.isAssignableFrom(ViewModelOverpass::class.java) -> {
                ViewModelOverpass(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelPhoton::class.java) -> {
                ViewModelPhoton(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelSave::class.java) -> {
                ViewModelSave(repository) as T
            }
            modelClass.isAssignableFrom(ViewModelMain::class.java) -> {
                ViewModelMain() as T
            }
            modelClass.isAssignableFrom(ViewModelTrip::class.java) -> {
                ViewModelTrip() as T
            }
            modelClass.isAssignableFrom(ViewModelFirebase::class.java) -> {
                ViewModelFirebase(firebaseAuth,database) as T
            }
            else -> throw IllegalArgumentException("Unknown fragment type")
        }
    }

}