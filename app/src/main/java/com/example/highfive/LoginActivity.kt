package com.example.highfive

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        buttonLogin.setOnClickListener { login() }
        buttonCreateAccount.setOnClickListener { createAccount() }

    }

    private fun login() {
        if (validEmail() && validPassword() && mAuth != null) {
            progressBar.visibility = View.VISIBLE
            mAuth!!.signInWithEmailAndPassword(
                editTextEmail.text.toString(),
                editTextPassword.text.toString()
            ).addOnCompleteListener(this@LoginActivity
            ) { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    onLoginSuccess()
                } else {
                    onLoginFailure()
                }
            }
        }
    }

    private fun createAccount() {
        //TODO add create account
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
        return if(editTextPassword.text.isNullOrEmpty()){
            editTextPassword.error = "Please enter a valid password"
            false
        } else {
            editTextPassword.error = null
            true
        }
    }

    private fun onLoginSuccess() {
        Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show()
    }

    private fun onLoginFailure() {
        Toast.makeText(this, "Login Failure", Toast.LENGTH_LONG).show()
    }
}