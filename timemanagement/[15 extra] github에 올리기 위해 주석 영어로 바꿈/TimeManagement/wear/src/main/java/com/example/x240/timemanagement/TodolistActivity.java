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
 * Created by One on 2017-09-01.
 * Author : Jiwon Park
 * This is TodolistActivity for wearable. you can select todo work or etc work.
 */

public class TodolistActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    SharedPreferences wearDB;
    SharedPreferences.Editor wearDBeditor;

    LinearLayout studyLinear, selfDevelopLinear, leisureLinear, exerciseLinear, memoLinear;         // should add actual todo list to these layout
    ImageView calcelButton, okButton;

    // The two layout below are frame layout and are transition
    LinearLayout checkOkLayout;
    ScrollView mainLayout;

    int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // Initialize Google Paly Service Object as Wearable
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

        // If we go out without select new work, workName and workStartTime is inilialized for prevent problems
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

        // Add in todo LinearLayout
        for(i = 0; i < dataCount; i++) {
            if(MainActivity.checkBoxArray.get(i) == 0 || MainActivity.checkBoxArray.get(i) == 1 || MainActivity.checkBoxArray.get(i) == 2 || MainActivity.checkBoxArray.get(i) == 6 || MainActivity.checkBoxArray.get(i) == 7) {    // Exclude etc work
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
                todoText.setId(Integer.parseInt(Integer.toString(i + 1) + Integer.toString(checkBoxArray.get(i))));       // Store checkBox. checkBox is index 0 value
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
    // Change ACTSTARTTIME and ACTENDTIME to real value on database
    // Parameter : DATE, TODO, TODOCHECK, ACTSTARTTIME, ACTENDTIME
    public void sendDataStringForChangeMemo(String date, String todo) {
        // Create data map for sending data
        PutDataMapRequest dataMap = PutDataMapRequest.create("/CHANGEMEMO_PATH");

        dataMap.getDataMap().putString("DATE", date);
        dataMap.getDataMap().putString("TODO", todo);

        // If sending text is the same as the text sended already, onDataChanged() is not called.
        // So for preventing this problem, we should send 'Count' value that is always have different number.
        dataMap.getDataMap().putInt("count", sendCount++);

        // Create a Request Object to transfer to the data map.
        PutDataRequest request = dataMap.asPutDataRequest();

        // Set Callback function.
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback);
    }

    private ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void onResult(Result result) {
            //String resultString = "Sending Result : " + result.getStatus().isSuccess();
            //Toast.makeText(getApplication(), resultString, Toast.LENGTH_SHORT).show();
        }
    };

    // "workName" - todoname, "checkBok" - checkbox (int), "workStartTime" - actstarttime
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

    @Override // Activity
    protected void onStart() {
        super.onStart();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

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
