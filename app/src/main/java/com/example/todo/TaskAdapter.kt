package com.example.todo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.TaskItemBinding
import com.example.todo.datamodel.TaskModel

class TaskAdapter(val taskList: ArrayList<TaskModel>, val context: Context) :
    RecyclerView.Adapter<TaskAdapter.MyHoler>() {


    class MyHoler(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHoler {

        val binding = TaskItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHoler(binding)
    }

    override fun onBindViewHolder(holder: MyHoler, position: Int) {

        try {
            val task = taskList[position]
            with(holder) {
                binding.tvTaskName.text = task.taskName
                binding.chBox.isChecked = task.isChecked

            }
        }catch (Exception e){
            Log.e("ERR", "Error")
        }

    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}

