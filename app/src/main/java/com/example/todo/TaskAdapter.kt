package com.example.todo

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.TaskItemBinding
import com.example.todo.datamodel.TaskModel
import com.google.firebase.firestore.FirebaseFirestore


class TaskAdapter(val taskList: ArrayList<TaskModel>, val context: Context) :
    RecyclerView.Adapter<TaskAdapter.MyHolder>() {

    lateinit var db: FirebaseFirestore
    lateinit var updatePopupWindow: PopupWindow
    var firstName: String = ""
    class MyHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        val inflater = LayoutInflater.from(context)
        val updatePopupView = inflater.inflate(R.layout.fragment_pop_up, null)

        // Set up the pop-up window view and listeners
        val etTaskName = updatePopupView.findViewById<EditText>(R.id.edit_task_name)
        val btnUpdate = updatePopupView.findViewById<TextView>(R.id.update_button)
        btnUpdate?.setOnClickListener {
            val newTaskName = etTaskName.text.toString().trim()
            updateTask(firstName, newTaskName)
            updatePopupWindow.dismiss()
        }

        updatePopupWindow = PopupWindow(
            updatePopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        db = FirebaseFirestore.getInstance()
        val task = taskList[position]
        with(holder) {
            binding.tvTaskName.text = task.taskName
            binding.btnDelete.setOnClickListener {
                deleteData(task.taskName)
            }
            binding.btnUpdate?.setOnClickListener {
                firstName = task.taskName
                showUpdatePopup(task.taskName, binding.btnUpdate)
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun showUpdatePopup(taskName: String, anchorView: View) {
        val etTaskName = updatePopupWindow.contentView.findViewById<EditText>(R.id.edit_task_name)
        etTaskName.setText(taskName)

        // Set the anchor view for the pop-up window
        updatePopupWindow.showAsDropDown(anchorView)
    }

    fun updateTask(FirstName: String?, newTaskName: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("all_tasks").whereEqualTo("taskName", FirstName).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null && task.result.documents.size > 0) {
                    val documentSnapshot = task.result.documents[0]
                    val documentID = documentSnapshot.id
                    db.collection("all_tasks").document(documentID)
                        .update("taskName", newTaskName)
                        .addOnSuccessListener {
                            Log.d(TAG, "Task updated")
                            Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating task", e)
                            Toast.makeText(context, "You don't have permissions to update this task", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }


    fun deleteData(FirstName: String?) {
        val db = FirebaseFirestore.getInstance()
        db.collection("all_tasks").whereEqualTo("taskName", FirstName).get()
            .addOnCompleteListener { task ->
                val documentSnapshot = task.result.documents[0]
                val documentID = documentSnapshot.id
                db.collection("all_tasks").document(documentID).delete().addOnSuccessListener { Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener {  Toast.makeText(context, "Error deleting task", Toast.LENGTH_SHORT).show() }
            }
    }

    fun updateData(newData: List<TaskModel>) {
        taskList.clear()
        taskList.addAll(newData)
        notifyDataSetChanged()
    }


}






