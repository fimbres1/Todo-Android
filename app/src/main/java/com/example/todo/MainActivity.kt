package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.ui.theme.TodoTheme
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import java.security.AuthProvider

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        //user already logged in
        if (auth.currentUser != null){
            Toast.makeText(this@MainActivity, "User already logged in", Toast.LENGTH_SHORT).show()
            goToHome()
        }

        findViewById<MaterialButton>(R.id.btLogin).setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        goToHome()
                    }else{
                        Toast.makeText(this@MainActivity, "User Auth Failed", Toast.LENGTH_SHORT).show()
                        Log.e("MA", "auth failed:" + (it.exception!!.message))
                    }

                }

        }

        //click to register
        findViewById<MaterialButton>(R.id.btRegister).setOnClickListener {

            var email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            var password = findViewById<EditText>(R.id.etPassword).text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this@MainActivity, "User Created", Toast.LENGTH_SHORT).show()
                        goToHome()
                    }else{
                        Toast.makeText(this@MainActivity, "User Already Exists", Toast.LENGTH_SHORT).show()
                        Log.e("MA", "auth failed:" + (it.exception!!.message))
                    }
                }

        }

    }
    fun goToHome(){
        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        finish()
    }
}