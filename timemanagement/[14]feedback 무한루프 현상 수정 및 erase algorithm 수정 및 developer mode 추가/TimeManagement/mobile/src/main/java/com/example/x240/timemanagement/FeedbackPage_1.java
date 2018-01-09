package com.example.x240.timemanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by X240 on 2017-09-01.
 */

public class FeedbackPage_1 extends android.support.v4.app.Fragment {
    LinearLayout linearLayout;

    ContactDBHelper dbHelper = null;
    static String YearMonthDay, MonthDay;
    TextView leftArrow, rightArrow;
    TextView date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.page_feedback_1, container, false);
        init_tables();

        date = (TextView) linearLayout.findViewById(R.id.date);
        getTodayDate();
        date.setText(MonthDay);

        if(checkExistSleepingTime()) {
            getStatistics();
        }
        else {
            Toast.makeText(getContext(), "You should setting your sleeping time!", Toast.LENGTH_LONG).show();
        }

        leftArrow = (TextView) linearLayout.findViewById(R.id.before);
        leftArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    String[] parts = YearMonthDay.split("-");
                    Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // 두번째 인자는 월, 1월이 0, 2월이 1... 이렇게 나가기 때문에 -1을 해주어야 함
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
                    YearMonthDay = sdfNow.format(cal.getTime());
                    SimpleDateFormat sdfNow2 = new SimpleDateFormat("MM-dd");
                    MonthDay = sdfNow2.format(cal.getTime());

                    date.setText(MonthDay);
                    if(checkExistSleepingTime()) {
                        getStatistics();
                    }
                    else {
                        clearStatistics();
                        Toast.makeText(getContext(), "There is no data!", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        rightArrow = (TextView) linearLayout.findViewById(R.id.after);
        rightArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    String[] parts = YearMonthDay.split("-");
                    Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // 두번째 인자는 월, 1월이 0, 2월이 1... 이렇게 나가기 때문에 -1을 해주어야 함
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
                    YearMonthDay = sdfNow.format(cal.getTime());
                    SimpleDateFormat sdfNow2 = new SimpleDateFormat("MM-dd");
                    MonthDay = sdfNow2.format(cal.getTime());

                    date.setText(MonthDay);
                    if(checkExistSleepingTime()) {
                        getStatistics();
                    }
                    else {
                        clearStatistics();
                        Toast.makeText(getContext(), "There is no data!", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        //LinearLayout background = (LinearLayout) linearLayout.findViewById(R.id.background);
        //TextView page_num = (TextView) linearLayout.findViewById(R.id.page_num);
        //page_num.setText(String.valueOf(1));
        //background.setBackground(new ColorDrawable(0xff6dc6d2));

        return linearLayout;
    }


    private void init_tables() {
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }

    private void getTodayDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String YMDHM = sdfNow.format(date);
        String[] tmpParts = YMDHM.split("\\s");
        String[] nowHourMinuteParts = tmpParts[1].split(":");

        if(Integer.parseInt(nowHourMinuteParts[0]) >= 4) {          //후에 설정을 통해 바꿀수 있도록...
            YearMonthDay = tmpParts[0];
        }
        else {
            YearMonthDay = getBeforeTheDay(tmpParts[0]);
        }
        //Toast.makeText(getApplicationContext(), YearMonthDay, Toast.LENGTH_LONG).show();
        String[] ymdParts = YearMonthDay.split("-");
        MonthDay = ymdParts[1] + "-" + ymdParts[2];
    }

    private String getBeforeTheDay(String yearMonthDay) {
        String[] parts = yearMonthDay.split("-");
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // 두번째 인자는 월, 1월이 0, 2월이 1... 이렇게 나가기 때문에 -1을 해주어야 함
        cal.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String beforeDay = sdfNow2.format(cal.getTime());
        return beforeDay;
    }

    private String getAfterTheDay(String yearMonthDay) {
        String[] parts = yearMonthDay.split("-");
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // 두번째 인자는 월, 1월이 0, 2월이 1... 이렇게 나가기 때문에 -1을 해주어야 함
        cal.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String beforeDay = sdfNow2.format(cal.getTime());
        return beforeDay;
    }

    private boolean checkExistSleepingTime() {
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

    private void getStatistics() {
        //load_values 함수 제거
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + YearMonthDay + "'", null);

        View listView = linearLayout.findViewById(R.id.todoLinear);

        // x -> 0, triangle -> 0.5, check -> 1
        double sumOfTodoCheck = 0.0;
        int todoCount = 0;

        Date sleepingEndDate = new Date();

        //각 tag 시간 (end sleeping ~ now)
        int studyMin = 0;
        int selfDevelopMin = 0;
        int exerciseMin = 0;
        int leisureMin = 0;
        int breatheMin = 0;
        int napMin = 0;
        int notRecordMin;



        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {

                if(cursor.getInt(3) == 0 || cursor.getInt(3) == 1 || cursor.getInt(3) == 2 || cursor.getInt(3) == 4 || cursor.getInt(3) == 5) {     //memo 제외
                    // 1. sleepTime을 위한 값 추출
                    // 이 부분 알고리즘에서 문제 생김
                    if (cursor.getString(1).equals("Sleeping")) {
                        Date startDate = new Date();
                        Date endDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        String startHourMin = cursor.getString(6);
                        String endHourMin = cursor.getString(7);
                        String[] tmpParts = startHourMin.split(":");
                        try {
                            if (Integer.parseInt(tmpParts[0]) > 4) {          //후에 설정을 통해 바꿀수 있도록...
                                startDate = dateFormat.parse(getBeforeTheDay(cursor.getString(0)) + " " + startHourMin);
                            } else {
                                startDate = dateFormat.parse(cursor.getString(0) + " " + startHourMin);
                            }

                            endDate = dateFormat.parse(cursor.getString(0) + " " + endHourMin);
                            sleepingEndDate = endDate;                                             //뒤에서 TagTime 구할때 사용하기 위함
                        } catch (ParseException e) {
                            Toast.makeText(getContext(), "Parse exception in first!", Toast.LENGTH_SHORT).show();
                        }

                        int minute = (int) ((endDate.getTime() - startDate.getTime()) / 60000);
                        int sleepHour = minute / 60;
                        int sleepMinute = minute % 60;

                        TextView sleepTime = (TextView) linearLayout.findViewById(R.id.sleepTime);
                        sleepTime.setTextColor(getResources().getColor(R.color.colorBlue));
                        sleepTime.setText("Sleeptime : " + startHourMin + "~" + endHourMin);

                        TextView duration = (TextView) linearLayout.findViewById(R.id.duration);
                        duration.setTextColor(getResources().getColor(R.color.colorBlue));
                        duration.setText("Duration : " + Integer.toString(sleepHour) + ":" + Integer.toString(sleepMinute));

                        cursor.moveToNext();
                        continue;
                    }

                    // 2. todo 달성률을 위한 값 추출
                    if (cursor.getInt(3) == 0) {
                        sumOfTodoCheck += 0;
                        todoCount++;
                    } else if (cursor.getInt(3) == 1) {
                        sumOfTodoCheck += 0.5;
                        todoCount++;
                    } else if (cursor.getInt(3) == 2) {
                        sumOfTodoCheck += 1.0;
                        todoCount++;
                    }

                    // 3. Tag별 비율을 위한 값 추출
                    if (cursor.getInt(3) != 0) {
                        //알고리즘은 위에 1.sleepTime구하는 알고리즘과 동일. try 구문 안에 날짜 변경해주는 알고리즘만 다름
                        Date startDate = new Date();
                        Date endDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        String startHourMin = cursor.getString(6);
                        String endHourMin = cursor.getString(7);
                        String[] tmpStartParts = startHourMin.split(":");
                        String[] tmpEndParts = endHourMin.split(":");
                        try {
                            if (Integer.parseInt(tmpStartParts[0]) > 4) {          //후에 설정을 통해 바꿀수 있도록...
                                startDate = dateFormat.parse(cursor.getString(0) + " " + startHourMin);
                            } else {
                                startDate = dateFormat.parse(getAfterTheDay(cursor.getString(0)) + " " + startHourMin);
                            }

                            if (Integer.parseInt(tmpEndParts[0]) > 4) {
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

        // 2. todo 달성률을 위한 값 추출
        int achievement = (int) (sumOfTodoCheck * 100 / todoCount);
        TextView achievePercent = (TextView) linearLayout.findViewById(R.id.achievePercent);
        achievePercent.setTextColor(getResources().getColor(R.color.colorBlue));
        achievePercent.setText("Percent of achievement : " + Integer.toString(achievement) + "%");


        // 3. Tag별 비율을 위한 값 추출
        long now = System.currentTimeMillis();
        Date nowDate = new Date(now);

        int todayMin = (int) ((nowDate.getTime() - sleepingEndDate.getTime()) / 60000);
        notRecordMin = todayMin - studyMin - selfDevelopMin - exerciseMin - leisureMin - breatheMin - napMin;


        TextView studyPercent = (TextView) linearLayout.findViewById(R.id.studyPercent);
        studyPercent.setTextColor(Color.parseColor("#FFFFFF"));
        studyPercent.setText(Integer.toString(studyMin / 60) + ":" + Integer.toString(studyMin % 60) + "(" + Integer.toString(studyMin * 100 / todayMin) + "%)");

        TextView selfDevelopPercent = (TextView) linearLayout.findViewById(R.id.selfDevelopPercent);
        selfDevelopPercent.setTextColor(Color.parseColor("#FFFFFF"));
        selfDevelopPercent.setText(Integer.toString(selfDevelopMin / 60) + ":" + Integer.toString(selfDevelopMin % 60) + "(" + Integer.toString(selfDevelopMin * 100 / todayMin) + "%)");

        TextView exercisePercent = (TextView) linearLayout.findViewById(R.id.exercisePercent);
        exercisePercent.setTextColor(Color.parseColor("#FFFFFF"));
        exercisePercent.setText(Integer.toString(exerciseMin / 60) + ":" + Integer.toString(exerciseMin % 60) + "(" + Integer.toString(exerciseMin * 100 / todayMin) + "%)");

        TextView leisurePercent = (TextView) linearLayout.findViewById(R.id.leisurePercent);
        leisurePercent.setTextColor(Color.parseColor("#FFFFFF"));
        leisurePercent.setText(Integer.toString(leisureMin / 60) + ":" + Integer.toString(leisureMin % 60) + "(" + Integer.toString(leisureMin * 100 / todayMin) + "%)");

        TextView breathePercent = (TextView) linearLayout.findViewById(R.id.breathePercent);
        breathePercent.setTextColor(Color.parseColor("#FFFFFF"));
        breathePercent.setText(Integer.toString(breatheMin / 60) + ":" + Integer.toString(breatheMin % 60) + "(" + Integer.toString(breatheMin * 100 / todayMin) + "%)");

        TextView nappingPercent = (TextView) linearLayout.findViewById(R.id.napPercent);
        nappingPercent.setTextColor(Color.parseColor("#FFFFFF"));
        nappingPercent.setText(Integer.toString(napMin / 60) + ":" + Integer.toString(napMin % 60) + "(" + Integer.toString(napMin * 100 / todayMin) + "%)");

        TextView notRecordPercent = (TextView) linearLayout.findViewById(R.id.notRecordPercent);
        notRecordPercent.setTextColor(Color.parseColor("#FFFFFF"));
        notRecordPercent.setText(Integer.toString(notRecordMin / 60) + ":" + Integer.toString(notRecordMin % 60) + "(" + Integer.toString(notRecordMin * 100 / todayMin) + "%)");

    }

    public void clearStatistics() {
        TextView sleepTime = (TextView) linearLayout.findViewById(R.id.sleepTime);
        sleepTime.setTextColor(getResources().getColor(R.color.colorBlue));
        sleepTime.setText("Sleeptime : 00:00~00:00");

        TextView duration = (TextView) linearLayout.findViewById(R.id.duration);
        duration.setTextColor(getResources().getColor(R.color.colorBlue));
        duration.setText("Duration : 00:00");

        TextView achievePercent = (TextView) linearLayout.findViewById(R.id.achievePercent);
        achievePercent.setTextColor(getResources().getColor(R.color.colorBlue));
        achievePercent.setText("Percent of achievement : 0%");

        TextView studyPercent = (TextView) linearLayout.findViewById(R.id.studyPercent);
        studyPercent.setTextColor(Color.parseColor("#FFFFFF"));
        studyPercent.setText("0:0(0%)");

        TextView selfDevelopPercent = (TextView) linearLayout.findViewById(R.id.selfDevelopPercent);
        selfDevelopPercent.setTextColor(Color.parseColor("#FFFFFF"));
        selfDevelopPercent.setText("0:0(0%)");

        TextView exercisePercent = (TextView) linearLayout.findViewById(R.id.exercisePercent);
        exercisePercent.setTextColor(Color.parseColor("#FFFFFF"));
        exercisePercent.setText("0:0(0%)");

        TextView leisurePercent = (TextView) linearLayout.findViewById(R.id.leisurePercent);
        leisurePercent.setTextColor(Color.parseColor("#FFFFFF"));
        leisurePercent.setText("0:0(0%)");

        TextView breathePercent = (TextView) linearLayout.findViewById(R.id.breathePercent);
        breathePercent.setTextColor(Color.parseColor("#FFFFFF"));
        breathePercent.setText("0:0(0%)");

        TextView nappingPercent = (TextView) linearLayout.findViewById(R.id.napPercent);
        nappingPercent.setTextColor(Color.parseColor("#FFFFFF"));
        nappingPercent.setText("0:0(0%)");

        TextView notRecordPercent = (TextView) linearLayout.findViewById(R.id.notRecordPercent);
        notRecordPercent.setTextColor(Color.parseColor("#FFFFFF"));
        notRecordPercent.setText("0:0(0%)");
    }

}

