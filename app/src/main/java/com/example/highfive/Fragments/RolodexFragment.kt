package com.example.highfive.Fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.highfive.Activities.ConnectionActivity
import com.example.highfive.Activities.TransferActivity
import com.example.highfive.Models.Connection
import com.example.highfive.Models.CurrentUser
import com.example.highfive.Models.User
import com.example.highfive.R
import com.example.highfive.Adapters.RolodexRecyclerViewAdapter
import com.example.highfive.Helper.CreateUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_contact_list.*


class RolodexFragment : Fragment(),
    RolodexRecyclerViewAdapter.RolodexListener {

    private var user: User = User("", "", "", "", "", "", "", "", "", ArrayList())
    private lateinit var adapter: RolodexRecyclerViewAdapter
    private lateinit var users: ArrayList<Connection>
    private val REQUEST_PERMISSION_PHONE = 0
    private var phoneNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        floating_action_button_add.setOnClickListener { startTransfer() }

        users = CurrentUser.readArray()
        recycler_view_contacts.layoutManager = LinearLayoutManager(view.context)
        adapter = RolodexRecyclerViewAdapter(users, this)
        recycler_view_contacts.adapter = adapter


        var databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(CurrentUser.read(CurrentUser.USER_ID))
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                users =  ArrayList()
                if (p0.hasChild("connections")) {
                    for (connection in p0.child("connections").children) {
                        var tempConnection = CreateUser.createConnection(connection)
                        Toast.makeText(requireContext(), tempConnection.name, Toast.LENGTH_LONG).show()
                        users.add(tempConnection)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    private fun startTransfer() {
        val intent = Intent(requireContext(), TransferActivity::class.java)
        intent.putExtra(TransferActivity.USER_ID, CurrentUser.read(CurrentUser.USER_ID))
        startActivity(intent)
    }

    override fun onCallButton(phone: String) {
        phoneNumber = phone
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var permissions = arrayOf(Manifest.permission.CALL_PHONE)
            requestPermissions(permissions, REQUEST_PERMISSION_PHONE)
        } else {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_PHONE) {
            onCallButton(phoneNumber)
        }
    }

    override fun onEmailButton(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_EMAIL, email)
        }
        startActivity(intent)
    }

    override fun onUserSelected(user: Connection) {
        //TODO progress indicator
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(user.userId)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var fullUser = User(
                    dataSnapshot.child("userId").value.toString(),
                    dataSnapshot.child("firstName").value.toString(),
                    dataSnapshot.child("lastName").value.toString(),
                    dataSnapshot.child("email").value.toString(),
                    dataSnapshot.child("phone").value.toString(),
                    if (dataSnapshot.hasChild("website")) {
                        dataSnapshot.child("website").value.toString()
                    } else {
                        ""
                    },
                    if (dataSnapshot.hasChild("company")) {
                        dataSnapshot.child("company").value.toString()
                    } else {
                        ""
                    },
                    if (dataSnapshot.hasChild("jobTitle")) {
                        dataSnapshot.child("jobTitle").value.toString()
                    } else {
                        ""
                    },
                    if (dataSnapshot.hasChild("picture")) {
                        dataSnapshot.child("picture").value.toString()
                    } else {
                        ""
                    },
                    ArrayList()
                )

                var intent = Intent(requireContext(), ConnectionActivity::class.java)
                updateConnection(fullUser, user)
                intent.putExtra(ConnectionActivity.USER, fullUser)
                intent.putExtra(ConnectionActivity.NOTES, user.note)
                startActivity(intent)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

    private fun updateConnection(user: User, connection: Connection) {
        var shouldUpdate: Boolean = false
        var userName = user.firstName + " " + user.lastName
        if (connection.name != userName) {
            connection.name = userName
            shouldUpdate = true
        }
        if (connection.company != user.company) {
            connection.company = user.company ?: ""
            shouldUpdate = true
        }
        if (connection.email != user.email) {
            connection.email = user.email
            shouldUpdate = true
        }
        if (connection.phone != user.phone) {
            connection.phone = user.phone
            shouldUpdate = true
        }
        if (connection.picture != user.picture) {
            connection.picture = user.picture ?: ""
            shouldUpdate = true
        }
        if (shouldUpdate) {
            FirebaseDatabase.getInstance().getReference("Users").child(CurrentUser.USER_ID)
                .child("connections").child(connection.userId).setValue(connection)
        }
    }
}