package com.makorino.alarmanoapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.makorino.alarmanoapp.databinding.ActivityRedtagBinding;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class RedtagActivity extends AppCompatActivity {

    private ActivityRedtagBinding binding;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writingTagFilters[];
    boolean writeMode;
    Tag myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redtag);

        binding = ActivityRedtagBinding.inflate(getLayoutInflater());

        //change status and toolbar color programatically
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.red_700));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_500)));

        binding.btnRedtagNew.setOnClickListener(v ->{
            try {
                if (myTag == null) {
                    Toast.makeText(this, "Cannot Detect NFC Tag", Toast.LENGTH_LONG).show();
                } else {
                    write("magnanakaw", myTag);
                    Toast.makeText(this, "Write Success", Toast.LENGTH_LONG).show();
                }
            }
            catch(IOException e){
                Toast.makeText(this, "Cannot Detect NFC Tag", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            catch(FormatException e){
                Toast.makeText(this, "Cannot Detect NFC Tag", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this, "Device has no NFC sensor", Toast.LENGTH_SHORT).show();
            finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[] {tagDetected};

        setContentView(binding.getRoot());
    }

    private void write(String text, Tag tag) throws IOException, FormatException{
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        payload[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        setWriteMode(false);

    }

    @Override
    public void onResume(){
        super.onResume();
        setWriteMode(true);

    }

    private void setWriteMode(boolean x){
        writeMode = x;
        if (x){
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null);
        }
        else{
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
}
