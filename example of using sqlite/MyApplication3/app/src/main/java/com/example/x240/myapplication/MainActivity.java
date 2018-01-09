package com.example.x240.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

//ContactDBCtrct파일과 ContactDBHelper파일을 import할 필요 없음

public class MainActivity extends AppCompatActivity {
    ContactDBHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_tables();

        // load values from db.
        load_values() ;

        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_values();
            }
        });

        Button buttonMenu = (Button) findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

    }

    private void init_tables() {
        dbHelper = new ContactDBHelper(this);
    }


    //내가 추가한것.... update 아주 잘 됨!!! AND 연산자 사용가능!!
    private void update_values() {
        EditText editTextNo = (EditText) findViewById(R.id.editTextNo);
        String no = editTextNo.getText().toString();

        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        String name = editTextName.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PHONE", "isChanged!!!!!!!!");
        db.update("CONTACT_T", contentValues, "NO = ? AND NAME = ?",new String[] { no, name });
    }

    private void load_values() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT, null);

        if(cursor.moveToFirst()) {
            int no = cursor.getInt(0);
            EditText editTextNo = (EditText) findViewById(R.id.editTextNo);
            editTextNo.setText(Integer.toString(no));

            String name = cursor.getString(1);
            EditText editTextName = (EditText) findViewById(R.id.editTextName);
            editTextName.setText(name);

            String phone = cursor.getString(2);
            EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone);
            editTextPhone.setText(phone);

            int over20 = cursor.getInt(3);
            CheckBox checkBoxOver20 = (CheckBox) findViewById(R.id.checkBoxOver20);
            if(over20 == 0) {
                checkBoxOver20.setChecked(false);
            }
            else {
                checkBoxOver20.setChecked(true);
            }
        }
    }

    private void save_values() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL((ContactDBCtrct.SQL_DELETE));

        EditText editTextNo = (EditText) findViewById(R.id.editTextNo);
        int no = Integer.parseInt(editTextNo.getText().toString());

        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        String name = editTextName.getText().toString();

        EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        String phone = editTextPhone.getText().toString();

        CheckBox checkBoxOver20 = (CheckBox) findViewById(R.id.checkBoxOver20);
        boolean isOver20 = checkBoxOver20.isChecked();

        String sqlInsert = ContactDBCtrct.SQL_INSERT +
                " (" +
                Integer.toString(no) + ", " +
                "'" + name + "', " +
                "'" + phone + "', " +
                ((isOver20) == true ? "1" : "0") +
                ")" ;

        db.execSQL(sqlInsert);
    }
}
