package com.example.x240.timemanagement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setting);

    }

    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Setting Activity!", Toast.LENGTH_LONG).show();
        finish();
    }
}
