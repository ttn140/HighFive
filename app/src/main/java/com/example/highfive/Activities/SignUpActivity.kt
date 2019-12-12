package com.example.highfive.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.highfive.Models.User
import com.example.highfive.Models.CurrentUser
import com.example.highfive.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private var isEdit: Boolean = false
    private var imageURI: Uri? = null
    private var userImageString = ""
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        firebaseStorage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()
        if (intent.hasExtra(EDIT_FLOW)) {
            if (intent.getBooleanExtra(EDIT_FLOW, false)) edit() else signUp()
        } else {
            signUp()
        }

        imageViewUserProfileImage.setOnClickListener { updateProfilePicture() }
        buttonSubmit.setOnClickListener {
            if (validFields()) {
                submit()
                loading(true)
            }
        }
    }

    fun signUp() {

    }

    fun edit() {
        isEdit = true
        val user = CurrentUser.user

        //Mandatory fields
        editTextFirstName.setText(user.firstName)
        editTextLastName.setText(user.lastName)
        editTextEmail.setText(user.email)
        editTextPhone.setText(user.phone)

        //Non-mandatory fields
        if (!user.website.isNullOrBlank()) {
            editTextWebsite.setText(user.website)
        }
        if (!user.company.isNullOrBlank()) {
            editTextCompany.setText(user.company)
        }
        if (!user.jobTitle.isNullOrBlank()) {
            editTextJobTitle.setText(user.jobTitle)
        }

    }

    fun updateProfilePicture() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(intent, "Select File"),
            SELECT_FILE
        )
    }

    fun submit() {
        if (isEdit) {
            submitEdit()
        } else {
            createUser()

        }
    }

    fun submitEdit() {
        CurrentUser.write(CurrentUser.FIRST_NAME, editTextFirstName.text.toString())
        CurrentUser.write(CurrentUser.LAST_NAME, editTextLastName.text.toString())
        CurrentUser.write(CurrentUser.EMAIL, editTextEmail.text.toString())
        CurrentUser.write(CurrentUser.PHONE, editTextPhone.text.toString())
        CurrentUser.write(CurrentUser.COMPANY, if (editTextCompany.text == null) "" else editTextCompany.text.toString())
        CurrentUser.write(CurrentUser.JOB_TITLE, if (editTextJobTitle.text == null) "" else editTextJobTitle.text.toString())
        CurrentUser.write(CurrentUser.WEBSITE, if (editTextWebsite.text == null) "" else editTextWebsite.text.toString())

        var intent = Intent(this@SignUpActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun validFields(): Boolean {
        return validateFirstName() && validateLastName() && validateEmail() && validatePassword() && validatePhone()
    }

    fun validateFirstName(): Boolean {
        return !editTextFirstName.text.isNullOrBlank()
    }

    fun validateLastName(): Boolean {
        return !editTextLastName.text.isNullOrBlank()
    }

    fun validateEmail(): Boolean {
        return !editTextEmail.text.isNullOrBlank()
    }

    fun validatePassword(): Boolean {
        return if (!editTextPassword.text.isNullOrBlank()) {
            editTextPassword.text.toString().length >= 6
        } else {
            false
        }
    }

    fun validatePhone(): Boolean {
        return if (!editTextPhone.text.isNullOrBlank()) {
            editTextPhone.text.toString().length >= 7
        } else {
            false
        }
    }

    fun createUser() {
        mAuth.createUserWithEmailAndPassword(
            editTextEmail.text.toString(),
            editTextPassword.text.toString()
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userID = mAuth.currentUser?.uid ?: ""
                    val user = User(
                        userID,
                        editTextFirstName.text.toString(),
                        editTextLastName.text.toString(),
                        editTextEmail.text.toString(),
                        editTextPhone.text.toString(),
                        if (editTextWebsite.text == null) "" else editTextWebsite.text.toString(),
                        if (editTextCompany.text == null) "" else editTextCompany.text.toString(),
                        if (editTextJobTitle.text == null) "" else editTextJobTitle.text.toString(),
                        userImageString,
                        CurrentUser.readArray()
                    )

                    CurrentUser.setUser(user)

                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(userID).setValue(user)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Registration Complete",
                                    Toast.LENGTH_LONG
                                ).show()
                                initializeUser(user)
                            } else {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Registration Incomplete" + task.exception!!.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun initializeUser(user: User) {
        launchMainActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        //if image is found..
        if (requestCode == SELECT_FILE) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    imageURI = data.data
                    uploadFile(bitmap)
                    setImage(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun setImage(bitmap: Bitmap) {
        val d = BitmapDrawable(resources, bitmap)
        imageViewUserProfileImage.setImageDrawable(d)
    }

    private fun uploadFile(bitmap: Bitmap) {
        loading(true)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val dataByte = baos.toByteArray()

        val path = "fireimage/" + UUID.randomUUID() + ".jpeg"//file name of the image
        val fireimageRef = firebaseStorage.getReference(path)
        val uploadTask = fireimageRef.putBytes(dataByte)
        uploadTask.addOnSuccessListener(
            this
        ) { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnCompleteListener { task ->
                userImageString = task.result.toString()
                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
                loading(false)
            }
        }
            .addOnFailureListener {
                loading(false)
                Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()
            }
    }

    private fun launchMainActivity() {
        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
        finish()
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            buttonSubmit.isEnabled = false
        } else {
            progressBar.visibility = View.INVISIBLE
            buttonSubmit.isEnabled = true
        }
    }

    companion object {
        const val EDIT_FLOW = "edit flow"
        const val SELECT_FILE = 1
    }
}