package com.example.x240.timemanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by X240 on 2017-09-01.
 */

public class SchedulePage_1 extends android.support.v4.app.Fragment {
    LinearLayout linearLayout;
    //SQLiteDatabase db;
    ContactDBHelper dbHelper = null;
    static String YearMonthDay, MonthDay;          //YearMonthDay는 DataBase로부터 데이터를 가져올때 사용할 key값, MonthDay는 화면에 Display할값
    TextView leftArrow, rightArrow;
    TextView date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.page_schedule_1, container, false);
        init_tables();

        date = (TextView) linearLayout.findViewById(R.id.date);
        getTodayDate();
        date.setText(MonthDay);
        getTodoList();

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
                    getTodoList();
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
                    getTodoList();
                }
                return true;
            }
        });

        Button addButton = (Button) linearLayout.findViewById(R.id.button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMessage();
            }
        });

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

    private void getTodoList() {
        //load_values 함수 제거
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + YearMonthDay + "'", null);

        View listView = linearLayout.findViewById(R.id.todoLinear);
        ((LinearLayout) listView).removeAllViews();

        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                RelativeLayout todo = new RelativeLayout(getActivity());              //동적으로 todo list를 만듬

                String name = cursor.getString(2);      //TODO

                ImageView imageView = new ImageView(getActivity());
                int checkBox = cursor.getInt(3);          //TODOCHECK
                if(checkBox == 10) {        //etc 일에 대한 처리
                    cursor.moveToNext();
                    continue;
                }
                switch (checkBox) {
                    case 0:
                        if(cursor.getString(1).equals("Study")) { imageView.setImageResource(R.drawable.ic_notstart_study); }
                        else if(cursor.getString(1).equals("Self Development")) { imageView.setImageResource(R.drawable.ic_notstart_selfdevelop); }
                        else if(cursor.getString(1).equals("Leisure")) { imageView.setImageResource(R.drawable.ic_notstart_leisure); }
                        else if(cursor.getString(1).equals("Exercise")) { imageView.setImageResource(R.drawable.ic_notstart_exercise); }
                        break;
                    case 1:
                        if(cursor.getString(1).equals("Study")) { imageView.setImageResource(R.drawable.ic_doing_study); }
                        else if(cursor.getString(1).equals("Self Development")) { imageView.setImageResource(R.drawable.ic_doing_selfdevelop); }
                        else if(cursor.getString(1).equals("Leisure")) { imageView.setImageResource(R.drawable.ic_doing_leisure); }
                        else if(cursor.getString(1).equals("Exercise")) { imageView.setImageResource(R.drawable.ic_doing_exercise); }
                        break;
                    case 2:
                        if(cursor.getString(1).equals("Study")) { imageView.setImageResource(R.drawable.ic_done_study); }
                        else if(cursor.getString(1).equals("Self Development")) { imageView.setImageResource(R.drawable.ic_done_selfdevelop); }
                        else if(cursor.getString(1).equals("Leisure")) { imageView.setImageResource(R.drawable.ic_done_leisure); }
                        else if(cursor.getString(1).equals("Exercise")) { imageView.setImageResource(R.drawable.ic_done_exercise); }
                        break;
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

                cursor.moveToNext();
            }
        }
    }

    private void init_tables() {
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }

    private void showAddMessage() {
        FragmentManager fm = getFragmentManager();
        MyDialogFragment dialogFragment = new MyDialogFragment ();
        dialogFragment.show(fm, "Sample Fragment");
    }

    public static class MyDialogFragment extends DialogFragment {
        //SQLiteOpenHelper helper;
        //SQLiteDatabase db;
        View rootView;
        ContactDBHelper dbHelper = null;
        EditText editTodo, editStartTime, editEndTime;
        TextView cancel, add;

        ArrayAdapter tagAdapter;
        Spinner tagSpinner;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.frag_addtodo, container, false);
            init_tables();

            editTodo = (EditText) rootView.findViewById(R.id.todo);
            editStartTime = (EditText) rootView.findViewById(R.id.startTime);
            editEndTime = (EditText) rootView.findViewById(R.id.endTime);

            tagSpinner = (Spinner) rootView.findViewById(R.id.tag);
            tagAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.tag, android.R.layout.simple_spinner_dropdown_item);
            tagSpinner.setAdapter(tagAdapter);

            cancel = (TextView) rootView.findViewById(R.id.cancel);
            cancel.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if(action == MotionEvent.ACTION_DOWN) {
                        getActivity().onBackPressed();              //수정해야함
                    }
                    return true;
                }
            });


            add = (TextView) rootView.findViewById(R.id.add);
            add.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if(action == MotionEvent.ACTION_DOWN) {
                        String todo = editTodo.getText().toString();
                        String expStartTime = editStartTime.getText().toString();
                        String expEndTime = editEndTime.getText().toString();
                        String tag = tagSpinner.getSelectedItem().toString();

                        save_values(todo, expStartTime, expEndTime, tag);

                        getActivity().onBackPressed();              //수정해야함
                    }
                    return true;
                }
            });
            return rootView;
        }

        private void init_tables() {
            dbHelper = new ContactDBHelper(rootView.getContext());
        }

        private void save_values(String todo, String expStartTime, String expEndTime, String tag) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String sqlInsert = ContactDBCtrct.SQL_INSERT +
                    " ('" +
                    SchedulePage_1.YearMonthDay + "', " +
                    "'" + tag + "', " +
                    "'" + todo + "', " +
                    "0" + ", " +
                    "'" + expStartTime + "', " +
                    "'" + expEndTime + "', " +
                    "'" + "None" + "', " +
                    "'" + "None" + "')";

            db.execSQL(sqlInsert);
        }
    }


}
