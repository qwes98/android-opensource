package com.example.x240.timemanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by X240 on 2017-09-01.
 */

public class FeedbackPage_1 extends android.support.v4.app.Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.page_feedback_1, container, false);

        //LinearLayout background = (LinearLayout) linearLayout.findViewById(R.id.background);
        //TextView page_num = (TextView) linearLayout.findViewById(R.id.page_num);
        //page_num.setText(String.valueOf(1));
        //background.setBackground(new ColorDrawable(0xff6dc6d2));

        return linearLayout;
    }
}
