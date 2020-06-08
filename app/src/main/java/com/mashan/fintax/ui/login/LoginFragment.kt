package com.mashan.fintax.ui.login

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.mashan.fintax.R
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var provider: List<AuthUI.IdpConfig>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        provider = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//            AuthUI.IdpConfig.PhoneBuilder().build()
        )

        showSignInOptions()

        sign_out.setOnClickListener {
            context?.let { view ->
                AuthUI.getInstance().signOut(view)
                    .addOnCompleteListener {
                        sign_out.isEnabled = false
                        showSignInOptions()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()

                    }
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {

            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(context, "" + user!!.email, Toast.LENGTH_SHORT).show()
                sign_out.isEnabled = true
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNavHostFragment())
            } else {

                Toast.makeText(context, "" + response!!.error!!.message, Toast.LENGTH_SHORT).show()

            }

        }
    }

    private fun showSignInOptions() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTheme(R.style.MyTheme)
                .build(), MY_REQUEST_CODE

        )
    }

    companion object {
        const val MY_REQUEST_CODE = 7917
    }
}
