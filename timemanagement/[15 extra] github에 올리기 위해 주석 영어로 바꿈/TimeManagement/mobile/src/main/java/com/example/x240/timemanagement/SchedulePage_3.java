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
 * This is third page of ScheduleActivity. you can check your actual timeline.
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

        make_timeline();

        return linearLayout;
    }

    private void init_tables() {
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }

    private void make_timeline() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + SchedulePage_1.YearMonthDay + "'", null);

        View listView = linearLayout.findViewById(R.id.tablelayout);

        removeAllBGColorAndText();

        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                if (cursor.getInt(3) == 1 || cursor.getInt(3) == 2 || cursor.getInt(3) == 4 || cursor.getInt(3) == 5) {     // Exclude not start work.

                    String tagName = cursor.getString(1);
                    String tagColorCode = tagMapForBackground.get(tagName);

                    String[] startParts = cursor.getString(6).split(":");
                    int startMin = Integer.parseInt(startParts[1]);
                    String startMinStr = "00";
                    if (startMin >= 0 && startMin <= 7) {
                        startMinStr = "00";
                    } else if (startMin >= 8 && startMin <= 22) {
                        startMinStr = "15";
                    } else if (startMin >= 23 && startMin <= 37) {
                        startMinStr = "30";
                    } else if (startMin >= 38 && startMin <= 52) {
                        startMinStr = "45";
                    } else {
                        startMinStr = "00";
                    }
                    int startTimeNum = Integer.parseInt(startParts[0] + startMinStr);

                    String[] endParts = cursor.getString(7).split(":");
                    int endMin = Integer.parseInt(endParts[1]);
                    String endMinStr = "00";
                    if (endMin >= 0 && endMin <= 7) {
                        endMinStr = "00";
                    } else if (endMin >= 8 && endMin <= 22) {
                        endMinStr = "15";
                    } else if (endMin >= 23 && endMin <= 37) {
                        endMinStr = "30";
                    } else if (endMin >= 38 && endMin <= 52) {
                        endMinStr = "45";
                    } else {
                        endMinStr = "00";
                    }
                    int endTimeNum = Integer.parseInt(endParts[0] + endMinStr);

                    String todoName = cursor.getString(2);

                    if (todoName.equals("Sleeping")) {      // Change BGcolor for sleeping
                        changeBGColorFromTo(500, endTimeNum, tagColorCode);
                        cursor.moveToNext();
                        continue;
                    }

                    if (startTimeNum != endTimeNum) {
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

                }
                cursor.moveToNext();
            }
        }
    }

    private void removeAllBGColorAndText() {
        int startTime = 0;
        while(startTime < 2400) {
            String id = "doList" + Integer.toString(startTime);
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
            String id = "doList" + Integer.toString(startTime);
            TextView textView = (TextView) linearLayout.findViewById(getResources().getIdentifier(id, "id", "com.example.x240.timemanagement"));
            textView.setBackgroundColor(Color.parseColor(color));

            if(startTime == 0 || startTime == 15 || startTime == 30) { startTime += 15;}
            else if(startTime == 45) { startTime += 55; }
            else if((startTime - 45) % 100 == 0) { startTime += 55; }
            else { startTime += 15; }
        }
    }

    private void inputTodoName(String name, int time) {
        String id = "doList" + Integer.toString(time);
        TextView textView = (TextView) linearLayout.findViewById(getResources().getIdentifier(id, "id", "com.example.x240.timemanagement"));
        textView.setText(name);
        textView.setTextSize(12);
    }

}
