package com.makorino.alarmanoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.makorino.alarmanoapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        binding.btnMagnanakaw.setOnClickListener(v ->{
            Toast.makeText(this, "Magnanakaw detected!", Toast.LENGTH_SHORT).show();
        });

        binding.btnNotmagnanakaw.setOnClickListener(v ->{
            Toast.makeText(this, "Hindi Magnanakaw", Toast.LENGTH_SHORT).show();
        });

        setContentView(binding.getRoot());
    }
}