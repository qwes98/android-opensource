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
 * This is SettingActivity.
 */

public class SettingActivity extends AppCompatActivity {
    final int MAX_PAGE = 1;                      //The number of page on ViewPaser
    Fragment cur_fragment = new Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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
                    cur_fragment = new SettingMainActivity();
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


