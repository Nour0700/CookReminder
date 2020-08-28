package com.example.android.reminder.loginActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.android.reminder.R
import com.example.android.reminder.databinding.ActivityLoginBinding
import com.example.android.reminder.mainActivity.MainActivity
import com.example.android.reminder.mainActivity.network.FirebaseUserLiveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val SIGN_IN_REQUEST_CODE = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this as AppCompatActivity?)!!.supportActionBar?.hide()

        val binding  = DataBindingUtil
            .setContentView<ActivityLoginBinding>(this, R.layout.activity_login)


        FirebaseUserLiveData().observe(this, Observer {
            if(it == null){

                register_button.setOnClickListener {
                    launchSignInFlow()
                }
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

    }




    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_REQUEST_CODE
        )
    }
}