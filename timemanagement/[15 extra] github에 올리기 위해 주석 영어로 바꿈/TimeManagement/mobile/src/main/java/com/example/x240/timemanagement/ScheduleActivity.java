package com.example.x240.timemanagement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by One on 2017-09-01.
 * Author : Jiwon Park
 * This is ScheduleActivity. you can check your todo list and timeline.
 */

public class ScheduleActivity extends AppCompatActivity {
    final int MAX_PAGE = 3;                     //The number of page on ViewPaser
    Fragment cur_fragment = new Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new adapter(getSupportFragmentManager()));
    }

    private class adapter extends FragmentPagerAdapter {
        public adapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            if(position < 0 || MAX_PAGE <= position)
                return null;
            switch(position) {
                case 0:
                    cur_fragment = new SchedulePage_1();
                    break;
                case 1:
                    cur_fragment = new SchedulePage_2();
                    break;
                case 2:
                    cur_fragment = new SchedulePage_3();
                    break;
            }
            return cur_fragment;
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }
}


