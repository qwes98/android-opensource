package com.example.x240.timemanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        //getStatistics();

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
                    getStatistics();
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
                    getStatistics();
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

    private void getTodayDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        YearMonthDay = sdfNow.format(date);
        sdfNow = new SimpleDateFormat("MM-dd");
        MonthDay = sdfNow.format(date);
    }

    private void getStatistics() {
        //load_values 함수 제거
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + YearMonthDay + "'", null);

        View listView = linearLayout.findViewById(R.id.todoLinear);
        ((LinearLayout) listView).removeAllViews();

        // sleeping시간 -> sleeping tag 누른 후부터 getup tag 누를때까지
        int sleepingTime = 0;

        // x -> 0, triangle -> 0.5, check -> 1
        double sumOfTodoCheck = 0.0;

        //각 tag 시간 (getup ~ sleeping)
        int studyTime = 0;
        int selfDevelopTime = 0;
        int exerciseTime = 0;
        int leisureTime = 0;
        int breatheTime = 0;
        int napTime = 0;
        int StudyTime = 0;

        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                // 1. sleepTime을 위한 값 추출
                String name = cursor.getString(2);      //TODO

                // 2. todo 달성률을 위한 값 추출
                int checkBox = cursor.getInt(3);          //TODOCHECK

                // 3. Tag별 비율을 위한 값 추출


                /*
                ImageView imageView = new ImageView(getActivity());
                switch (checkBox) {
                    case 0:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.blank_box));
                        break;
                    case 1:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.x));
                        break;
                    case 2:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.triangle));
                        break;
                    default:
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.check));
                }

                String expStartTime = cursor.getString(4);
                String expEndTime = cursor.getString(5);
                String expTime = expStartTime + " ~ " + expEndTime;

                imageView.setId(R.id.one);              //인자로 int값을 바로 넣으면 인식이 안되서 id.xml파일을 만들어서 사용함
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(140, 140);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                imageView.setLayoutParams(layoutParams);
                imageView.setPadding(25, 25, 50, 20);
                todo.addView(imageView);

                TextView todoName = new TextView(getActivity());
                todoName.setId(R.id.two);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(600, 130);
                layoutParams1.addRule(RelativeLayout.RIGHT_OF, R.id.one);
                todoName.setLayoutParams(layoutParams1);
                todoName.setGravity(Gravity.CENTER_VERTICAL);
                todoName.setTextSize(20);
                todoName.setText(name);
                todo.addView(todoName);

                TextView time = new TextView(getActivity());
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 130);
                layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                time.setLayoutParams(layoutParams2);
                time.setGravity(Gravity.CENTER_VERTICAL);
                time.setTextSize(12);
                time.setText(expTime);
                todo.addView(time);

                todo.setPadding(35, 3, 60, 3);


                ((LinearLayout) listView).addView(todo);
                */

                cursor.moveToNext();
            }
        }
    }

    private void init_tables() {
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }
}
