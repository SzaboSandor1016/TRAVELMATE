package com.example.gtk_maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class ViewModelFirebase (private val firebaseAuth: FirebaseAuth, private val database: FirebaseDatabase): ViewModel() {


    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var currentUser: FirebaseUser? = null

    fun getCurrentUser(): FirebaseUser?  = currentUser

    fun checkUser(){
        _user.value = firebaseAuth.currentUser
    }

    fun createUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->

            if (task.isSuccessful){

                Log.d("FirebaseAuth", "createUserWithEmail:success")
                currentUser = firebaseAuth.currentUser
                _user.postValue(firebaseAuth.currentUser)
            } else {

                Log.e("FirebaseAuth", "createUserWithEmail:error", task.exception)
                _error.postValue("Error creating user account")
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _user.value = null
        currentUser = null
    }

    fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->

            if (task.isSuccessful) {

                Log.d("FirebaseAuth", "signInUserWithEmail:success")
                currentUser = firebaseAuth.currentUser
                _user.postValue(firebaseAuth.currentUser)
            } else {
                Log.e("FirebaseAuth", "signInUserWithEmail:error", task.exception)
                _error.postValue("Error signing in user")
            }
        }
    }
}