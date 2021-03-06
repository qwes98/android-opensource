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

public class SchedulePage_2 extends android.support.v4.app.Fragment {
    LinearLayout linearLayout;
    ContactDBHelper dbHelper = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.page_schedule_2, container, false);

        init_tables();

        make_timeline();
        //TextView page_num = (TextView) linearLayout.findViewById(R.id.page_num);
        //page_num.setText(String.valueOf(2));

        return linearLayout;
    }

    private void init_tables() {
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }

    private void make_timeline() {      //load_values 내용 포함
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + SchedulePage_1.YearMonthDay + "'", null);

        View listView = linearLayout.findViewById(R.id.tablelayout);

        if(cursor.moveToFirst()) {
            //if(cursor.getInt(0) > 0) {
                while (cursor.isAfterLast() == false) {
                    if(cursor.getInt(3) == 4 || cursor.getInt(3) == 5) {      //etc 일에 대한 처리
                        cursor.moveToNext();
                        continue;
                    }

                    String tagName = cursor.getString(1);
                    String tagColorCode = tagMap.get(tagName);

                    //SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                    //Date expStartTime = parser.parse(cursor.getString(4));
                    //Date expEndTime = parser.parse(cursor.getString(5));
                    String[] startParts = cursor.getString(4).split(":");
                    int startTimeNum = Integer.parseInt(startParts[0] + startParts[1]);
                    String[] endParts = cursor.getString(5).split(":");
                    int endTimeNum = Integer.parseInt(endParts[0] + endParts[1]);

                    String todoName = cursor.getString(2);

                    if(endTimeNum <= 400) {             //endTime이 날을 넘긴경우
                        if(startTimeNum <= 400) {
                            changeBGColorFromTo(startTimeNum, endTimeNum, tagColorCode);
                            inputTodoName(todoName + " start!", startTimeNum);          //avgTime 찾는 알고리즘 찾기 전까지는 그냥 시작시간에 표시하는걸로..
                        }
                        else {
                            //두번 색깔 칠하기
                            changeBGColorFromTo(startTimeNum, 2400, tagColorCode);
                            changeBGColorFromTo(0, endTimeNum, tagColorCode);
                            inputTodoName(todoName + " start!", startTimeNum);
                        }

                        /*
                        //todo 띄우기
                        //minTime 찾는 알고리즘 찾으면 그거 사용하기.... 밑에 있는 건 신뢰성 없음..
                        int difTime1 = ((2400 - startTimeNum) % 2 == 0) ? (2400 - startTimeNum) / 2 : (2400 - startTimeNum) / 2 + 8;       //덧셈결과에서 아래두자릿수가 홀수이면 8을 더해줘야 결과적으로 15의 배수가 나옴
                        int difTime2 = (endTimeNum % 2 == 0) ? endTimeNum / 2 : endTimeNum / 2 + 8;
                        int difAvgTime = ((difTime1 + difTime2) % 2 == 0) ? (difTime1 + difTime2) / 2 : (difTime1 + difTime2) / 2 + 8;
                        if(difTime1 > difTime2) {
                            int avgTime = startTimeNum + difAvgTime;
                            inputTodoName(todoName, avgTime);
                        } else{
                            int avgTime = startTimeNum + difAvgTime - 2400;
                            inputTodoName(todoName, avgTime);
                        }
                        */

                    }
                    else {
                        changeBGColorFromTo(startTimeNum, endTimeNum, tagColorCode);
                        inputTodoName(todoName + " start!", startTimeNum);

                        /*
                        //todo 띄우기
                        int avgTime = ((startTimeNum + endTimeNum) % 2 == 0) ? (startTimeNum + endTimeNum) / 2 : (startTimeNum + endTimeNum) / 2 + 8;       //덧셈결과에서 아래두자릿수가 홀수이면 8을 더해줘야 결과적으로 15의 배수가 나옴
                        inputTodoName(todoName, avgTime);
                        */
                    }

                    cursor.moveToNext();
                }
            //}
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
