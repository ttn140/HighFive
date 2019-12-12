package com.example.highfive.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.highfive.Models.User
import com.example.highfive.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_connection.*

class ConnectionFragment(val user: User, val notes: String, val listener: ConnectionInterface) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(user.picture.isNullOrBlank()){
            imageViewUserProfile.setImageResource(R.drawable.ic_user)
        } else {
            Picasso.get().load(user.picture).into(imageViewUserProfile)
        }

        val userName = user.firstName + " " + user.lastName
        textViewUserName.text = userName
        textViewPhone.text = user.phone
        textViewEmail.text = user.email

        if(user.website.isNullOrBlank()){
            textViewWebsite.visibility = View.GONE
        } else {
            textViewWebsite.visibility = View.VISIBLE
            textViewWebsite.text = user.website
        }

        if(user.company.isNullOrBlank()){
            textViewCompany.visibility = View.GONE
        } else {
            textViewCompany.visibility = View.VISIBLE
            textViewCompany.text = user.company
        }

        if(user.jobTitle.isNullOrBlank()){
            textViewJobTitle.visibility = View.GONE
        } else {
            textViewJobTitle.visibility = View.VISIBLE
            textViewJobTitle.text = user.jobTitle
        }

        if(notes.isNullOrBlank()){
            textViewNotes.visibility = View.GONE
        } else {
            textViewNotes.visibility = View.VISIBLE
            textViewNotes.text = notes
        }

        buttonAction.setOnClickListener{ listener.onEditNote(user, notes)}
    }

    interface ConnectionInterface{
        fun onEditNote(user: User, note: String)
    }
}