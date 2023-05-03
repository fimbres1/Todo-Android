package com.example.todo.datamodel

import com.google.firebase.firestore.DocumentReference
import java.util.Date

class TaskModel(
    val taskName: String = "",
    val isChecked: Boolean = false,
    val userID: String = "",
    val Date: Date? = null,
    val Priority: String = "",
    val id: DocumentReference? = null,
)