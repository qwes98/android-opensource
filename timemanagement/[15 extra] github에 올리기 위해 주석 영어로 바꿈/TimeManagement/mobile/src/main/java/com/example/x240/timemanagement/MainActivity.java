package com.example.x240.timemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by One on 2017-09-01.
 * Author : Jiwon Park
 * This is MainActivity. If you run app, you can see this activity.
 */

public class MainActivity extends AppCompatActivity {
    ContactDBHelper dbHelper = null;
    TextView scheduleText, feedbackText, settingText;
    ImageView frontImage;

    int clickCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplicationContext(), MainService.class);
        startService(intent);   // Important!!

        init_tables();

        frontImage = (ImageView) findViewById(R.id.frontImage);
        frontImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    clickCount++;
                    if(clickCount > 5) {
                        clickCount = 0;
                        Intent intent = new Intent(getApplicationContext(), DeveloperActivity.class);
                        startActivity(intent);
                    }
                }
                return true;
            }
        });


        scheduleText = (TextView) findViewById(R.id.scheduleText);
        scheduleText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                    intent.putExtra("enterFromMainActivity", true);
                    startActivity(intent);
                }
                return true;
            }
        });

        feedbackText = (TextView) findViewById(R.id.feedbackText);
        feedbackText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        settingText = (TextView) findViewById(R.id.settingText);
        settingText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void init_tables() {                //mainActivity에 있는 함수를 그대로 가져옴
        dbHelper = new ContactDBHelper(this);
    }
}
