package com.assignment.usertodo.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.usertodo.activity.BaseActivity
import com.assignment.usertodo.adapter.TodoAdapter
import com.assignment.usertodo.adapter.UserAdapter
import com.assignment.usertodo.databinding.FragmentTodoDetailBinding
import com.assignment.usertodo.model.TodoTask
import com.assignment.usertodo.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TodoDetailFragment : Fragment() {

    private lateinit var binding : FragmentTodoDetailBinding

    private lateinit var adapter : TodoAdapter

            @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoDetailBinding.inflate(inflater,container,false)

        val number = arguments!!.getString("number")
        getTodo(number)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun getTodo(number: String?) {
        var todoList = ArrayList<TodoTask>()
        val database = Firebase.database
        val myRef = database.reference.child(number!!).child("Tasks")

        myRef.get().addOnCompleteListener{ it ->
            if (it.isSuccessful) {
                it.result.children.forEach{
                    val todo: TodoTask? = it.getValue(TodoTask::class.java)
                    todoList.add(todo!!)
                }

                setRecyclerView(todoList)
            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun setRecyclerView(todoList: java.util.ArrayList<TodoTask>) {
        adapter = TodoAdapter(todoList)
        adapter.setOnItemClickListener {
           Toast.makeText(activity!!,"${it.title}",Toast.LENGTH_LONG).show()
        }
        binding.rvTodoDetail.layoutManager = LinearLayoutManager(context)
        binding.rvTodoDetail.adapter = adapter
    }
}