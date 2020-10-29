package com.mashan.fintax

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.mashan.fintax.ui.mainPage.MainPageFragmentDirections
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.home_page_fragment.*

class MainActivity : AppCompatActivity(R.layout.activity_main),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.carFragment, R.id.houseFragment, R.id.salaryFragment, R.id.percentageFragment,
                R.id.summaryFragment, R.id.infoFragment
            ), drawerLayout
        )
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)
        drawerLayout.nav_view.setNavigationItemSelectedListener(this)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    // Sign out drawer item click listener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sign_out_nav) {
            this.let { view ->
                AuthUI.getInstance().signOut(view)
                    .addOnCompleteListener {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
