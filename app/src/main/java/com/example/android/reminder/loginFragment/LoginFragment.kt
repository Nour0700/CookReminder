package com.example.android.reminder.loginFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.reminder.R
import com.example.android.reminder.addFragment.TAG
import com.example.android.reminder.mainFragment.AuthenticationState
import com.example.android.reminder.mainFragment.MainViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

private val SIGN_IN_REQUEST_CODE = 1

class LoginFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()

    //=========================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity?)!!.supportActionBar?.hide()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        register_button.setOnClickListener {
            launchSignInFlow()
        }

        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    (activity as AppCompatActivity?)!!.supportActionBar?.show()
                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                }
                AuthenticationState.UNAUTHENTICATED -> Log.i(TAG, "UNAUTHENTICATED")
                else -> Log.e(TAG, "New $authenticationState state that doesn't require any UI change")
            }
        })
    }

    //=========================================

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