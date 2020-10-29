package com.mashan.fintax.ui.login


import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mashan.fintax.R
import com.mashan.fintax.utils.ScreenUtils
import kotlinx.android.synthetic.main.splash_fragment.*


class SplashFragment : Fragment(R.layout.splash_fragment) {


    override fun onResume() {
        super.onResume()

        img_logo.apply {
            translationY = ScreenUtils.dipsToPixel(120f)
            animate().alpha(1f).setStartDelay(500).setDuration(300).start()
            animate()
                .setInterpolator(DecelerateInterpolator())
                .translationY(1f)
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setStartDelay(1000)
                .setDuration(1800)

                .withEndAction {
                    navigateToDetails()
                }.start()
        }
    }

    private fun navigateToDetails() {
        val navTo = if (FirebaseAuth.getInstance().currentUser != null)
            SplashFragmentDirections.actionSplashFragmentToNavHostFragment().actionId
        else SplashFragmentDirections.actionSplashFragmentToLoginFragment().actionId
        //to not backPressed to startup fragment (Splash)
        findNavController().navigate(
            navTo,
            null,
            NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
        )
    }
}
