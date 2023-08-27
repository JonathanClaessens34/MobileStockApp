package com.pxl.stockify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.pxl.stockify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //TODO: Clean whole program when done
    //TODO: Add usefull comments
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration



    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",0) //mode 0 = private

        if(sharedPreference.getBoolean("darkmode",false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)


        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        navController = findNavController(R.id.nav_host_fragment)
        drawerLayout = findViewById(R.id.drawer_Layout)
        binding.navigationView.setupWithNavController(navController)

        //menubar inplaats van backbutton heb je bij de topleberdestinations
        val topLevelDestinations = setOf(
            R.id.firstFragment,
            R.id.thirdFragment,
            R.id.fourthFragment
           //R.id.secondFragment
        )

        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setDrawerLayout(drawerLayout)
            .build()
        //appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)



        setupActionBarWithNavController(navController, appBarConfiguration)


        //val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",0) //mode 0 = private
//
        //if(sharedPreference.getBoolean("darkmode",false)){
        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//
//
        //}else{
        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //}


    }


    override fun onSupportNavigateUp(): Boolean {


        val navController = findNavController(R.id.nav_host_fragment)
        var detailFrame: View? = findViewById(R.id.details)

        if (detailFrame != null) {
            detailFrame.visibility = View.INVISIBLE
        }
        //val drawerLayout = findViewById(R.id.drawer_Layout)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }

}