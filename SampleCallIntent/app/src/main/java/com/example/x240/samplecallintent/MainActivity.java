package com.example.x240.samplecallintent;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
/*
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String data = editText.getText().toString();

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(data));
                startActivity(intent);
            }});
        */
    }

    public void onButton1Clicked(View v) {
        Intent intent = new Intent();

        ComponentName name = new ComponentName("org.androidtown.sampleintent", "org.androidtown.sampleintent.MenuActivity");
        intent.setComponent(name);
        startActivityForResult(intent, 100);
    }

}
