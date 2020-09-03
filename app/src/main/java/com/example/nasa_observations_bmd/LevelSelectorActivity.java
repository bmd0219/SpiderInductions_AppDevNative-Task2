package com.example.nasa_observations_bmd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LevelSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selector);

        Button level1 = findViewById(R.id.level_1);
        Button level2 = findViewById(R.id.level_2);

        Intent intent1 = new Intent(this, APODActivity.class);
        Intent intent2 = new Intent(this, SearchActivity.class);


        level1.setOnClickListener(view -> {
            startActivity(intent1);
//            finish();
        });

        level2.setOnClickListener(view -> {
            startActivity(intent2);
//            finish();
        });
    }
}