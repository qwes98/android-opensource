package com.example.x240.timemanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.x240.timemanagement.ContactDBCtrct.tagMapForBackground;

/**
 * Created by One on 2017-09-01.
 * Author : Jiwon Park
 * This is second page of ScheduleActivity. you can check your expected timeline.
 */

public class SchedulePage_2 extends android.support.v4.app.Fragment {
    LinearLayout linearLayout;
    ContactDBHelper dbHelper = null;

    boolean first = true;
    boolean enterPage = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // When page_1 is made, page_2 is also made (for erase delay when scroll page). So if you change date in this page, page_2 is not changed.
    // For solving this problem, in setMenuVisibility() that runs before onCreateView() and when coming in or out, make_timeline() is called.
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(first) {
            enterPage = true;
        }
        else if(enterPage) {
            make_timeline();
            enterPage = false;
        }
        else {
            removeAllBGColorAndText();
            enterPage = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.page_schedule_2, container, false);

        init_tables();

        make_timeline();
        first = false;

        return linearLayout;
    }

    private void init_tables() {
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }


    private void make_timeline() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + SchedulePage_1.YearMonthDay + "'", null);

        View listView = linearLayout.findViewById(R.id.tablelayout);


        if(cursor.moveToFirst()) {
            //if(cursor.getInt(0) > 0) {
                while (cursor.isAfterLast() == false) {
                    if (cursor.getInt(3) == 0 || cursor.getInt(3) == 1 || cursor.getInt(3) == 2) {      // Exclude etc, added work.

                        String tagName = cursor.getString(1);
                        String tagColorCode = tagMapForBackground.get(tagName);

                        //SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                        //Date expStartTime = parser.parse(cursor.getString(4));
                        //Date expEndTime = parser.parse(cursor.getString(5));
                        String[] startParts = cursor.getString(4).split(":");
                        int startTimeNum = Integer.parseInt(startParts[0] + startParts[1]);
                        String[] endParts = cursor.getString(5).split(":");
                        int endTimeNum = Integer.parseInt(endParts[0] + endParts[1]);

                        String todoName = cursor.getString(2);

                        if (endTimeNum <= 400) {             // After midnight
                            if (startTimeNum <= 400) {
                                changeBGColorFromTo(startTimeNum, endTimeNum, tagColorCode);
                                inputTodoName(todoName, startTimeNum);
                            } else {
                                // Change BGcolor twice (before midnight and after midnight)
                                changeBGColorFromTo(startTimeNum, 2400, tagColorCode);
                                changeBGColorFromTo(0, endTimeNum, tagColorCode);
                                inputTodoName(todoName, startTimeNum);
                            }
                        } else {
                            changeBGColorFromTo(startTimeNum, endTimeNum, tagColorCode);
                            inputTodoName(todoName, startTimeNum);
                        }

                    }
                    cursor.moveToNext();
                }
            //}
        }
    }

    private void removeAllBGColorAndText() {
        int startTime = 0;
        while(startTime < 2400) {
            String id = "todoList" + Integer.toString(startTime);
            TextView textView = (TextView) linearLayout.findViewById(getResources().getIdentifier(id, "id", "com.example.x240.timemanagement"));
            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setText("");

            if(startTime == 0 || startTime == 15 || startTime == 30) { startTime += 15;}
            else if(startTime == 45) { startTime += 55; }
            else if((startTime - 45) % 100 == 0) { startTime += 55; }
            else { startTime += 15; }
        }

    }
    private void changeBGColorFromTo(int startTime, int endTime, String color) {
        while(startTime < endTime) {
            String id = "todoList" + Integer.toString(startTime);
            TextView textView = (TextView) linearLayout.findViewById(getResources().getIdentifier(id, "id", "com.example.x240.timemanagement"));
            textView.setBackgroundColor(Color.parseColor(color));

            if(startTime == 0 || startTime == 15 || startTime == 30) { startTime += 15;}
            else if(startTime == 45) { startTime += 55; }
            else if((startTime - 45) % 100 == 0) { startTime += 55; }
            else { startTime += 15; }
        }
    }

    private void inputTodoName(String name, int time) {
        String id = "todoList" + Integer.toString(time);
        TextView textView = (TextView) linearLayout.findViewById(getResources().getIdentifier(id, "id", "com.example.x240.timemanagement"));
        textView.setText(name);
        textView.setTextSize(12);
    }

}
