package com.assignment.usertodo.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.service.autofill.UserData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.usertodo.activity.BaseActivity
import com.assignment.usertodo.adapter.UserAdapter
import com.assignment.usertodo.databinding.FragmentHomeBinding
import com.assignment.usertodo.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    private lateinit var progressDialog: ProgressDialog

    private lateinit var currentUserId : String

    private lateinit var adapter : UserAdapter

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val shared: SharedPreferences = activity!!.getSharedPreferences("MySharedPreference", MODE_PRIVATE)
        currentUserId = shared.getString("number", "")!!


        initView()

        getUsers()
        return binding.root
    }

    private fun initView() {
        binding.rlUserDetail.setOnClickListener {
            val intent = Intent(requireActivity(), BaseActivity::class.java)
            intent.putExtra("mode","todoDetail")
            intent.putExtra("number","+91${binding.txtUserNumber.text.toString()}")
            startActivity(intent)
        }
    }

    private fun getUsers(){
        var userList = ArrayList<User>()
        val database = Firebase.database
        val myRef = database.reference
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                progressDialog.dismiss()
                for (child in dataSnapshot.children) {
                    myRef.child(child.key.toString()).child("MyData").get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                var user: User? = it.result.getValue(User::class.java)
                                if (currentUserId == child.key){
                                    setCurrentUser(user)
                                }else{
                                    userList.add(user!!)
                                    setRecyclerView(userList)
                                }
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
               showToast("Unable to get Data")
            }
        })
    }

    private fun setRecyclerView(userList: ArrayList<User>) {
        adapter = UserAdapter(userList)
        adapter.setOnItemClickListener {
            val intent = Intent(requireActivity(), BaseActivity::class.java)
            intent.putExtra("mode","todoDetail")
            intent.putExtra("number","+91${it.number}")
            startActivity(intent)
        }
        binding.rvUser.layoutManager = LinearLayoutManager(context)
        binding.rvUser.adapter = adapter

    }

    private fun setCurrentUser(user: User?) {
        binding.apply {
            txtUserName.text = user?.name
            txtUserDob.text = user?.dob
            txtUserEmail.text = user?.email
            txtUserNumber.text = user?.number
        }
    }

    private fun showToast(s: String) = Toast.makeText(activity, s, Toast.LENGTH_LONG).show()

}