package com.example.x240.timemanagement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by One on 2017-09-04.
 * Author : Jiwon Park
 * This is activity for developer.
 * You can add or erase database data.
 * If you click logo 6 times located on MainActivity, you can enter this activity.
 */

public class DeveloperActivity extends AppCompatActivity {
    final int MAX_PAGE = 1;                     //The number of page on ViewPaser
    Fragment cur_fragment = new Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_developer);

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
                    cur_fragment = new DeveloperPage_1();
                    break;
            }
            return cur_fragment;
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }

    /*
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Return Button!", Toast.LENGTH_LONG).show();
        finish();
    }
    */
}


