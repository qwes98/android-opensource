package com.example.x240.timemanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.example.x240.timemanagement.ContactDBCtrct.dateChangeHour;

/**
 * Created by One on 2017-09-01.
 * Author : Jiwon Park
 * This is second page of FeedbackActivity.
 */

public class FeedbackPage_2 extends android.support.v4.app.Fragment {
    LinearLayout linearLayout;

    EditText starttext, endtext;
    String startDate, endDate;
    ContactDBHelper dbHelper = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.page_feedback_2, container, false);
        init_tables();

        Button button = (Button) linearLayout.findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                starttext = (EditText) linearLayout.findViewById(R.id.startdate);
                startDate = starttext.getText().toString();
                endtext = (EditText) linearLayout.findViewById(R.id.enddate);
                endDate = endtext.getText().toString();
                getStatistics();
            }
        });

        return linearLayout;
    }



    private void init_tables() {
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }

    private String getBeforeTheDay(String yearMonthDay) {
        String[] parts = yearMonthDay.split("-");
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // parts[1] value means Month and that value is one less than actual. So we have to subtract 1.
        cal.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String beforeDay = sdfNow2.format(cal.getTime());
        return beforeDay;
    }

    private String getAfterTheDay(String yearMonthDay) {
        String[] parts = yearMonthDay.split("-");
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String afterDay = sdfNow2.format(cal.getTime());
        return afterDay;
    }

    private boolean checkExistSleepingTime(String YearMonthDay) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + YearMonthDay + "'", null);

        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                if(cursor.getString(1).equals("Sleeping")) {
                    return true;
                }
                cursor.moveToNext();
            }
        }
        return false;
    }

    private int getStatistics() {
        String date = startDate;

        //각 tag 시간 (end sleeping ~ now)
        int studyMin = 0;
        int selfDevelopMin = 0;
        int exerciseMin = 0;
        int leisureMin = 0;
        int breatheMin = 0;
        int napMin = 0;


        while(!getAfterTheDay(endDate).equals(date)) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + date + "'", null);

            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() == false) {

                    if (cursor.getInt(3) == 0 || cursor.getInt(3) == 1 || cursor.getInt(3) == 2 || cursor.getInt(3) == 4 || cursor.getInt(3) == 5) {     // Exclude memo
                        // 1. Get data for sleepTime
                        if (cursor.getString(1).equals("Sleeping")) {
                            cursor.moveToNext();
                            continue;
                        }

                        // 2. Get data for time rates of Tags
                        if (cursor.getInt(3) != 0) {
                            Date startDate = new Date();
                            Date endDate = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                            String startHourMin = cursor.getString(6);
                            String endHourMin = cursor.getString(7);
                            String[] tmpStartParts = startHourMin.split(":");
                            String[] tmpEndParts = endHourMin.split(":");
                            try {
                                if (Integer.parseInt(tmpStartParts[0]) > dateChangeHour) {
                                    startDate = dateFormat.parse(cursor.getString(0) + " " + startHourMin);
                                } else {
                                    startDate = dateFormat.parse(getAfterTheDay(cursor.getString(0)) + " " + startHourMin);
                                }

                                if (Integer.parseInt(tmpEndParts[0]) > dateChangeHour) {
                                    endDate = dateFormat.parse(cursor.getString(0) + " " + endHourMin);
                                } else {
                                    endDate = dateFormat.parse(getAfterTheDay(cursor.getString(0)) + " " + endHourMin);
                                }
                            } catch (ParseException e) {
                                Toast.makeText(getContext(), "Parse exception in first!", Toast.LENGTH_SHORT).show();
                            }

                            int minute = (int) ((endDate.getTime() - startDate.getTime()) / 60000);

                            if (cursor.getString(1).equals("Study")) {
                                studyMin += minute;
                            } else if (cursor.getString(1).equals("Self Development")) {
                                selfDevelopMin += minute;
                            } else if (cursor.getString(1).equals("Exercise")) {
                                exerciseMin += minute;
                            } else if (cursor.getString(1).equals("Leisure")) {
                                leisureMin += minute;
                            } else if (cursor.getString(1).equals("Breathe")) {
                                breatheMin += minute;
                            } else if (cursor.getString(1).equals("Napping")) {
                                napMin += minute;
                            }

                        }

                    }
                    cursor.moveToNext();
                }
            }
            date = getAfterTheDay(date);
        }

        int totalMin =  studyMin + selfDevelopMin + exerciseMin + leisureMin + breatheMin + napMin;

        if(totalMin <= 0) {
            Toast.makeText(getContext(), "There is no data!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        TextView studyPercent = (TextView) linearLayout.findViewById(R.id.studyPercent);
        studyPercent.setTextColor(getResources().getColor(R.color.textColor));
        studyPercent.setText(Integer.toString(studyMin / 60) + ":" + Integer.toString(studyMin % 60) + "(" + Integer.toString(studyMin * 100 / totalMin) + "%)");

        TextView selfDevelopPercent = (TextView) linearLayout.findViewById(R.id.selfDevelopPercent);
        selfDevelopPercent.setTextColor(getResources().getColor(R.color.textColor));
        selfDevelopPercent.setText(Integer.toString(selfDevelopMin / 60) + ":" + Integer.toString(selfDevelopMin % 60) + "(" + Integer.toString(selfDevelopMin * 100 / totalMin) + "%)");

        TextView exercisePercent = (TextView) linearLayout.findViewById(R.id.exercisePercent);
        exercisePercent.setTextColor(getResources().getColor(R.color.textColor));
        exercisePercent.setText(Integer.toString(exerciseMin / 60) + ":" + Integer.toString(exerciseMin % 60) + "(" + Integer.toString(exerciseMin * 100 / totalMin) + "%)");

        TextView leisurePercent = (TextView) linearLayout.findViewById(R.id.leisurePercent);
        leisurePercent.setTextColor(getResources().getColor(R.color.textColor));
        leisurePercent.setText(Integer.toString(leisureMin / 60) + ":" + Integer.toString(leisureMin % 60) + "(" + Integer.toString(leisureMin * 100 / totalMin) + "%)");

        TextView breathePercent = (TextView) linearLayout.findViewById(R.id.breathePercent);
        breathePercent.setTextColor(getResources().getColor(R.color.textColor));
        breathePercent.setText(Integer.toString(breatheMin / 60) + ":" + Integer.toString(breatheMin % 60) + "(" + Integer.toString(breatheMin * 100 / totalMin) + "%)");

        TextView todaysworkPercent = (TextView) linearLayout.findViewById(R.id.todaysworkPercent);
        todaysworkPercent.setTextColor(getResources().getColor(R.color.colorBlue));
        todaysworkPercent.setText(Integer.toString((studyMin + selfDevelopMin) / 60) + ":" + Integer.toString((studyMin + selfDevelopMin) % 60) + "(" + Integer.toString((studyMin + selfDevelopMin) * 100 / totalMin) + "%)");

        return 0;
    }
}
