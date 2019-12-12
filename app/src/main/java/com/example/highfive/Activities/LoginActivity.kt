package com.example.highfive.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.highfive.Helper.CreateUser
import com.example.highfive.Models.Connection
import com.example.highfive.Models.CurrentUser
import com.example.highfive.Models.User
import com.example.highfive.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        buttonLogin.setOnClickListener { login() }
        buttonCreateAccount.setOnClickListener { createAccount() }
        CurrentUser.init(this)

    }

    private fun login() {
        if (validEmail() && validPassword()) {
            progressBar.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            ).addOnCompleteListener(this@LoginActivity
            ) { task ->
                if (task.isSuccessful) {
                    onLoginSuccess()
                } else {
                    onLoginFailure()
                }
            }
        }
    }

    private fun createAccount() {
        val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validEmail(): Boolean {
        return if(editTextEmail.text.isNullOrEmpty()){
            editTextEmail.error = "Please enter a valid email address"
            false
        } else {
            editTextEmail.error = null
            true
        }
    }

    private fun validPassword(): Boolean {
        return if(editTextPassword.text.isNullOrEmpty() || editTextPassword.text.toString().length < 6){
            editTextPassword.error = "Please enter a valid password"
            false
        } else {
            editTextPassword.error = null
            true
        }
    }

    private fun onLoginSuccess() {
        Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show()
        val userId = mAuth.currentUser?.uid ?: ""
        val databaseReference = mDatabase.getReference("Users").child(userId)
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                progressBar.visibility = View.GONE
                onLoginFailure()
            }

            override fun onDataChange(p0: DataSnapshot) {
                progressBar.visibility = View.GONE

                val user = CreateUser.createUser(p0, userId)
                CurrentUser.setUser(user)
                launchMainActivity()

            }

        })

    }

    private fun onLoginFailure() {
        Toast.makeText(this, "Login Failure", Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE
    }

    private fun launchMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}