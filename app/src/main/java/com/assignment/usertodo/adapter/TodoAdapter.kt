package com.assignment.usertodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment.usertodo.databinding.TodoDetailItemBinding
import com.assignment.usertodo.databinding.UserItemBinding
import com.assignment.usertodo.model.TodoTask
import com.assignment.usertodo.model.User

class TodoAdapter (private val mList: List<TodoTask>): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(val binding : TodoDetailItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = TodoDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = mList[position]
        holder.binding.apply {
            txtTitle.text = todo.title
            txtDescription.text = todo.description

            root.setOnClickListener {
                onItemClickListener?.let {
                    it(todo)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private var onItemClickListener: ((TodoTask) -> Unit)? = null

    fun setOnItemClickListener(listener: (TodoTask) -> Unit){
        onItemClickListener = listener
    }
}