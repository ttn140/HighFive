package com.example.highfive.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.highfive.Helper.CreateUser
import com.example.highfive.Helper.QRCodeGenerator
import com.example.highfive.Models.Connection
import com.example.highfive.Models.CurrentUser
import com.example.highfive.Models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_transfer.*


class TransferActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback,
    NfcAdapter.OnNdefPushCompleteCallback {

    private var userId: String = ""
    private lateinit var dialog: AlertDialog
    private val MESSAGE_SENT: Int = 1
    private val REQUEST_PERMISSION_CAMERA = 100

    private var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == MESSAGE_SENT) {
                Toast.makeText(
                    getApplicationContext(),
                    "Business card successfully transferred",
                    Toast.LENGTH_LONG
                ).show();

            }
        }
    }

    override fun createNdefMessage(p0: NfcEvent?): NdefMessage {
        val ndefRecord = NdefRecord.createMime("text/plain", userId.toByteArray())
        return NdefMessage(ndefRecord)
    }

    override fun onNdefPushComplete(p0: NfcEvent?) {
        dialog.dismiss()
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.highfive.R.layout.activity_transfer)
        if (intent.hasExtra(USER_ID)) {
            userId = intent.getStringExtra(USER_ID)
        }
        imageView.setImageBitmap(QRCodeGenerator.textToImage(userId, 400, 400))
        buttonNFC.setOnClickListener { nfcTransfer() }
        buttonQR.setOnClickListener { QRTransfer() }
    }

    private fun QRTransfer() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var permissions = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permissions, REQUEST_PERMISSION_CAMERA)
        } else {
            var integrator: IntentIntegrator = IntentIntegrator(this@TransferActivity)
            integrator.initiateScan()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        if (scanResult != null) {
            pushProfiles(scanResult.contents)
        }
    }

    private fun nfcTransfer() {
        val mAdapter: NfcAdapter = NfcAdapter.getDefaultAdapter(this@TransferActivity)
        if (!mAdapter.isEnabled) {
            Toast.makeText(applicationContext, "Please enable NFC via Settings.", Toast.LENGTH_LONG)
                .show();
        } else {
            val alertDialogueBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialogueBuilder.setMessage("Hold phones back to back to transfer business cards")
            dialog = alertDialogueBuilder.create()
            dialog.show()
            mAdapter.setNdefPushMessageCallback(this, this)
            mAdapter.setOnNdefPushCompleteCallback(this, this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            var contactUserId = getNFCmessage(intent)
            //Toast.makeText(this, contactUserId, Toast.LENGTH_LONG).show()
            pushProfiles(contactUserId)

        }
    }

    fun getNFCmessage(intent: Intent): String {
        val rawMessages = intent.getParcelableArrayExtra(
            NfcAdapter.EXTRA_NDEF_MESSAGES
        )

        val message = rawMessages[0] as NdefMessage // only one message transferred
        return String(message.records[0].payload)
    }

    fun pushProfiles(newUserId: String) {
        //Push to current user
        val currentUserReference =
            FirebaseDatabase.getInstance().getReference("Users")
                .child(CurrentUser.read(CurrentUser.USER_ID))
        val newUserReference = FirebaseDatabase.getInstance().getReference("Users").child(newUserId)

        newUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var newUser: User = CreateUser.createUser(p0, newUserId)
                var newConnection: Connection = CreateUser.createConnection(newUser)
                var array = CurrentUser.readArray()
                array.add(newConnection)
                CurrentUser.writeArray(array)
                currentUserReference.child("connections").child(newUserId).setValue(newConnection)
                var currentConnection: Connection = CreateUser.createConnection(CurrentUser.user)
                newUserReference.child("connections").child(currentConnection.userId)
                    .setValue(currentConnection)
                Toast.makeText(this@TransferActivity, "Connection Made", Toast.LENGTH_LONG).show()
            }

        })


    }

    companion object {
        val USER_ID = "userId"
    }
}