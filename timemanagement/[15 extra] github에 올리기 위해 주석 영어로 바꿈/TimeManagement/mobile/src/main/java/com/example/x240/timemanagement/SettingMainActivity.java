package com.example.x240.timemanagement;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.example.x240.timemanagement.ContactDBCtrct.dateChangeHour;

/**
 * Created by One on 2017-09-04.
 * Author : Jiwon Park
 * This is a page for SettingActivity.
 */

public class SettingMainActivity extends android.support.v4.app.Fragment {
    LinearLayout linearLayout;

    ContactDBHelper dbHelper = null;
    static String YearMonthDay, MonthDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.page_setting, container, false);
        init_tables();

        getTodayDate();

        EditText showSleepingSetting = (EditText) linearLayout.findViewById(R.id.setSleepingTime);
        showSleepingSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSleepingsetMessage();
            }
        });

        return linearLayout;
    }


    private void init_tables() {
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

        if(Integer.parseInt(nowHourMinuteParts[0]) >= dateChangeHour) {
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
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // parts[1] value means Month and that value is one less than actual. So we have to subtract 1.
        cal.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String beforeDay = sdfNow2.format(cal.getTime());
        return beforeDay;
    }

    public void showSleepingsetMessage() {     // Callback function
        FragmentManager fm = getFragmentManager();
        MyDialogFragment dialogFragment = new MyDialogFragment ();
        dialogFragment.show(fm, "Sample Fragment");
    }

    public static class MyDialogFragment extends DialogFragment {
        View rootView;
        ContactDBHelper dbHelper = null;
        TextView cancel, add;

        ArrayAdapter startHourAdapter, startMinAdapter, startAmpmAdapter, endHourAdapter, endMinAdapter, endAmpmAdapter;
        Spinner startHourSpinner, startMinSpinner, startAmpmSpinner, endHourSpinner, endMinSpinner, endAmpmSpinner;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.frag_addsleepingtime, container, false);
            init_tables();

            startHourSpinner = (Spinner) rootView.findViewById(R.id.startHour);
            startHourAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.hour, android.R.layout.simple_spinner_dropdown_item);
            startHourSpinner.setAdapter(startHourAdapter);

            startMinSpinner = (Spinner) rootView.findViewById(R.id.startMin);
            startMinAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.minute, android.R.layout.simple_spinner_dropdown_item);
            startMinSpinner.setAdapter(startMinAdapter);

            startAmpmSpinner = (Spinner) rootView.findViewById(R.id.startAmpm);
            startAmpmAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.ampm, android.R.layout.simple_spinner_dropdown_item);
            startAmpmSpinner.setAdapter(startAmpmAdapter);

            endHourSpinner = (Spinner) rootView.findViewById(R.id.endHour);
            endHourAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.hour, android.R.layout.simple_spinner_dropdown_item);
            endHourSpinner.setAdapter(endHourAdapter);

            endMinSpinner = (Spinner) rootView.findViewById(R.id.endMin);
            endMinAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.minute, android.R.layout.simple_spinner_dropdown_item);
            endMinSpinner.setAdapter(endMinAdapter);

            endAmpmSpinner = (Spinner) rootView.findViewById(R.id.endAmpm);
            endAmpmAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.ampm, android.R.layout.simple_spinner_dropdown_item);
            endAmpmSpinner.setAdapter(endAmpmAdapter);

            cancel = (TextView) rootView.findViewById(R.id.cancel);
            cancel.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if(action == MotionEvent.ACTION_DOWN) {
                        getActivity().onBackPressed();              // Should change.
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
                        String expStartTime = "";
                        String expEndTime = "";
                        if(startAmpmSpinner.getSelectedItem().toString().equals("AM")) {
                            expStartTime = startHourSpinner.getSelectedItem().toString() + ":" + startMinSpinner.getSelectedItem().toString();
                        }
                        else if(startAmpmSpinner.getSelectedItem().toString().equals("PM")) {
                            expStartTime = Integer.toString(Integer.parseInt(startHourSpinner.getSelectedItem().toString()) + 12) + ":" + startMinSpinner.getSelectedItem().toString();
                        }

                        if(endAmpmSpinner.getSelectedItem().toString().equals("AM")) {
                            expEndTime = endHourSpinner.getSelectedItem().toString() + ":" + endMinSpinner.getSelectedItem().toString();
                        }
                        else if(endAmpmSpinner.getSelectedItem().toString().equals("PM")) {
                            expEndTime = Integer.toString(Integer.parseInt(endHourSpinner.getSelectedItem().toString()) + 12) + ":" + endMinSpinner.getSelectedItem().toString();
                        }

                        sleeping_save_values(expStartTime, expEndTime);

                        getActivity().onBackPressed();              // Should change.
                    }
                    return true;
                }
            });
            return rootView;
        }

        private void init_tables() {
            dbHelper = new ContactDBHelper(rootView.getContext());
        }

        private void sleeping_save_values(String actStartTime, String actEndTime) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String sqlInsert = ContactDBCtrct.SQL_INSERT +
                    " ('" +
                    YearMonthDay + "', " +
                    "'" + "Sleeping" + "', " +
                    "'" + "Sleeping" + "', " +
                    "4" + ", " +
                    "'" + "None" + "', " +
                    "'" + "None" + "', " +
                    "'" + actStartTime + "', " +
                    "'" + actEndTime + "')";

            db.execSQL(sqlInsert);
        }
    }

}
