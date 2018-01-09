package com.example.x240.timemanagement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {
    final int MAX_PAGE = 2;                     //ViewPaser의 총 페이지 개수
    Fragment cur_fragment = new Fragment();     //현재 ViewPaser가 가리키는 Fragment를 받을 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_feedback);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);     //ViewPaser의 선언 및 초기화
        viewPager.setAdapter(new adapter(getSupportFragmentManager()));     //선언한 ViewPaser에 adapter 연결
    }

    private class adapter extends FragmentPagerAdapter {
        public adapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            if(position < 0 || MAX_PAGE <= position)
                return null;
            switch(position) {
                case 0:
                    cur_fragment = new FeedbackPage_1();
                    break;
                case 1:
                    cur_fragment = new FeedbackPage_2();
                    break;
            }
            return cur_fragment;
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }

    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Return Button!", Toast.LENGTH_LONG).show();
        finish();
    }
}


