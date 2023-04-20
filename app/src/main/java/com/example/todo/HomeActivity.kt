package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.databinding.ActivityHomeBinding
import com.example.todo.datamodel.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        Log.e("USER", "Error : Err :" + currentUser!!.uid)

        binding.btnAdd.setOnClickListener {
            val task = binding.etTask.text.toString().trim()
            if (task.isEmpty()){
                binding.etTask.setError("Task cannot be empty")
                return@setOnClickListener
            }

            val taskData = TaskModel(task, false, currentUser!!.uid)
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
}