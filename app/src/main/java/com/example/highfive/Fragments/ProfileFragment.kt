package com.example.highfive.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.highfive.Models.ContactObject
import com.example.highfive.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

private const val ARG_PARAM = "contactObject"


class ProfileFragment : Fragment(), View.OnClickListener {


    // TODO: Rename and change types of parameters
    private var contactObject: ContactObject? = null
    private var listener: OnFragmentInteractionListener? = null
    private val EDIT_NOTE = "Edit Note"
    private val SAVE_NOTE = "Save Note"
    private val ADD_NOTE = "Add Note"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactObject = it.getSerializable(ARG_PARAM) as ContactObject?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.textViewFirstName.text = contactObject!!.firstName
        view.textViewLastName.text = contactObject!!.lastName
        view.textViewAddress.text = contactObject?.address
        view.textViewPhone.text = contactObject?.phone
        view.textViewCompanyName.text = contactObject?.company
        view.textViewEmail.text = contactObject!!.email
        view.textViewJobTitle.text = contactObject?.jobTitle
        view.textViewWebSite.text = contactObject?.website
        if (contactObject?.picture.isNullOrBlank()) {
            view.imageViewUserProfileImage.setImageResource(R.drawable.profile_icon)
        } else {
            Picasso.get().load(contactObject?.picture).into(view.imageViewUserProfileImage)
        }

        //TODO: GET CURRENT USERID HERE.
        if(contactObject!!.userID == "02154625555w"){
            view.buttonEditNotes.visibility = INVISIBLE
            view.editTextNotes.visibility = INVISIBLE
            view.buttonEditProfile.visibility = VISIBLE
            view.buttonEditProfile.setOnClickListener(this)

        }else{

            view.buttonEditNotes.visibility = VISIBLE
            if(contactObject!!.notes.isNullOrEmpty()){
                view.buttonEditNotes.text = ADD_NOTE
            }else view.buttonEditNotes.text = EDIT_NOTE
            view.editTextNotes.visibility = VISIBLE
            view!!.editTextNotes.setText(contactObject?.notes, TextView.BufferType.EDITABLE)
            view.buttonEditNotes.setOnClickListener(this)
            view.editTextNotes.isEnabled = false
            view.buttonEditProfile.visibility = INVISIBLE
        }
        view.buttonSaveProfile.visibility = INVISIBLE
        view.buttonCancelEdit.visibility = INVISIBLE


        return view
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param contactObject Parameter 1.
         * @return A new instance of fragment ProfileFragment.
         */
        @JvmStatic
        fun newInstance(contactObject: ContactObject) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM, contactObject)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonEditNotes -> {
                if (view!!.buttonEditNotes.text.equals(EDIT_NOTE) || view!!.buttonEditNotes.text.equals(ADD_NOTE)) {
                    view!!.editTextNotes.isEnabled = true
                    view!!.buttonEditNotes.text = SAVE_NOTE
                    view!!.buttonCancelNote.visibility = VISIBLE
                    view!!.buttonCancelNote.setOnClickListener(this)
                } else {
                    if(view!!.editTextNotes.text.isNullOrEmpty()){
                        //TODO: CLEAR NOTES IN FIREBASE.
                        view!!.buttonEditNotes.text = SAVE_NOTE

                    }else{
                        //TODO: SAVE NOTES TO FIREBASE HERE]
                        view!!.buttonEditNotes.text = EDIT_NOTE

                    }
                    view!!.editTextNotes.isEnabled = false
                    view!!.buttonCancelNote.visibility = INVISIBLE
                }

            }
            R.id.buttonEditProfile ->{
                showEditTextProfile();
            }
            R.id.buttonCancelNote ->{
                if(contactObject?.notes.isNullOrEmpty()){
                    view!!.buttonEditNotes.text = ADD_NOTE
                } else view!!.buttonEditNotes.text = EDIT_NOTE

                view!!.editTextNotes.setText(contactObject?.notes, TextView.BufferType.EDITABLE)
                view!!.editTextNotes.isEnabled = false
                view!!.buttonCancelNote.visibility = INVISIBLE
            }
            R.id.buttonSaveProfile->{
                val newProfile = ContactObject("currentUserID",
                    editTextFirstName.text.toString(),
                    editTextLastName.text.toString(),
                    editTextEmail.text.toString(),
                    editTextPhone.text.toString(),
                    editTextWebsite.text.toString(),
                    editTextCompanyName.text.toString(),
                    editTextAddress.text.toString(),
                    "image",
                    "",
                    editTextJobTitle.text.toString());

                //TODO: GET IMAGE
                //TODO: WRITE NEW PROFILE TO FIREBASE
                showTextViewProfile()

            }
            R.id.buttonCancelEdit->{
                showTextViewProfile();
            }

        }
    }

    private fun showEditTextProfile() {
        buttonEditProfile.visibility = INVISIBLE
        buttonSaveProfile.visibility = VISIBLE
        buttonCancelEdit.visibility = VISIBLE
        buttonSaveProfile.setOnClickListener(this)
        buttonCancelEdit.setOnClickListener(this)

        textViewFirstName.visibility = INVISIBLE
        editTextFirstName.visibility = VISIBLE
        editTextFirstName.setText(contactObject?.firstName, TextView.BufferType.EDITABLE)

        textViewLastName.visibility = INVISIBLE
        editTextLastName.visibility = VISIBLE
        editTextLastName.setText(contactObject?.lastName, TextView.BufferType.EDITABLE)


        textViewJobTitle.visibility = INVISIBLE
        editTextJobTitle.visibility = VISIBLE
        editTextJobTitle.setText(contactObject?.jobTitle, TextView.BufferType.EDITABLE)


        textViewCompanyName.visibility = INVISIBLE
        editTextCompanyName.visibility = VISIBLE
        editTextCompanyName.setText(contactObject?.company, TextView.BufferType.EDITABLE)


        textViewAddress.visibility = INVISIBLE
        editTextAddress.visibility = VISIBLE
        editTextAddress.setText(contactObject?.address, TextView.BufferType.EDITABLE)


        textViewWebSite.visibility = INVISIBLE
        editTextWebsite.visibility = VISIBLE
        editTextWebsite.setText(contactObject?.website, TextView.BufferType.EDITABLE)


        textViewEmail.visibility = INVISIBLE
        editTextEmail.visibility = VISIBLE
        editTextEmail.setText(contactObject?.email, TextView.BufferType.EDITABLE)


        textViewPhone.visibility = INVISIBLE
        editTextPhone.visibility = VISIBLE
        editTextPhone.setText(contactObject?.phone, TextView.BufferType.EDITABLE)

    }

    private fun showTextViewProfile() {
        buttonEditProfile.visibility = VISIBLE
        buttonSaveProfile.visibility = INVISIBLE
        buttonCancelEdit.visibility = INVISIBLE
        buttonSaveProfile.setOnClickListener(this)

        textViewFirstName.visibility = VISIBLE
        editTextFirstName.visibility = INVISIBLE

        textViewLastName.visibility = VISIBLE
        editTextLastName.visibility = INVISIBLE

        textViewJobTitle.visibility = VISIBLE
        editTextJobTitle.visibility = INVISIBLE

        textViewCompanyName.visibility = VISIBLE
        editTextCompanyName.visibility = INVISIBLE

        textViewAddress.visibility = VISIBLE
        editTextAddress.visibility = INVISIBLE

        textViewWebSite.visibility = VISIBLE
        editTextWebsite.visibility = INVISIBLE

        textViewEmail.visibility = VISIBLE
        editTextEmail.visibility = INVISIBLE

        textViewPhone.visibility = VISIBLE
        editTextPhone.visibility = INVISIBLE
    }
}
