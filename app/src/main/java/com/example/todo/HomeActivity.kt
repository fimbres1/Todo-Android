package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ActivityHomeBinding
import com.example.todo.datamodel.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

class HomeActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore

    lateinit var binding : ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        //user already logged in
        if (auth.currentUser == null){
            goToLogin()
        }
        val currentUser = auth.currentUser

        loadAllData(currentUser!!.uid)

        binding.btnAdd.setOnClickListener {
            val task = binding.etTask.text.toString().trim()
            if (task.isEmpty()){
                binding.etTask.setError("Task cannot be empty")
                return@setOnClickListener
            }

            val priority =binding.etPriority.text.toString()
            if (priority.isEmpty()){
                binding.etPriority.setError("Priority cannot be empty")
            }

            val taskData = TaskModel(task, false, currentUser!!.uid, Date = Date(), taskPriority = String())
            db.collection("all_tasks")
                .add(taskData)
                .addOnSuccessListener {
                    Toast.makeText(this@HomeActivity, "Task saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this@HomeActivity, "Task not saved", Toast.LENGTH_SHORT).show()
                    Log.e("HA", "Error : Err :" + it.message)
                }
        }

        //refresh
        binding.refresh.setOnRefreshListener {
            if(binding.refresh.isRefreshing){
                binding.refresh.isRefreshing = false
            }
            loadAllData(currentUser!!.uid)
        }

        binding.btnLogout.setOnClickListener {
            //logout
            auth.signOut()
            goToLogin()
        }
    }
    
    fun goToLogin(){
        startActivity(Intent(this@HomeActivity, MainActivity::class.java))
        finish()
    }

    //Load all tasks
    fun loadAllData(userID : String){

        val taskList = ArrayList<TaskModel>()

        val ref = db.collection("all_tasks")
        ref.whereEqualTo("userID",userID)
            .get()
            .addOnSuccessListener {

                if (it.isEmpty){
                    Toast.makeText(this@HomeActivity, "No task found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for ( doc in it){
                    val taskModel = doc.toObject(TaskModel::class.java)
                    taskList.add(taskModel)
                }

                binding.rvToDoList.apply {
                    layoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false)
                    adapter = TaskAdapter(taskList, this@HomeActivity)
                }

            }
    }
}