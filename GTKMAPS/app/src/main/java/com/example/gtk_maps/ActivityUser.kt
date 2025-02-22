package com.example.gtk_maps

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.gtk_maps.databinding.ActivityMainBinding
import com.example.gtk_maps.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ActivityUser : AppCompatActivity(), FragmentUser.Selected {

    private lateinit var binding: ActivityUserBinding

    private lateinit var repository: DataRepository

    private lateinit var uiController: UiControllerUser

    private lateinit var viewModelFirebase: ViewModelFirebase
    private lateinit var viewModelSave: ViewModelSave

    private lateinit var firebaseAuth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val tag = "USER_FRAGMENT"

            val fragment = FragmentUser.newInstance()

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.user_frame_layout, fragment, tag)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        uiController = UiControllerUser(binding, supportFragmentManager)

        repository = DataRepository.getInstance()

        firebaseAuth = Firebase.auth

        val factory = MyApplication.factory

        viewModelFirebase = ViewModelProvider(this, factory)[ViewModelFirebase::class.java]


        viewModelFirebase.checkUser()

        viewModelFirebase.user.observe(this@ActivityUser) { user ->

        }

    }

     fun  initUserFragment() {

        val tag = "USER_FRAGMENT"

        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        if (existingFragment == null) {

            val fragment = FragmentUser.newInstance()

            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.user_frame_layout, fragment, tag)
                .commit()

        } else {
            (existingFragment as FragmentUser).updateUserFragment()
        }

    }

    fun  initSignInFragment() {

        val tag = "SIGN_IN_FRAGMENT"

        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        if (existingFragment == null) {

            val fragment = FragmentSignIn.newInstance()

            supportFragmentManager.beginTransaction()
                .replace(binding.userFrameLayout.id, fragment, tag)
                .commit()

        } else {
            (existingFragment as FragmentSignIn).updateSignInFragment()
        }

    }

    fun initSignUpFragment() {
        val tag = "SIGN_UP_FRAGMENT"

        val existingFragment = supportFragmentManager.findFragmentByTag(tag)

        if (existingFragment == null) {

            val fragment = FragmentSignUp.newInstance()

            supportFragmentManager.beginTransaction()
                .replace(binding.userFrameLayout.id,fragment,tag)
                .commit()

        } else {
            (existingFragment as FragmentSignUp).updateSignUpFragment()
        }
    }

    override fun onSelect() {
        finish()
    }


}