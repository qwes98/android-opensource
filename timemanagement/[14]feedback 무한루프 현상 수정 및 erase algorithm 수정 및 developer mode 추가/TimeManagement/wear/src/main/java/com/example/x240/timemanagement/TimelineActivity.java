package com.example.x240.timemanagement;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import static com.example.x240.timemanagement.MainActivity.dataArrayList;
import static com.example.x240.timemanagement.MainActivity.dataCount;

/**
 * Created by X240 on 2017-09-07.
 */

public class TimelineActivity extends Activity {
    public static Map<String, String> tagMap = new HashMap<String, String>() {
        {
            put("Study", "#9E96FF");            //pupple
            put("Self Development", "#ADD8E6");      //skyblue
            put("Exercise", "#EC776E");         //orange
            put("Leisure", "#9ACD32");          //yellowgreen
            put("Breathe", "#FAD16A");          //yellow
            put("Napping", "#45464B");         //gray
            put("NotRecord", "#FFFFFF");        //white
            put("Sleeping", "#45464B");         //gray
        }
    };

    LinearLayout timelineLinear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        getTimeline();
    }

    public void getTimeline() {
        timelineLinear = (LinearLayout) findViewById(R.id.timelineLinear);

        for (int i = 0; i < dataCount; i++) {
            /*
            if (MainActivity.checkBoxArray.get(i) == 4 || MainActivity.checkBoxArray.get(i) == 5) {       //etc 일, 추가된 일 제외
                continue;
            }
            */

            if(MainActivity.checkBoxArray.get(i) == 0 || MainActivity.checkBoxArray.get(i) == 1 || MainActivity.checkBoxArray.get(i) == 2) {    //etc 일, 추가된 일 제외
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        70));

                TextView time = new TextView(getApplicationContext());
                time.setLayoutParams(new ViewGroup.LayoutParams(160, 70));
                time.setText(dataArrayList.get(i).get(3) + " ~ " + dataArrayList.get(i).get(4));
                time.setTextSize(14);
                time.setTextColor(Color.parseColor("#787878"));
                time.setPadding(5, 6, 10, 6);
                //time.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                TextView name = new TextView(getApplicationContext());
                name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 70));
                name.setText(dataArrayList.get(i).get(2));
                name.setTextSize(14);
                name.setTextColor(Color.parseColor(tagMap.get(dataArrayList.get(i).get(1))));
                name.setPadding(10, 0, 0, 0);
                name.setGravity(Gravity.CENTER_VERTICAL);

                linearLayout.addView(time);
                linearLayout.addView(name);

                timelineLinear.addView(linearLayout);
            }
        }
    }
}
