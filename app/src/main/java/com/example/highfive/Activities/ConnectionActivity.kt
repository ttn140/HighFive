package com.example.highfive.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.highfive.Fragments.ConnectionFragment
import com.example.highfive.Fragments.EditNoteFragment
import com.example.highfive.Models.User
import com.example.highfive.R

class ConnectionActivity() : AppCompatActivity(), ConnectionFragment.ConnectionInterface, EditNoteFragment.EditListener{
    override fun onSaveNote(user: User, note: String) {
        setFragment(ConnectionFragment(user, note, this))
    }

    override fun onEditNote(user: User, note: String) {
        setFragment(EditNoteFragment(user, note, this))
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        if(intent.hasExtra(USER) && intent.hasExtra(NOTES)){
            val user = intent.getParcelableExtra<User>(USER)
            val notes = intent.getStringExtra(NOTES)
            setFragment(ConnectionFragment(user, notes, this))
        }

    }

    private fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }



    companion object{
        val USER = "user"
        val NOTES = "notes"
    }
}