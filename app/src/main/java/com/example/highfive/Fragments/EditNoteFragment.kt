package com.example.highfive.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.highfive.Models.CurrentUser
import com.example.highfive.Models.User
import com.example.highfive.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_edit_note.*

class EditNoteFragment(val user: User, val note: String, val listener: EditListener): Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editNoteEditText.setText(note)
        buttonSaveNote.setOnClickListener { saveNote(editNoteEditText.text?.toString() ?: "") }
    }

    private fun saveNote(note: String) {
        FirebaseDatabase.getInstance().getReference("Users").child(CurrentUser.read(CurrentUser.USER_ID)).child("connections").child(user.userId).child("note").setValue(note)
        listener.onSaveNote(user, note)
    }

    interface EditListener{
        fun onSaveNote(user: User, note: String)
    }
}