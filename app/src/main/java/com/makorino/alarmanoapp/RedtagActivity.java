package com.makorino.alarmanoapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.makorino.alarmanoapp.databinding.ActivityRedtagBinding;

public class RedtagActivity extends AppCompatActivity {

    private ActivityRedtagBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redtag);

        binding = ActivityRedtagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
