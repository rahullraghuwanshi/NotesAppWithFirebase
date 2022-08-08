package com.assignment.usertodo.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.assignment.usertodo.R
import com.assignment.usertodo.databinding.ActivityBaseBinding
import com.assignment.usertodo.fragment.AddToDoFragment
import com.assignment.usertodo.fragment.AuthFragment
import com.assignment.usertodo.fragment.TodoDetailFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    private lateinit var auth: FirebaseAuth

    private var viewMode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        viewMode = if (intent.getStringExtra("mode") != null) {
            intent.getStringExtra("mode").toString()
        } else {
            "auth"
        }

        when (viewMode) {
            "todo" -> {
                addFragment(AddToDoFragment())
            }
            "todoDetail" -> {
                if (intent.getStringExtra("number") != null){
                   showTodoDetail(intent.getStringExtra("number").toString())
                }
            }
            "auth" -> {
                addFragment(AuthFragment())
            }
        }
    }

    private fun showTodoDetail(number: String) {
        val bundle = Bundle()
        bundle.putString("number", number)
        val fragment = TodoDetailFragment()
        fragment.arguments = bundle;
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.add(R.id.frame_layout, fragment)
        tr.commit()
    }

    private fun addFragment(fragment: Fragment?) {

        if (fragment == null) return
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.add(R.id.frame_layout, fragment)
        tr.commit()
    }
//    private fun addFragmentToFragment(fragment: Fragment){
//
//        val ft = childFragmentManager.beginTransaction()
//        ft.add(R.id.framlayout, fragment, fragment.javaClass.name)
//        ft.commitAllowingStateLoss()
//    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (viewMode.equals("auth")) {
            if (currentUser != null) {
                startActivity(Intent(this@BaseActivity, HomeActivity::class.java))
                finish()
            }
        }
    }
}