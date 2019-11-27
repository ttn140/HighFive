package com.example.highfive.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.example.highfive.Models.ContactObject;
import com.example.highfive.Fragments.ProfileFragment;
import com.example.highfive.R;
import com.example.highfive.Util.UtilHelper;

import org.jetbrains.annotations.NotNull;


public class TransferActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback, ProfileFragment.OnFragmentInteractionListener {

    AlertDialog dialog;
    NdefMessage ndefMessage;
    private static final int MESSAGE_SENT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);


        //TODO: SET UP A LISTENER FOR NEW CONTACTS HERE

        findViewById(R.id.buttonNFC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(TransferActivity.this);
                if (mAdapter == null) {
                    Toast.makeText(getApplicationContext(), "NFC is not supported", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if (!mAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
                }else{
                    AlertDialog.Builder alertDialogueBuilder = new AlertDialog.Builder(TransferActivity.this);
                    alertDialogueBuilder.setMessage("Hold phones back to back to transfer business cards");
                     dialog = alertDialogueBuilder.create();
                    dialog.show();
                    mAdapter.setNdefPushMessageCallback(TransferActivity.this, TransferActivity.this);
                    mAdapter.setOnNdefPushCompleteCallback(TransferActivity.this, TransferActivity.this);
                }
            }
        });

        //TODO: IMPLEMENT QR CODE HERE.


    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        //TODO: NEED TO GEt USER ID
        String userID = "user id goes here";
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", userID.getBytes());
         ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
            dialog.dismiss();
       mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /** This handler receives a message from onNdefPushComplete */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Business card successfully transferred", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        final String contactUserId;

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.getAction()) {
            contactUserId = UtilHelper.getNFCmessage(getIntent());

            //TODO: GET FIREBASE OBJECT BASED ON CONTACTUSERID HERE. ASSIGNED TO A CONTACTOBJECT.
            final ContactObject contactObject = new ContactObject("02154625555w", "JD", "James", "email@gmail.com", "7045658456",
                    "www.website.com", "Working the Web", "3874 W Charlotte", "", "", "Accountant");

            AlertDialog.Builder alertDialogueBuilder = new AlertDialog.Builder(TransferActivity.this);
            alertDialogueBuilder.setTitle("New Contact")
                            .setMessage("You got a new contact. Would you like to view them?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.layoutFrame,ProfileFragment.newInstance(contactObject));
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

            dialog = alertDialogueBuilder.create();
            dialog.show();

        }
    }


    @Override
    public void onFragmentInteraction(@NotNull Uri uri) {

    }
}

