package com.iam_client.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.iam_client.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.toolbar

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.entry, R.id.customer_list, R.id.catalogFragment, R.id.settingsFragment),
            drawer_layout)
        nav_view.setupWithNavController(navController)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem)=
        item.onNavDestinationSelected(navController)
            || super.onOptionsItemSelected(item)


    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration)
}
