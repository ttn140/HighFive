package com.example.highfive.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.highfive.Activities.LoginActivity
import com.example.highfive.Activities.SignUpActivity
import com.example.highfive.Models.CurrentUser
import com.example.highfive.Models.User
import com.example.highfive.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user: User = CurrentUser.user

        if(user.picture.isNullOrBlank()){
            imageViewUserProfile.setImageResource(R.drawable.ic_user)
        } else {
            Picasso.get().load(user.picture).into(imageViewUserProfile)
        }

        buttonAction.setOnClickListener { edit() }
        buttonLogOut.setOnClickListener { logout() }

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
    }

    fun logout(){
        CurrentUser.clearUser()
        var intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    fun edit(){
        var intent = Intent(requireContext(), SignUpActivity::class.java)
        intent.putExtra(SignUpActivity.EDIT_FLOW, true)
        startActivity(intent)
    }

    companion object {
        fun getInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}