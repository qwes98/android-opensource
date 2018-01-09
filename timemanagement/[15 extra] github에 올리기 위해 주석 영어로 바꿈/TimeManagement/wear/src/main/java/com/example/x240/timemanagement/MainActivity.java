package com.example.x240.timemanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by One on 2017-09-01.
 * Author : Jiwon Park
 * This is MainActivity for wearable.
 */

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener,
        DataApi.DataListener {

    // Change date based on this value
    public static final int dateChangeHour = 4;

    private GoogleApiClient mGoogleApiClient; // Google Play Service API Object
    //static variable is for other activity
    static String YearMonthDay, HourMin, YMDHM;                 // Current value
    // Shared Preferences is for sending workStartTime, workName
    SharedPreferences wearDB;
    SharedPreferences.Editor wearDBeditor;
    TextView doTouch, todoTouch;                                // For change activity
    static TextView curTime, doName, doTime, todoName, todoTime;// For show data
    LinearLayout progressLinear, doFinishLinear, sleepingCheckLinear, mainLinear;                    // For progressbar
    ImageView calcelButton, okButton;

    static ArrayList<ArrayList<String>> dataArrayList = new ArrayList<ArrayList<String>>();         // List format : index 0(Date), 1(Tagname), 2(Todo), 4(ExpStartTime), 5(ExpEndTime)
    static ArrayList<Integer> checkBoxArray = new ArrayList<Integer>();                   // List format : index 3(Checkbox)
    static int dataCount;                                       // The length of ArrayList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting sharedPreference
        wearDB = getSharedPreferences("wearDB", MODE_PRIVATE);
        wearDBeditor = wearDB.edit();
        if(wearDB.getString("workStartTime", null) == null) {
            wearDBeditor.putString("workStartTime", ":");
            wearDBeditor.apply();
        }
        if (wearDB.getString("workName", null) == null) {
            wearDBeditor.putString("workName", "Nothing");
            wearDBeditor.apply();
        }
        if(wearDB.getInt("beforeCheckBox", 0) == 0) {
            wearDBeditor.putInt("beforeCheckBox", 0);
            wearDBeditor.apply();
        }

        doName = (TextView) findViewById(R.id.doName);
        doTime = (TextView) findViewById(R.id.doTime);
        doName.setText(wearDB.getString("workName", null));
        doTime.setText(wearDB.getString("workStartTime", null) + "~Now");

        // For change screen Layout
        progressLinear = (LinearLayout) findViewById(R.id.progressLinear);
        doFinishLinear = (LinearLayout) findViewById(R.id.doFinishLinear);
        sleepingCheckLinear = (LinearLayout) findViewById(R.id.sleepingCheckLayout);
        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);

        calcelButton = (ImageView) findViewById(R.id.cancelButton);
        okButton = (ImageView) findViewById(R.id.okButton);

        // Initialize Google Paly Service Object as Wearable
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        getTime();

        curTime = (TextView) findViewById(R.id.curTime);
        curTime.setText(HourMin);

        sendMessageForGetData();

        // Recall sendMessageForGetData()
        curTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == motionEvent.ACTION_DOWN) {
                    progressLinear.setVisibility(View.VISIBLE);
                    mainLinear.setVisibility(View.INVISIBLE);
                    sendMessageForGetData();
                }
                return true;
            }
        });

        // Recall sendMessageForGetData()
        progressLinear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == motionEvent.ACTION_DOWN) {
                    sendMessageForGetData();
                }
                return true;
            }
        });

        doTouch = (TextView) findViewById(R.id.doTouch);
        doTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == motionEvent.ACTION_DOWN) {
                    int nowHour = Integer.parseInt(HourMin.split(":")[0]);
                    int doHour = 0;
                    if(!wearDB.getString("workStartTime", null).equals(":")) {
                        doHour = Integer.parseInt(wearDB.getString("workStartTime", null).split(":")[0]);
                    }
                    if((nowHour > 4 && nowHour < 21) && (doHour > 21 || doHour < 4) && !wearDB.getString("workName", null).equals("Sleeping")) {      // Erase Yesterday's data
                        Toast.makeText(getApplicationContext(), "data erased!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                        startActivity(intent);
                    }
                    else {
                        if (wearDB.getString("workName", null).equals("Nothing")) {
                            Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                            startActivity(intent);
                        } else {
                            if(wearDB.getString("workName", null).equals("Sleeping")) {
                                TextView checkSignText = (TextView) findViewById(R.id.checkSignText);
                                checkSignText.setText(wearDB.getString("workStartTime", null) + "~" + HourMin + ", right?");

                                mainLinear.setVisibility(View.INVISIBLE);
                                sleepingCheckLinear.setVisibility(View.VISIBLE);


                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        sendDataStringForAddNewData(YearMonthDay, wearDB.getString("workName", null), wearDB.getString("workStartTime", null), HourMin);

                                        mainLinear.setVisibility(View.VISIBLE);
                                        sleepingCheckLinear.setVisibility(View.INVISIBLE);

                                        Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                calcelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        mainLinear.setVisibility(View.VISIBLE);
                                        sleepingCheckLinear.setVisibility(View.INVISIBLE);

                                        Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            } else if (wearDB.getString("workName", null).equals("Study") || wearDB.getString("workName", null).equals("Self Development") || wearDB.getString("workName", null).equals("Leisure") || wearDB.getString("workName", null).equals("Exercise") || wearDB.getString("workName", null).equals("Breathe") || wearDB.getString("workName", null).equals("Napping")) {   // Etc work
                                // Add to database
                                sendDataStringForAddNewData(YearMonthDay, wearDB.getString("workName", null), wearDB.getString("workStartTime", null), HourMin);

                                Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                                startActivity(intent);
                            } else if (wearDB.getString("workName", null).equals("Memo")) {    //memo
                                Toast.makeText(getApplicationContext(), "MainActivity에서 memo가 들어옴... 수정요망", Toast.LENGTH_LONG).show();
                            } else {                                                  // When doing todo work
                                mainLinear.setVisibility(View.INVISIBLE);
                                doFinishLinear.setVisibility(View.VISIBLE);

                                //ImageView notstartButton = (ImageView) findViewById(R.id.notstartButton);
                                ImageView doingButton = (ImageView) findViewById(R.id.doingButton);
                                ImageView doneButton = (ImageView) findViewById(R.id.doneButton);

                                doingButton.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        int action2 = motionEvent.getAction();
                                        if (action2 == motionEvent.ACTION_DOWN) {
                                            sendDataStringForChangeData(YearMonthDay, wearDB.getString("workName", null), wearDB.getInt("beforeCheckBox", 0), 1, wearDB.getString("workStartTime", null), HourMin);

                                            changeVisibleAndActivity();
                                        }
                                        return true;
                                    }
                                });

                                doneButton.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        int action2 = motionEvent.getAction();
                                        if (action2 == motionEvent.ACTION_DOWN) {
                                            sendDataStringForChangeData(YearMonthDay, wearDB.getString("workName", null), wearDB.getInt("beforeCheckBox", 0), 2, wearDB.getString("workStartTime", null), HourMin);

                                            changeVisibleAndActivity();
                                        }
                                        return true;
                                    }
                                });

                            }
                        }
                    }
                }
                return true;
            }
        });

        todoTouch = (TextView) findViewById(R.id.todoTouch);
        todoTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == motionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void changeVisibleAndActivity() {
        mainLinear.setVisibility(View.VISIBLE);
        doFinishLinear.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {         // Execute when activity is hided and rerun
        super.onResume();
        getTime();
        curTime.setText(HourMin);

        //sendMessageForGetData();
        doName.setText(wearDB.getString("workName", null));
        doTime.setText(wearDB.getString("workStartTime", null) + "~Now");

    }

    private void getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        YMDHM = sdfNow.format(date);
        String[] tmpParts = YMDHM.split("\\s");
        String[] nowHourMinuteParts = tmpParts[1].split(":");

        if(Integer.parseInt(nowHourMinuteParts[0]) >= dateChangeHour) {
            YearMonthDay = tmpParts[0];
        }
        else {
            YearMonthDay = getBeforeTheDay(tmpParts[0]);
        }
        HourMin = tmpParts[1];

    }

    private String getBeforeTheDay(String yearMonthDay) {
        String[] parts = yearMonthDay.split("-");
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // parts[1] value means Month and that value is one less than actual. So we have to subtract 1.
        cal.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String beforeDay = sdfNow2.format(cal.getTime());
        return beforeDay;
    }

    private String getAfterTheDay(String yearMonthDay) {
        String[] parts = yearMonthDay.split("-");
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String beforeDay = sdfNow2.format(cal.getTime());
        return beforeDay;
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

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle bundle) {
        // Set eventlistener for using message, data event
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();

        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        //Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
    }

    // For getting YearMonthDay from database
    public void sendMessageForGetData() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {

                    // 노드를 가져온 후 실행된다.
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult
                                                 getConnectedNodesResult) {

                        for (final Node node : getConnectedNodesResult.getNodes()) {

                            // Make message text
                            byte[] bytes = YearMonthDay.getBytes();

                            // Set callback function
                            Wearable.MessageApi.sendMessage(mGoogleApiClient,
                                    node.getId(), "/GETDATA_PATH", bytes)
                                    .setResultCallback(resultCallback);
                        }
                    }
                });
    }

    private ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void onResult(Result result) {
            //String resultString = "Sending Result : " + result.getStatus().isSuccess();
            //Toast.makeText(getApplication(), resultString, Toast.LENGTH_SHORT).show();
        }
    };


    private int sendCount = 0;
    // For add etc data on database
    // Parameter : DATE, TAGNAME, ACTSTARTTIME, ACTENDTIME
    public void sendDataStringForAddNewData(String date, String tagname, String actStartTime, String actEndTime) {
        // Create data map for sending data
        PutDataMapRequest dataMap = PutDataMapRequest.create("/ADDNEWDATA_PATH");

        dataMap.getDataMap().putString("DATE", date);
        dataMap.getDataMap().putString("TAGNAME", tagname);
        dataMap.getDataMap().putString("ACTSTARTTIME", actStartTime);
        dataMap.getDataMap().putString("ACTENDTIME", actEndTime);

        // If sending text is the same as the text sended already, onDataChanged() is not called.
        // So for preventing this problem, we should send 'Count' value that is always have different number.
        dataMap.getDataMap().putInt("count", sendCount++);

        // Create a Request Object to transfer to the data map.
        PutDataRequest request = dataMap.asPutDataRequest();

        // Set Callback function.
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback2);
    }

    private ResultCallback resultCallback2 = new ResultCallback() {
        @Override
        public void onResult(Result result) {
            //Log.d("data Send!", "in wearable new data");
        }
    };

    private int sendCount2 = 0;

    // Change ACTSTARTTIME and ACTENDTIME to real value on database
    //인자 : DATE, TODO, TODOCHECK, ACTSTARTTIME, ACTENDTIME
    public void sendDataStringForChangeData(String date, String todo, int beforeTodocheck,int afterTodocheck, String actStartTime, String actEndTime) {
        PutDataMapRequest dataMap = PutDataMapRequest.create("/CHANGEDATA_PATH");

        dataMap.getDataMap().putString("DATE", date);
        dataMap.getDataMap().putString("TODO", todo);
        dataMap.getDataMap().putInt("BEFORETODOCHECK", beforeTodocheck);
        dataMap.getDataMap().putInt("TODOCHECK", afterTodocheck);
        dataMap.getDataMap().putString("ACTSTARTTIME", actStartTime);
        dataMap.getDataMap().putString("ACTENDTIME", actEndTime);

        dataMap.getDataMap().putInt("count", sendCount2++);

        PutDataRequest request = dataMap.asPutDataRequest();

        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback);
    }

    // ----------------------------------------------------------------
    // Called when getting multiple data
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {

                String path = event.getDataItem().getUri().getPath();

                if (path.equals("/STRING_DATA_PATH")) {
                    String exptodoName = "Nothing";
                    String expTime = "";

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-DD HH:mm");
                    dataCount = dataMapItem.getDataMap().getInt("dataCount");

                    ArrayList<ArrayList<String>> tmpDataArrayList = new ArrayList<ArrayList<String>>();
                    ArrayList<Integer> tmpCheckBoxArray = new ArrayList<Integer>();
                    for(int i = 0; i < dataCount; i++) {
                        ArrayList<String> oneTodoData = dataMapItem.getDataMap().getStringArrayList("todoData" + Integer.toString(i));

                        tmpDataArrayList.add(oneTodoData);      // The database data used in all wearable activities is managed by this mainActivity.

                        tmpCheckBoxArray.add(dataMapItem.getDataMap().getInt("checkBox" + Integer.toString(i)));

                        if(tmpCheckBoxArray.get(i) == 0 || tmpCheckBoxArray.get(i) == 1 || tmpCheckBoxArray.get(i) == 2) {
                            try {   // For show todo
                                Date startTime;
                                String[] nowHourMinuteParts = oneTodoData.get(3).split(":");
                                if(Integer.parseInt(nowHourMinuteParts[0]) >= dateChangeHour) {
                                    startTime = formatter.parse(oneTodoData.get(0) + " " + oneTodoData.get(3));
                                }
                                else {
                                    String afterDay = getAfterTheDay(oneTodoData.get(0));
                                    startTime = formatter.parse(afterDay + " " + oneTodoData.get(3));
                                }

                                Date endTime;
                                String[] nowHourMinuteParts2 = oneTodoData.get(4).split(":");
                                if(Integer.parseInt(nowHourMinuteParts2[0]) >= dateChangeHour) {
                                    endTime = formatter.parse(oneTodoData.get(0) + " " + oneTodoData.get(4));
                                }
                                else {
                                    String afterDay = getAfterTheDay(oneTodoData.get(0));
                                    endTime = formatter.parse(afterDay + " " + oneTodoData.get(4));
                                }

                                Date nowTime = formatter.parse(YMDHM);

                                if (nowTime.after(startTime) && nowTime.before(endTime)) {
                                    exptodoName = oneTodoData.get(2);
                                    expTime = oneTodoData.get(3) + "~" + oneTodoData.get(4);
                                }
                            } catch (ParseException e) {
                                Toast.makeText(getApplicationContext(), "date compare exception!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    dataArrayList = tmpDataArrayList;
                    checkBoxArray = tmpCheckBoxArray;

                    final TextView todoName = (TextView) findViewById(R.id.todoName);
                    final TextView todoTime = (TextView) findViewById(R.id.todoTime);

                    final String inputTodoName = exptodoName;
                    final String inputExpTime = expTime;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            todoName.setText(inputTodoName);
                            todoTime.setText(inputExpTime);
                        }
                    });

                    // Waitbar is erased and MainActivity is showed
                    progressLinear.setVisibility(View.INVISIBLE);
                    mainLinear.setVisibility(View.VISIBLE);

                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
    }

    /*
    @Override
    public void onPeerConnected(Node node) {

    }

    @Override
    public void onPeerDisconnected(Node node) {

    }
    */
}
