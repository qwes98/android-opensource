package com.example.x240.timemanagement;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.x240.timemanagement.MainActivity.checkBoxArray;
import static com.example.x240.timemanagement.MainActivity.dataArrayList;
import static com.example.x240.timemanagement.MainActivity.dataCount;

/**
 * Created by X240 on 2017-09-07.
 */

public class TodolistActivity extends Activity{
    SharedPreferences wearDB;
    SharedPreferences.Editor wearDBeditor;

    LinearLayout studyLinear, selfDevelopLinear, leisureLinear, exerciseLinear;         //실제 todo리스트를 이 layout에 추가시키면 됨
    ImageView calcelButton, okButton;

    //밑의 두 레이아웃은 프레임레이아웃으로 이루어져 있어서 화면전환이 됨
    LinearLayout checkOkLayout;
    ScrollView mainLayout;

    int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        wearDB = getSharedPreferences("wearDB", MODE_PRIVATE);
        wearDBeditor = wearDB.edit();

        calcelButton = (ImageView) findViewById(R.id.cancelButton);
        okButton = (ImageView) findViewById(R.id.okButton);

        checkOkLayout = (LinearLayout) findViewById(R.id.checkOkLayout);
        mainLayout = (ScrollView) findViewById(R.id.mainLayout);

        //우리가 어떤 일을 끝내면 무조건 이 액티비티로 들어오게 되어있다.
        //실수나 고의로 새 일을 선택하지 않고 나갔을 때 생기는 문제를 방지하기위해 workName과 workStartTime을 초기화한다.
        wearDBeditor.putString("workName", "Nothing");
        wearDBeditor.putString("workStartTime", MainActivity.HourMin);
        wearDBeditor.putInt("beforeCheckBox", 0);
        wearDBeditor.apply();

        getTodolist();

    }

    private void getTodolist() {
        studyLinear = (LinearLayout) findViewById(R.id.StudyLinear);
        selfDevelopLinear = (LinearLayout) findViewById(R.id.SelfDevelopLinear);
        leisureLinear = (LinearLayout) findViewById(R.id.LeisureLinear);
        exerciseLinear = (LinearLayout) findViewById(R.id.ExerciseLinear);

        Toast.makeText(getApplicationContext(), "datacount" + Integer.toString(dataCount), Toast.LENGTH_SHORT).show();
        //todo LinearLayout에 추가
        for(i = 0; i < dataCount; i++) {
            if(MainActivity.checkBoxArray.get(i) == 4 || MainActivity.checkBoxArray.get(i) == 5) {        //etc 일에 대한 처리
                continue;
            }
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            ImageView tagImage = new ImageView(this);
            switch (MainActivity.checkBoxArray.get(i)) {
                case 0:
                    if (dataArrayList.get(i).get(1).equals("Study")) {
                        tagImage.setImageResource(R.drawable.ic_notstart_study);
                    } else if (dataArrayList.get(i).get(1).equals("Self Development")) {
                        tagImage.setImageResource(R.drawable.ic_notstart_selfdevelop);
                    } else if (dataArrayList.get(i).get(1).equals("Leisure")) {
                        tagImage.setImageResource(R.drawable.ic_notstart_leisure);
                    } else if (dataArrayList.get(i).get(1).equals("Exercise")) {
                        tagImage.setImageResource(R.drawable.ic_notstart_exercise);
                    }
                    break;
                case 1:
                    if (dataArrayList.get(i).get(1).equals("Study")) {
                        tagImage.setImageResource(R.drawable.ic_doing_study);
                    } else if (dataArrayList.get(i).get(1).equals("Self Development")) {
                        tagImage.setImageResource(R.drawable.ic_doing_selfdevelop);
                    } else if (dataArrayList.get(i).get(1).equals("Leisure")) {
                        tagImage.setImageResource(R.drawable.ic_doing_leisure);
                    } else if (dataArrayList.get(i).get(1).equals("Exercise")) {
                        tagImage.setImageResource(R.drawable.ic_doing_exercise);
                    }
                    break;
                case 2:
                    if (dataArrayList.get(i).get(1).equals("Study")) {
                        tagImage.setImageResource(R.drawable.ic_done_study);
                    } else if (dataArrayList.get(i).get(1).equals("Self Development")) {
                        tagImage.setImageResource(R.drawable.ic_done_selfdevelop);
                    } else if (dataArrayList.get(i).get(1).equals("Leisure")) {
                        tagImage.setImageResource(R.drawable.ic_done_leisure);
                    } else if (dataArrayList.get(i).get(1).equals("Exercise")) {
                        tagImage.setImageResource(R.drawable.ic_done_exercise);
                    }
                    break;
            }

            tagImage.setPadding(19, 5, 0, 0);
            tagImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));

            TextView todoText = new TextView(this);
            todoText.setId(Integer.parseInt(Integer.toString(i+1) + Integer.toString(checkBoxArray.get(i))));       //checkBox를 보관하기 위함, 만약 checkBox가 1이고 i가 10이면 id값은 110로 우리가 사용할때에는 idx 0만 사용!, 여기서 나올수 있는 checkBox는 0,1,2 뿐임
            todoText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70));
            todoText.setText(dataArrayList.get(i).get(2));
            todoText.setTextSize(17);
            todoText.setPadding(0, 6, 0, 0);
            todoText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

            todoText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        final String viewText =  (String) ((TextView)view).getText();
                        TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
                        checkSignText.setText(viewText + ", right?");

                        String id = Integer.toString(view.getId());
                        final int checkBox = Character.getNumericValue(id.charAt(id.length() - 1));

                        mainLayout.setVisibility(View.INVISIBLE);
                        checkOkLayout.setVisibility(View.VISIBLE);


                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                wearDBeditor.putString("workStartTime", MainActivity.HourMin);
                                wearDBeditor.putString("workName", viewText);
                                wearDBeditor.putInt("beforeCheckBox", checkBox);
                                wearDBeditor.apply();


                                mainLayout.setVisibility(View.VISIBLE);
                                checkOkLayout.setVisibility(View.INVISIBLE);
                                finish();
                            }
                        });

                        calcelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mainLayout.setVisibility(View.VISIBLE);
                                checkOkLayout.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });

            linearLayout.addView(tagImage);
            linearLayout.addView(todoText);

            if (dataArrayList.get(i).get(1).equals("Study")) {
                studyLinear.addView(linearLayout);
            } else if (dataArrayList.get(i).get(1).equals("Self Development")) {
                selfDevelopLinear.addView(linearLayout);
            } else if (dataArrayList.get(i).get(1).equals("Leisure")) {
                leisureLinear.addView(linearLayout);
            } else if (dataArrayList.get(i).get(1).equals("Exercise")) {
                exerciseLinear.addView(linearLayout);
            }
        }
    }

    //원래 있던 todo의 버튼이 눌렸을 경우 저장해야하는 데이터
    //"workName" - todoname, "checkBok" - checkbox (int), "workStartTime" - actstarttime
    //default버튼의 callback함수들
    public void defaultStudyTouch(View v) {
        TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
        checkSignText.setText("Study, right?");

        mainLayout.setVisibility(View.INVISIBLE);
        checkOkLayout.setVisibility(View.VISIBLE);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wearDBeditor.putString("workStartTime", MainActivity.HourMin);
                wearDBeditor.putString("workName", "Study");
                wearDBeditor.putInt("beforeCheckBox", 0);
                wearDBeditor.apply();

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        calcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void defaultSelfDevelopTouch(View v) {
        TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
        checkSignText.setText("Self Development, right?");

        mainLayout.setVisibility(View.INVISIBLE);
        checkOkLayout.setVisibility(View.VISIBLE);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wearDBeditor.putString("workStartTime", MainActivity.HourMin);
                wearDBeditor.putString("workName", "Self Development");
                wearDBeditor.putInt("beforeCheckBox", 0);
                wearDBeditor.apply();

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        calcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void defaultLeisureTouch(View v) {
        TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
        checkSignText.setText("Leisure, right?");

        mainLayout.setVisibility(View.INVISIBLE);
        checkOkLayout.setVisibility(View.VISIBLE);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wearDBeditor.putString("workStartTime", MainActivity.HourMin);
                wearDBeditor.putString("workName", "Leisure");
                wearDBeditor.putInt("beforeCheckBox", 0);
                wearDBeditor.apply();

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        calcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void defaultExerciseTouch(View v) {
        TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
        checkSignText.setText("Exercise, right?");

        mainLayout.setVisibility(View.INVISIBLE);
        checkOkLayout.setVisibility(View.VISIBLE);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wearDBeditor.putString("workStartTime", MainActivity.HourMin);
                wearDBeditor.putString("workName", "Exercise");
                wearDBeditor.putInt("beforeCheckBox", 0);
                wearDBeditor.apply();

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        calcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void defaultBreatheTouch(View v) {
        TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
        checkSignText.setText("Breathe, right?");

        mainLayout.setVisibility(View.INVISIBLE);
        checkOkLayout.setVisibility(View.VISIBLE);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wearDBeditor.putString("workStartTime", MainActivity.HourMin);
                wearDBeditor.putString("workName", "Breathe");
                wearDBeditor.putInt("beforeCheckBox", 0);
                wearDBeditor.apply();

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        calcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void defaultNappingTouch(View v) {
        TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
        checkSignText.setText("Napping, right?");

        mainLayout.setVisibility(View.INVISIBLE);
        checkOkLayout.setVisibility(View.VISIBLE);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wearDBeditor.putString("workStartTime", MainActivity.HourMin);
                wearDBeditor.putString("workName", "Napping");
                wearDBeditor.putInt("beforeCheckBox", 0);
                wearDBeditor.apply();

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        calcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void defaultSleepingTouch(View v) {
        TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
        checkSignText.setText("Sleeping, right?");

        mainLayout.setVisibility(View.INVISIBLE);
        checkOkLayout.setVisibility(View.VISIBLE);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wearDBeditor.putString("workStartTime", MainActivity.HourMin);
                wearDBeditor.putString("workName", "Sleeping");
                wearDBeditor.putInt("beforeCheckBox", 0);
                wearDBeditor.apply();

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        calcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainLayout.setVisibility(View.VISIBLE);
                checkOkLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

}
