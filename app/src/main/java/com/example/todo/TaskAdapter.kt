package com.example.todo

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.TaskItemBinding
import com.example.todo.datamodel.TaskModel
import com.google.firebase.firestore.FirebaseFirestore


class TaskAdapter(val taskList: ArrayList<TaskModel>, val context: Context) :
    RecyclerView.Adapter<TaskAdapter.MyHolder>() {
    class MyHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root)

    lateinit var db : FirebaseFirestore

    fun checkLateInit(){
        if (this::db.isInitialized){
            Log.e("HX","No ha iniciado")
    }else{
        Log.e("HZ","Se inicio")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        val binding = TaskItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        try {
            val task = taskList[position]
            with(holder) {
                binding.tvTaskName.text = task.taskName
                binding.chBox.isChecked = task.isChecked
                binding.tvPriority.text = task.Priority
                binding.btnDelete.setOnClickListener(View.OnClickListener {
                    db.collection("all_tasks").document("document=**").delete()
                        .addOnSuccessListener { Log.d(TAG, "Task deleted") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting", e) }
                })
            }
        }catch (w:Exception){
            Log.w(TAG, "Error", w)
        }
        }



    override fun getItemCount(): Int {
        return taskList.size
    }

}

