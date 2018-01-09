package com.example.x240.timemanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;

/**
 * Created by X240 on 2017-09-01.
 */

public class SchedulePage_3 extends android.support.v4.app.Fragment {
    LinearLayout linearLayout;
    ContactDBHelper dbHelper = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.page_schedule_3, container, false);

        init_tables();
        //TextView page_num = (TextView) linearLayout.findViewById(R.id.page_num);
        //page_num.setText(String.valueOf(2));

        return linearLayout;
    }

    private void init_tables() {
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }

    private void load_values() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + SchedulePage_1.YearMonthDay + "'", null);

        View listView = linearLayout.findViewById(R.id.tablelayout);
        ((TableLayout) listView).removeAllViews();

        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                if(cursor.getInt(3) == 10) {      //etc 일에 대한 처리
                    //다른 방식의 처리
                    continue;
                }
                cursor.moveToNext();
            }
        }
    }

}
