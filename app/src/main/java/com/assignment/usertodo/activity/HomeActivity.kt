package com.assignment.usertodo.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.assignment.usertodo.R
import com.assignment.usertodo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
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
}