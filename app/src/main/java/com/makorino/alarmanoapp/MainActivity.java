package com.makorino.alarmanoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.makorino.alarmanoapp.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button magnanakaw_cancel;
    private Button notmagnanakaw_cancel;

    //NFC vars
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writingTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        final MediaPlayer mpWarning = MediaPlayer.create(this,R.raw.warning);
        final MediaPlayer mpDing = MediaPlayer.create(this,R.raw.ding);

        binding.btnMagnanakaw.setOnClickListener(v ->{
            createMagnanakawDialog();
            mpWarning.start();
        });

        binding.btnNotmagnanakaw.setOnClickListener(v ->{
            createNotmagnanakawDialog();
            mpDing.start();
        });

        //NFC write stuff

        //context = this;
        binding.btnRedtag.setOnClickListener(v ->{
            try {
                if (myTag == null) {
                    Toast.makeText(this, "No Tag Available", Toast.LENGTH_LONG).show();
                } else {
                    write("PlainText|" + binding.etInputfield.getText().toString(), myTag);
                    Toast.makeText(this, "Write Success", Toast.LENGTH_LONG).show();
                }
            }
            catch(IOException e){
                Toast.makeText(this, "IOException occurred", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            catch(FormatException e){
                Toast.makeText(this, "FormatException occurred", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this, "Device has no NFC sensor", Toast.LENGTH_SHORT).show();
            finish();
        }
        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_IMMUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[] {tagDetected};

        setContentView(binding.getRoot());
    }
    public void createMagnanakawDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View magnanakawPopupView = getLayoutInflater().inflate(R.layout.popup_magnanakaw, null);
        magnanakaw_cancel = (Button) magnanakawPopupView.findViewById(R.id.btn_magnanakaw_cancel);

        dialogBuilder.setView(magnanakawPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        magnanakaw_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void createNotmagnanakawDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View notmagnanakawPopupView = getLayoutInflater().inflate(R.layout.popup_notmagnanakaw, null);
        notmagnanakaw_cancel = (Button) notmagnanakawPopupView.findViewById(R.id.btn_notmagnanakaw_cancel);

        dialogBuilder.setView(notmagnanakawPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        notmagnanakaw_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_redtag:
                //Toast.makeText(this, "Red Tagger Here", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MainActivity.this, RedtagActivity.class);
                startActivity(i);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //stuff that deals with NFC
    private void readFromIntent(Intent intent){
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i<rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs){
        if (msgs == null || msgs.length ==0) return;
        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;

        try{
            text = new String(payload, languageCodeLength+1, payload.length - languageCodeLength-1, textEncoding);
        }
        catch(UnsupportedEncodingException e){
            Log.e("UnsupportedEncoding", e.toString());
        }
        Toast.makeText(this, "Read the NFC tag", Toast.LENGTH_LONG).show();
        binding.tvNfccontents.setText("NFC Content: " + text);
    }

    private void write(String text, Tag tag) throws IOException, FormatException{
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException{
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
        readFromIntent(intent);
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