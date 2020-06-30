package com.naina.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.naina.foodrunner.R
import com.naina.foodrunner.fragment.*

class DashboardActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtUserName: TextView
    lateinit var txtMobileNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        init()

        setUpHeader()

        setUpToolbar()

        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, HomeFragment()).commit()

        supportActionBar?.title = "Home"

        navigationView.setCheckedItem(R.id.home)

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@DashboardActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isChecked = true
            it.isCheckable = true

            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, HomeFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Home"

                }

                R.id.profile -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ProfileFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Profile"

                }

                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavouritesFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourites"
                }

                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrderHistoryFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "My Order History"
                }

                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FaqFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Faq"
                }

                R.id.logout -> {
                    logout()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when (frag) {
            !is HomeFragment -> {

                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, HomeFragment())
                    .commit()

                supportActionBar?.title = "Home"

                navigationView.setCheckedItem(R.id.home)
            }

            else -> {
                ActivityCompat.finishAffinity(this@DashboardActivity)
                super.onBackPressed()
            }

        }
    }

    fun init() {
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
    }

    fun setUpHeader() {
        val convertView =
            LayoutInflater.from(this@DashboardActivity).inflate(R.layout.drawer_header, null)

        txtUserName = convertView.findViewById(R.id.txtUserName)

        txtMobileNumber = convertView.findViewById(R.id.txtMobileNumber)

        txtUserName.text = sharedPreferences.getString("name", null)

        txtMobileNumber.text = "+91" + sharedPreferences.getString("mobile_number", null)

        navigationView.addHeaderView(convertView)
    }

    fun logout() {
        val alertDialog = AlertDialog.Builder(this@DashboardActivity)
        alertDialog.setTitle("Confirmation")
        alertDialog.setMessage("Are you sure you want to logout?")
        alertDialog.setPositiveButton("Yes") { text, listner ->
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            sharedPreferences.edit().clear().apply()
            startActivity(intent)
            finish()
        }
        alertDialog.setNegativeButton("No")
        { text, listner ->
            text.dismiss()
        }
        alertDialog.create()
        alertDialog.show()
    }


}
