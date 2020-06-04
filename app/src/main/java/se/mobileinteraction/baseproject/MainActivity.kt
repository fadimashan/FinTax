package se.mobileinteraction.baseproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import se.mobileinteraction.baseproject.utils.NavExtensions.setUpBottomNavigation

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.fragmentHost)

        setUpBottomNavigation(bottomNavigationView, navController)

        val appBarConfig =
            AppBarConfiguration(setOf(R.id.aboutFragment, R.id.favouriteCatsFragment, R.id.catListFragment))

        setupActionBarWithNavController(navController, appBarConfig)
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.fragmentHost).navigateUp()
}
