package com.example.highfive.Util;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

public class UtilHelper {

    public static String getNFCmessage(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
        return new String(message.getRecords()[0].getPayload());
    }
}
