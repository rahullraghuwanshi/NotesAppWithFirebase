package com.assignment.usertodo.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.assignment.usertodo.R
import com.assignment.usertodo.databinding.ActivityHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private var darkmode_state : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        darkmode_state = getSharedPreferences(packageName, MODE_PRIVATE).getBoolean("darkmode", true);
        changeThemeOnStart();
        initView()
    }

    private fun changeThemeOnStart() {
        if (darkmode_state) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private fun initView() {
       setUpBottomNavigation()
    }

    private fun setUpBottomNavigation() {
        binding.apply {
            val navController = findNavController(R.id.nav_host_fragment)
            bottomNavigationView.apply {
            background = null
            menu.getItem(2).isEnabled = false
            setupWithNavController(navController)
            }

            fabAdd.setOnClickListener {
                val intent = Intent(this@HomeActivity,BaseActivity::class.java)
                intent.putExtra("mode","todo")
                startActivity(intent)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val settingsItem: MenuItem = menu.findItem(R.id.action_theme)
        // set your desired icon here based on a flag if you like
        if (darkmode_state) {
            settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_night))
        } else {
            settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day))
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_theme -> {
                if (darkmode_state) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    getSharedPreferences(packageName, MODE_PRIVATE)
                        .edit()
                        .putBoolean("darkmode", false)
                        .apply()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    getSharedPreferences(packageName, MODE_PRIVATE)
                        .edit()
                        .putBoolean("darkmode", true)
                        .apply()
                }
                invalidateOptionsMenu()
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}