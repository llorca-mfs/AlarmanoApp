package com.makorino.alarmanoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.makorino.alarmanoapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button magnanakaw_cancel;
    private Button notmagnanakaw_cancel;

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
}