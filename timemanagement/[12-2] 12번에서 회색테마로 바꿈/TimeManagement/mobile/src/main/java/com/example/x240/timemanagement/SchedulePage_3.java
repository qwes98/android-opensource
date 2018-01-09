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

import static com.example.x240.timemanagement.ContactDBCtrct.tagMap;

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
        make_timeline();
        //TextView page_num = (TextView) linearLayout.findViewById(R.id.page_num);
        //page_num.setText(String.valueOf(2));

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
            while (cursor.isAfterLast() == false) {
                //여기서 문제 터지는 것도 checkbox 4인 sleeping 때문..
                if(cursor.getInt(3) == 0) {      //하지 않은 일
                    cursor.moveToNext();
                    continue;
                }

                String tagName = cursor.getString(1);
                String tagColorCode = tagMap.get(tagName);

                String[] startParts = cursor.getString(6).split(":");
                int startMin = Integer.parseInt(startParts[1]);
                String startMinStr = "00";
                //우리가 timeline의 시간간격을 15분으로 했기 때문에, 15분 이내 시간동안 어떤 작업을 끝냈을 때, 잘못하면 todoline에는 안보일 수 있다
                //따라서 이 actual timeline은 대략적인 시간 사용의 흐름을 볼 때 사용하는것이 좋다
                if(startMin >= 0 && startMin <= 7) {
                    startMinStr = "00";
                }
                else if(startMin >= 8 && startMin <= 22) {
                    startMinStr = "15";
                }
                else if(startMin >= 23 && startMin <= 37) {
                    startMinStr = "30";
                }
                else if(startMin >= 38 && startMin <= 52) {
                    startMinStr = "45";
                }
                else {
                    startMinStr = "00";
                }
                int startTimeNum = Integer.parseInt(startParts[0] + startMinStr);

                String[] endParts = cursor.getString(7).split(":");
                int endMin = Integer.parseInt(endParts[1]);
                String endMinStr = "00";
                //우리가 timeline의 시간간격을 15분으로 했기 때문에, 15분 이내 시간동안 어떤 작업을 끝냈을 때, 잘못하면(같은 시간 간격 사이에 시작과 끝이 들어갈경우) todoline에 보이지 않을 수 있다.
                //따라서 이 actual timeline은 대략적인 시간 사용의 흐름을 볼 때 사용하는것이 좋다
                if(endMin >= 0 && endMin <= 7) {
                    endMinStr = "00";
                }
                else if(endMin >= 8 && endMin <= 22) {
                    endMinStr = "15";
                }
                else if(endMin >= 23 && endMin <= 37) {
                    endMinStr = "30";
                }
                else if(endMin >= 38 && endMin <= 52) {
                    endMinStr = "45";
                }
                else {
                    endMinStr = "00";
                }
                int endTimeNum = Integer.parseInt(endParts[0] + endMinStr);

                String todoName = cursor.getString(2);

                if(startTimeNum != endTimeNum) {
                    if (endTimeNum <= 400) {             //endTime이 날을 넘긴경우
                        if (startTimeNum <= 400) {
                            changeBGColorFromTo(startTimeNum, endTimeNum, tagColorCode);
                            inputTodoName(todoName + " start!", startTimeNum);          //avgTime 찾는 알고리즘 찾기 전까지는 그냥 시작시간에 표시하는걸로..
                        } else {
                            //두번 색깔 칠하기
                            changeBGColorFromTo(startTimeNum, 2400, tagColorCode);
                            changeBGColorFromTo(0, endTimeNum, tagColorCode);
                            inputTodoName(todoName + " start!", startTimeNum);
                        }

                    } else {
                        changeBGColorFromTo(startTimeNum, endTimeNum, tagColorCode);
                        inputTodoName(todoName + " start!", startTimeNum);
                    }
                }

                cursor.moveToNext();
            }
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
