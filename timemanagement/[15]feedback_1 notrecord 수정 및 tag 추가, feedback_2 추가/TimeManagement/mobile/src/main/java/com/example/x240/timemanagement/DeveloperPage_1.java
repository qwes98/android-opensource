package com.example.x240.timemanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DeveloperPage_1 extends android.support.v4.app.Fragment {
    LinearLayout linearLayout;
    //SQLiteDatabase db;
    ContactDBHelper dbHelper = null;
    static String YearMonthDay, MonthDay;          //YearMonthDay는 DataBase로부터 데이터를 가져올때 사용할 key값, MonthDay는 화면에 Display할값
    TextView leftArrow, rightArrow;
    TextView date;

    static String eraseTextLine;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.page_developer_1, container, false);

        getTodayDate();

        init_tables();

        date = (TextView) linearLayout.findViewById(R.id.date);

        date.setText(MonthDay);
        getDatabaseTable();

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
                    getDatabaseTable();
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
                    getDatabaseTable();
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

    private void init_tables() {                //mainActivity에 있는 함수를 그대로 가져옴
        dbHelper = new ContactDBHelper(linearLayout.getContext());
    }

    private void getTodayDate() {
        //현재실제시간
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

    private void getDatabaseTable() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + YearMonthDay + "'", null);

        View listView = linearLayout.findViewById(R.id.tableLinear);
        ((LinearLayout) listView).removeAllViews();

        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                RelativeLayout tableLine = new RelativeLayout(getActivity());              //동적으로 tableLine list를 만듬

                String tableStr = cursor.getString(1) + ", " + cursor.getString(2) + ", " + Integer.toString(cursor.getInt(3)) + ", " + cursor.getString(4) + "~" + cursor.getString(5) + ", " + cursor.getString(6) + "~" + cursor.getString(7);

                TextView textLine = new TextView(getActivity());
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(1200, 80);
                layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                textLine.setLayoutParams(layoutParams1);
                textLine.setGravity(Gravity.CENTER_VERTICAL);
                textLine.setTextSize(13);
                textLine.setText(tableStr);
                textLine.setTextColor(Color.parseColor("#FFFFFF"));
                tableLine.addView(textLine);

                tableLine.setPadding(60, 3, 0, 3);

                textLine.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int action = motionEvent.getAction();

                        float beforeX = (float) 0.0;
                        float afterX = (float) 0.0;
                        if (action == motionEvent.ACTION_DOWN) {
                            beforeX = motionEvent.getX();
                        } else if (action == motionEvent.ACTION_UP) {
                            afterX = motionEvent.getX();
                        }

                        if (afterX - beforeX > 50) {
                            eraseTextLine = (String) ((TextView) view).getText();
                            showEraseConfirmMessage();
                        }
                        return true;
                    }
                });

                ((LinearLayout) listView).addView(tableLine);

                cursor.moveToNext();
            }
        }
    }

    private void showEraseConfirmMessage() {
        FragmentManager fm = getFragmentManager();
        MyEraseConfirmFragment confirmFragment = new MyEraseConfirmFragment ();
        confirmFragment.show(fm, "Sample Fragment2");
    }

    public static class MyEraseConfirmFragment extends DialogFragment {
        View rootView;
        ContactDBHelper dbHelper = null;
        TextView cancel, erase;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.frag_eraseconfirm, container, false);
            init_tables();

            cancel = (TextView) rootView.findViewById(R.id.cancel);
            cancel.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if(action == MotionEvent.ACTION_DOWN) {

                        Toast.makeText(getContext(), "Cancel!", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();              //수정해야함
                    }
                    return true;
                }
            });


            erase = (TextView) rootView.findViewById(R.id.erase);
            erase.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if(action == MotionEvent.ACTION_DOWN) {
                        String[] tmpArray = eraseTextLine.split(", "); // index 0 : tagname, 1 : todo, 2 : todocheck, 3 : exptime, 4 : acttime

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("DELETE FROM TodoDatabase WHERE DATE = ? AND TODO = ? AND EXPSTARTTIME = ? AND EXPENDTIME = ? AND ACTSTARTTIME = ? AND ACTENDTIME = ?", new String[] { YearMonthDay, tmpArray[1], tmpArray[3].split("~")[0], tmpArray[3].split("~")[1], tmpArray[4].split("~")[0], tmpArray[4].split("~")[1]});

                        Toast.makeText(getContext(), "Erase!", Toast.LENGTH_SHORT).show();
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
        EditText editDate, editTodo, editTodoCheck, editExpTime, editActTime;
        TextView cancel, add;

        ArrayAdapter tagAdapter;
        Spinner tagSpinner;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.frag_developer_addtodo, container, false);
            init_tables();

            editDate = (EditText) rootView.findViewById(R.id.date);
            editTodo = (EditText) rootView.findViewById(R.id.todo);
            editTodoCheck = (EditText) rootView.findViewById(R.id.todoCheck);
            editExpTime = (EditText) rootView.findViewById(R.id.expTime);
            editActTime = (EditText) rootView.findViewById(R.id.actTime);

            tagSpinner = (Spinner) rootView.findViewById(R.id.tag);
            tagAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.developerTag, android.R.layout.simple_spinner_dropdown_item);
            tagSpinner.setAdapter(tagAdapter);

            cancel = (TextView) rootView.findViewById(R.id.cancel);
            cancel.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if(action == MotionEvent.ACTION_DOWN) {
                        Toast.makeText(getContext(), "Cancel!", Toast.LENGTH_SHORT).show();
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
                        String date = editDate.getText().toString();
                        String todo = editTodo.getText().toString();
                        String tag = tagSpinner.getSelectedItem().toString();
                        String todoCheck = editTodoCheck.getText().toString();
                        String expStartTime = editExpTime.getText().toString().split("~")[0];
                        String expEndTime = editExpTime.getText().toString().split("~")[1];
                        String actStartTime = editActTime.getText().toString().split("~")[0];
                        String actEndTime = editActTime.getText().toString().split("~")[1];

                        save_values(date, tag, todo, todoCheck, expStartTime, expEndTime, actStartTime, actEndTime);

                        Toast.makeText(getContext(), "Add todo!", Toast.LENGTH_SHORT).show();
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

        private void save_values(String date, String tagName, String todo, String todoCheck, String expStartTime, String expEndTime, String actStartTime, String actEndTime) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String sqlInsert = ContactDBCtrct.SQL_INSERT +
                    " ('" +
                    date + "', " +
                    "'" + tagName + "', " +
                    "'" + todo + "', " +
                    todoCheck + ", " +
                    "'" + expStartTime + "', " +
                    "'" + expEndTime + "', " +
                    "'" + actStartTime + "', " +
                    "'" + actEndTime + "')";

            db.execSQL(sqlInsert);
        }

    }
}
