package com.example.x240.timemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ContactDBHelper dbHelper = null;
    TextView scheduleText, feedbackText, settingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplicationContext(), MainService.class);
        startService(intent);   // 중요!!!

        init_tables();

        scheduleText = (TextView) findViewById(R.id.scheduleText);
        scheduleText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
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

    /*
    class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {        //getWritableDatabase()함수 호출시 Database가 만들어지지 않았다면 여기서 자동으로 만들어줌
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {           //getWritableDatabase()함수 호출시 이 함수가 호출되는데, 한번만 호출되므로 테이블 생성코드를 여기에 넣으면 된다
            //String tableName = "TodoDatabase";
            String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, date text, tagName text, tagColor text, todo text, todoCheck text, expectedStartTime text, expectedEndTime text, actualStartTime text, actualEndTime text);";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        }
    }
    */
}
