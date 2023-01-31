package com.supersonic.cigarettescounting

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.supersonic.cigarettescounting.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(){


    private lateinit var drawerLayout: DrawerLayout

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.appNavHostFragment)
        NavigationUI.setupActionBarWithNavController(
            this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navigationView, navController)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.appNavHostFragment)

        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}