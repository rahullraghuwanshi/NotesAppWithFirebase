package com.assignment.usertodo.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.assignment.usertodo.databinding.FragmentAddToDoBinding
import com.assignment.usertodo.model.TodoTask
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class AddToDoFragment : Fragment() {

    private lateinit var binding : FragmentAddToDoBinding

    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddToDoBinding.inflate(inflater,container,false)

        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)


        binding.btnSave.setOnClickListener{
            save()
        }
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun save(){

        progressDialog.show()

        val shared: SharedPreferences = activity!!.getSharedPreferences("MySharedPreference", Context.MODE_PRIVATE)
        val number = shared.getString("number", "")!!

        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()

        val database = Firebase.database
        val myRef = database.getReference(number).child("Tasks")

        myRef.child(UUID.randomUUID().toString()).setValue(TodoTask(title,description))
            .addOnCompleteListener {
                progressDialog.dismiss()
                if (it.isSuccessful) {
                    showToast("Todo Task Added!!")
                    activity!!.onBackPressed()
                    activity!!.finish()
                } else {
                    showToast("Something is wrong")
                }
            }
    }

    private fun showToast(s: String) = Toast.makeText(activity, s, Toast.LENGTH_LONG).show()
}