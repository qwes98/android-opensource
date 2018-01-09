package com.example.x240.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class MenuActivity extends AppCompatActivity {
    ContactDBHelper dbHelper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init_tables();

        load_values();
    }

    private void init_tables() {                //mainActivity에 있는 함수를 그대로 가져옴
        dbHelper = new ContactDBHelper(this);
    }

    private void load_values() {                //mainActivity에 있는 함수를 그대로 가져와 EditText의 id만 바꿔줌
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT, null);

        if(cursor.moveToFirst()) {
            int no = cursor.getInt(0);
            EditText editTextNo = (EditText) findViewById(R.id.editText);
            editTextNo.setText(Integer.toString(no));

            String name = cursor.getString(1);
            EditText editTextName = (EditText) findViewById(R.id.editText2);
            editTextName.setText(name);

            String phone = cursor.getString(2);
            EditText editTextPhone = (EditText) findViewById(R.id.editText3);
            editTextPhone.setText(phone);

        }
    }
}
