package com.example.todo.datamodel

import java.text.SimpleDateFormat
import java.util.Date

class TaskModel(
    val taskName: String = "",
    val isChecked: Boolean = false,
    val userID: String = "",
    val Date: Date? = null,
)