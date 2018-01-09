package com.example.x240.timemanagement;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import static com.example.x240.timemanagement.MainActivity.checkBoxArray;
import static com.example.x240.timemanagement.MainActivity.dataArrayList;
import static com.example.x240.timemanagement.MainActivity.dataCount;

/**
 * Created by X240 on 2017-09-07.
 */

public class TodolistActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient; // 구글 플레이 서비스 API 객체, memo를 위해

    SharedPreferences wearDB;
    SharedPreferences.Editor wearDBeditor;

    LinearLayout studyLinear, selfDevelopLinear, leisureLinear, exerciseLinear, memoLinear;         //실제 todo리스트를 이 layout에 추가시키면 됨
    ImageView calcelButton, okButton;

    //밑의 두 레이아웃은 프레임레이아웃으로 이루어져 있어서 화면전환이 됨
    LinearLayout checkOkLayout;
    ScrollView mainLayout;

    int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // 구글 플레이 서비스 객체를 시계 설정으로 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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
        memoLinear = (LinearLayout) findViewById(R.id.MemoLinear);

        Toast.makeText(getApplicationContext(), "datacount" + Integer.toString(dataCount), Toast.LENGTH_SHORT).show();
        //todo LinearLayout에 추가
        for(i = 0; i < dataCount; i++) {
            /*
            if(MainActivity.checkBoxArray.get(i) == 4 || MainActivity.checkBoxArray.get(i) == 5) {        //etc 일에 대한 처리
                continue;
            }
            */
            if(MainActivity.checkBoxArray.get(i) == 0 || MainActivity.checkBoxArray.get(i) == 1 || MainActivity.checkBoxArray.get(i) == 2 || MainActivity.checkBoxArray.get(i) == 6 || MainActivity.checkBoxArray.get(i) == 7) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        80));

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
                    case 6:
                        tagImage.setImageResource(R.drawable.ic_notstart_memo);
                        break;
                    case 7:
                        tagImage.setImageResource(R.drawable.ic_done_memo);
                        break;
                }

                tagImage.setPadding(21, 5, 0, 0);
                tagImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 80, Gravity.CENTER_VERTICAL));

                TextView todoText = new TextView(this);
                todoText.setId(Integer.parseInt(Integer.toString(i + 1) + Integer.toString(checkBoxArray.get(i))));       //checkBox를 보관하기 위함, 만약 checkBox가 1이고 i가 10이면 id값은 110로 우리가 사용할때에는 idx 0만 사용!, 여기서 나올수 있는 checkBox는 0,1,2 뿐임
                todoText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80));
                todoText.setText(dataArrayList.get(i).get(2));
                todoText.setTextSize(17);
                todoText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                if(MainActivity.checkBoxArray.get(i) == 0 || MainActivity.checkBoxArray.get(i) == 1) {
                    todoText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String viewText = (String) ((TextView) view).getText();
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
                }
                else if(MainActivity.checkBoxArray.get(i) == 6) {
                    todoText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String viewText = (String) ((TextView) view).getText();
                            TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
                            checkSignText.setText(viewText + " done?");

                            String id = Integer.toString(view.getId());
                            final int checkBox = Character.getNumericValue(id.charAt(id.length() - 1));

                            mainLayout.setVisibility(View.INVISIBLE);
                            checkOkLayout.setVisibility(View.VISIBLE);


                            okButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sendDataStringForChangeMemo(MainActivity.YearMonthDay, viewText);

                                    mainLayout.setVisibility(View.VISIBLE);
                                    checkOkLayout.setVisibility(View.INVISIBLE);
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
                }

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
                } else if(dataArrayList.get(i).get(1).equals("Memo")) {
                    memoLinear.addView(linearLayout);
                }

            }
        }
    }

    private int sendCount = 0;
    //DB에 있는 데이터의 ACTSTARTTIME과 ACTENDTIME을 실제 값으로 바꾸기 위함
    //인자 : DATE, TODO, TODOCHECK, ACTSTARTTIME, ACTENDTIME
    public void sendDataStringForChangeMemo(String date, String todo) {
        // 모바일로 전송할 데이터 묶음인 데이터 맵을 생성한다.
        PutDataMapRequest dataMap = PutDataMapRequest.create("/CHANGEMEMO_PATH");

        // 전송할 텍스트를 지정한다.
        dataMap.getDataMap().putString("DATE", date);
        dataMap.getDataMap().putString("TODO", todo);

        // 현재 보내는 텍스트와 지난번 보냈던 텍스트가 같으면
        // onDataChanged() 메소드가 실행되지 않는다.
        // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
        dataMap.getDataMap().putInt("count", sendCount++);

        // 데이터 맵으로 전송할 요청 객체를 생성한다.
        PutDataRequest request = dataMap.asPutDataRequest();

        // 데이터 전송 및 전송 후 실행 될 콜백 함수 지정
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback);
    }

    // 모바일로 데이터 및 메시지를 전송 후 실행되는 메소드
    private ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void onResult(Result result) {
            //String resultString = "Sending Result : " + result.getStatus().isSuccess();
            //Toast.makeText(getApplication(), resultString, Toast.LENGTH_SHORT).show();
        }
    };

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

    // 액티비티가 시작할 때 실행
    @Override // Activity
    protected void onStart() {
        super.onStart();

        // 구글 플레이 서비스에 접속돼 있지 않다면 접속한다.
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    // 액티비티가 종료될 때 실행
    @Override // Activity
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
