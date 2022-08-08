package com.assignment.usertodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment.usertodo.databinding.UserItemBinding
import com.assignment.usertodo.model.User

class UserAdapter(private val mList: List<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding : UserItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
       val user = mList[position]
        holder.binding.apply {
            txtUserName.text = user.name
            txtUserDob.text = user.dob
            txtUserEmail.text = user.email

            root.setOnClickListener {
                onItemClickListener?.let {
                    it(user)
                }
            }
        }
    }

    override fun getItemCount(): Int {
       return mList.size
    }

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit){
        onItemClickListener = listener
    }
}