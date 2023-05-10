package com.example.todo

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ActivityHomeBinding
import com.example.todo.datamodel.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date
class HomeActivity : AppCompatActivity() {

    enum class Priorities {
        Low,
        Medium,
        High
    }

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityHomeBinding
    val currentDate = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val priorities = Priorities.values()
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item,
            priorities
        )

        with(binding.etPriority) {
            setAdapter(adapter)
        }
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        //user already logged in
        if (auth.currentUser == null) {
            goToLogin()
        }
        val currentUser = auth.currentUser

        loadAllData(currentUser!!.uid)

        binding.btnAdd.setOnClickListener {
            val task = binding.etTask.text.toString().trim()
            if (task.isEmpty()) {
                binding.etTask.setError("Task cannot be empty")
                return@setOnClickListener
            }

            val priority = binding.etPriority.text.toString()
            if (priority.isEmpty()) {
                binding.etPriority.setError("Priority cannot be empty")
                return@setOnClickListener
            }

            val document = db.collection("all_tasks").document()

            val taskData =
                TaskModel(task, false, currentUser!!.uid, Date = Date(), priority, document)
            db.collection("all_tasks")
                .add(taskData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this@HomeActivity, "Task saved", Toast.LENGTH_SHORT).show()
                    val taskList = ArrayList<TaskModel>()
                    taskList.add(taskData)
                    notifyItemInserted(taskList.size - 1)
                }
                .addOnFailureListener {
                    Toast.makeText(this@HomeActivity, "Task not saved", Toast.LENGTH_SHORT).show()
                    Log.e("HA", "Error : Err :" + it.message)
                }
        }


        //refresh
        binding.refresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            if (binding.refresh.isRefreshing) {
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

    fun goToLogin() {
        startActivity(Intent(this@HomeActivity, MainActivity::class.java))
        finish()
    }

    //Load all tasks
    fun loadAllData(userID: String) {

        val taskList = ArrayList<TaskModel>()
        val ref = db.collection("all_tasks")
            //.whereEqualTo("userID", userID).orderBy("date", Query.Direction.ASCENDING)
            //.whereEqualTo("userID", userID).whereLessThan("date", currentDate).orderBy("date", Query.Direction.ASCENDING)
            //.whereEqualTo("userID", userID).whereEqualTo("priority", "High").orderBy("taskName", Query.Direction.ASCENDING)
            .whereEqualTo("userID", userID).whereEqualTo("priority", "High").whereLessThanOrEqualTo("date", currentDate).orderBy("date", Query.Direction.DESCENDING)
            //.whereNotEqualTo("userID", userID).orderBy("userID", Query.Direction.DESCENDING).orderBy("priority").orderBy("taskName")
            .get()
            .addOnSuccessListener {

                if (it.isEmpty) {
                    Toast.makeText(this@HomeActivity, "No task found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (doc in it) {
                    val taskModel = doc.toObject(TaskModel::class.java)
                    taskList.add(taskModel)
                }

                val Query = db.collection("all_tasks")
                val countQuery = Query.whereEqualTo("userID", userID).count()
                countQuery.get(AggregateSource.SERVER).addOnCompleteListener { taskList ->
                    if (taskList.isSuccessful) {
                        val snapshot = taskList.result
                        Toast.makeText(
                            this@HomeActivity,
                            "Total tasks: ${snapshot.count}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(TAG, "Count: ${snapshot.count}")
                    } else {
                        Log.d(TAG, "Count Failed", taskList.exception)
                    }
                    val highPriorityQuery = db.collection("all_tasks")
                        .whereEqualTo("priority", "High").whereEqualTo("userID", userID)
                    highPriorityQuery.get()
                        .addOnSuccessListener { highPriorityDocs ->
                            val highPriorityCount = highPriorityDocs.size()
                            Toast.makeText(this@HomeActivity, "High priority tasks: $highPriorityCount", Toast.LENGTH_SHORT).show()
                        }

                    val mediumPriorityQuery = db.collection("all_tasks")
                        .whereEqualTo("priority", "Medium").whereEqualTo("userID", userID)
                    mediumPriorityQuery.get()
                        .addOnSuccessListener { mediumPriorityDocs ->
                            val mediumPriorityCount = mediumPriorityDocs.size()
                            Toast.makeText(this@HomeActivity, "Medium priority tasks: $mediumPriorityCount", Toast.LENGTH_SHORT).show()
                        }

                    val lowPriorityQuery = db.collection("all_tasks")
                        .whereEqualTo("priority", "Low").whereEqualTo("userID", userID)
                    lowPriorityQuery.get()
                        .addOnSuccessListener { lowPriorityDocs ->
                            val lowPriorityCount = lowPriorityDocs.size()
                            Toast.makeText(this@HomeActivity, "Low priority tasks: $lowPriorityCount", Toast.LENGTH_SHORT).show()
                        }
                }

                binding.rvToDoList.apply {
                    layoutManager =
                        LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false)
                    adapter = TaskAdapter(taskList, this@HomeActivity)
                }

            }
    }
}

private fun notifyItemInserted(i: Int) {


}




