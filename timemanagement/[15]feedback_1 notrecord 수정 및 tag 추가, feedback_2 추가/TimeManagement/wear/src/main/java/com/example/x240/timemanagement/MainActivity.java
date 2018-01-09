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

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener,
        //NodeApi.NodeListener,
        DataApi.DataListener {

    private GoogleApiClient mGoogleApiClient; // 구글 플레이 서비스 API 객체
    //static변수는 다른 Activity에서 사용할 값들
    static String YearMonthDay, HourMin, YMDHM;                 //현재값
    //workStartTime, workName은 SharedPreferences를 통해 관리
    SharedPreferences wearDB;
    SharedPreferences.Editor wearDBeditor;
    //static String workStartTime, workName;                    //현재 진행하고 있는 일의 시작시간, 일의 이름(todo인 경우 todo이름, default인 경우 default이름)
    TextView doTouch, todoTouch;                                //for change activity
    static TextView curTime, doName, doTime, todoName, todoTime;//for show data, do -> 내가 하고있는 것, todo -> 현재 시간에 해야할 것
    LinearLayout progressLinear, doFinishLinear, sleepingCheckLinear, mainLinear;                    //for progressBar
    ImageView calcelButton, okButton;

    static ArrayList<ArrayList<String>> dataArrayList = new ArrayList<ArrayList<String>>();         //database <index 0(Date), 1(Tagname), 2(Todo), 4(ExpStartTime), 5(ExpEndTime)> 이 리스트 형태로 들어가 있음
    static ArrayList<Integer> checkBoxArray = new ArrayList<Integer>();                   //database index 3(Checkbox) 가 리스트 형태로 들어가 있음
    static int dataCount;                                      //데이터 개수(ArrayList길이)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sharedPreference 설정
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

        //for change screen Layout
        progressLinear = (LinearLayout) findViewById(R.id.progressLinear);
        doFinishLinear = (LinearLayout) findViewById(R.id.doFinishLinear);
        sleepingCheckLinear = (LinearLayout) findViewById(R.id.sleepingCheckLayout);
        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);

        calcelButton = (ImageView) findViewById(R.id.cancelButton);
        okButton = (ImageView) findViewById(R.id.okButton);

        // 구글 플레이 서비스 객체를 시계 설정으로 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        getTime();

        curTime = (TextView) findViewById(R.id.curTime);
        curTime.setText(HourMin);

        sendMessageForGetData();

        //다시 sendMessageForGetData() 함수 호출
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

        //다시 sendMessageForGetData() 함수 호출
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
                    //날이 지난것 체크
                    int nowHour = Integer.parseInt(HourMin.split(":")[0]);
                    int doHour = 0;
                    if(!wearDB.getString("workStartTime", null).equals(":")) {
                        doHour = Integer.parseInt(wearDB.getString("workStartTime", null).split(":")[0]);
                    }
                    if((nowHour > 4 && nowHour < 21) && (doHour > 21 || doHour < 4) && !wearDB.getString("workName", null).equals("Sleeping")) {      //저녁 9시를 기준으로 체크, 깜박하고 sleeping을 안누르고 잠을 잔 경우
                        Toast.makeText(getApplicationContext(), "data erased!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                        startActivity(intent);
                    }
                    else {
                        if (wearDB.getString("workName", null).equals("Nothing")) {
                            Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                            startActivity(intent);
                        } else {  //이미 어떤일을 진행하고 있는 경우
                            //todolist에 있는 일을 했느냐, ect일을 했느냐에 따라 달라짐
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
                            } else if (wearDB.getString("workName", null).equals("Study") || wearDB.getString("workName", null).equals("Self Development") || wearDB.getString("workName", null).equals("Leisure") || wearDB.getString("workName", null).equals("Exercise") || wearDB.getString("workName", null).equals("Breathe") || wearDB.getString("workName", null).equals("Napping")) {   //etc 일을 한 경우
                                //database에 추가해줘야
                                //인자 : DATE, TAGNAME, ACTSTARTTIME, ACTENDTIME
                                sendDataStringForAddNewData(YearMonthDay, wearDB.getString("workName", null), wearDB.getString("workStartTime", null), HourMin);

                                Intent intent = new Intent(getApplicationContext(), TodolistActivity.class);
                                startActivity(intent);
                            } else if (wearDB.getString("workName", null).equals("Memo")) {    //memo
                                Toast.makeText(getApplicationContext(), "MainActivity에서 memo가 들어옴... 수정요망", Toast.LENGTH_LONG).show();
                            } else {                                                  //todolist에 있는 일을 한 경우
                                mainLinear.setVisibility(View.INVISIBLE);
                                doFinishLinear.setVisibility(View.VISIBLE);

                                //ImageView notstartButton = (ImageView) findViewById(R.id.notstartButton);
                                ImageView doingButton = (ImageView) findViewById(R.id.doingButton);
                                ImageView doneButton = (ImageView) findViewById(R.id.doneButton);

                            /*
                            notstartButton.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    int action2 = motionEvent.getAction();
                                    if (action2 == motionEvent.ACTION_DOWN) {
                                        //actstarttime, actendtime, todocheck 수정해줘야
                                        //인자 : DATE, TODO, TODOCHECK, ACTSTARTTIME, ACTENDTIME
                                        sendDataStringForChangeData(YearMonthDay, wearDB.getString("workName", null), 0, wearDB.getString("workStartTime", null), HourMin);

                                        changeVisibleAndActivity();
                                    }
                                    return true;
                                }
                            });
                            */

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
    protected void onResume() {         //액티비티가 가려져있다가 다시 보였을때 실행됨
        super.onResume();
        getTime();
        curTime.setText(HourMin);

        //sendMessageForGetData();
        doName.setText(wearDB.getString("workName", null));
        doTime.setText(wearDB.getString("workStartTime", null) + "~Now");

    }

    private void getTime() {
        //현재실제시간
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        YMDHM = sdfNow.format(date);
        String[] tmpParts = YMDHM.split("\\s");
        String[] nowHourMinuteParts = tmpParts[1].split(":");

        if(Integer.parseInt(nowHourMinuteParts[0]) >= 4) {          //후에 설정을 통해 바꿀수 있도록...
            YearMonthDay = tmpParts[0];
        }
        else {
            YearMonthDay = getBeforeTheDay(tmpParts[0]);
        }
        //Toast.makeText(getApplicationContext(), YearMonthDay, Toast.LENGTH_LONG).show();
        HourMin = tmpParts[1];

    }

    private String getBeforeTheDay(String yearMonthDay) {
        String[] parts = yearMonthDay.split("-");
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // 두번째 인자는 월, 1월이 0, 2월이 1... 이렇게 나가기 때문에 -1을 해주어야 함
        cal.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String beforeDay = sdfNow2.format(cal.getTime());
        return beforeDay;
    }

    private String getAfterTheDay(String yearMonthDay) {
        String[] parts = yearMonthDay.split("-");
        Calendar cal = new GregorianCalendar(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2])); // 두번째 인자는 월, 1월이 0, 2월이 1... 이렇게 나가기 때문에 -1을 해주어야 함
        cal.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("yyyy-MM-dd");
        String beforeDay = sdfNow2.format(cal.getTime());
        return beforeDay;
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

    // 구글 플레이 서비스에 접속 됐을 때 실행
    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle bundle) {
        // 노드, 메시지, 데이터 이벤트를 활용할 수 있도록 이벤트 리스너 지정
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        //Wearable.NodeApi.addListener(mGoogleApiClient, this);
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    // 구글 플레이 서비스에 접속이 일시정지 됐을 때 실행
    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }

    // 구글 플레이 서비스에 접속을 실패했을 때 실행
    @Override // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();

        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        //Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
    }

    //DB로부터 YearMonthDay날의 데이터를 얻어오기 위함
    public void sendMessageForGetData() {

        // 페어링 기기들을 지칭하는 노드를 가져온다.
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {

                    // 노드를 가져온 후 실행된다.
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult
                                                 getConnectedNodesResult) {

                        // 노드를 순회하며 메시지를 전송한다.
                        for (final Node node : getConnectedNodesResult.getNodes()) {

                            // 전송할 메시지 텍스트 생성
                            byte[] bytes = YearMonthDay.getBytes();

                            // 메시지 전송 및 전송 후 실행 될 콜백 함수 지정
                            Wearable.MessageApi.sendMessage(mGoogleApiClient,
                                    node.getId(), "/GETDATA_PATH", bytes)
                                    .setResultCallback(resultCallback);
                        }
                    }
                });
    }

    // 모바일로 데이터 및 메시지를 전송 후 실행되는 메소드
    private ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void onResult(Result result) {
            //String resultString = "Sending Result : " + result.getStatus().isSuccess();
            //Toast.makeText(getApplication(), resultString, Toast.LENGTH_SHORT).show();
        }
    };


    private int sendCount = 0;
    //DB에 새로운 데이터(etc데이터)를 저장하기 위함
    //인자 : DATE, TAGNAME, ACTSTARTTIME, ACTENDTIME
    public void sendDataStringForAddNewData(String date, String tagname, String actStartTime, String actEndTime) {
        // 모바일로 전송할 데이터 묶음인 데이터 맵을 생성한다.
        PutDataMapRequest dataMap = PutDataMapRequest.create("/ADDNEWDATA_PATH");
        //받는쪽에서 todocheck 4으로 사용해야!

        // 전송할 텍스트를 지정한다.
        dataMap.getDataMap().putString("DATE", date);
        dataMap.getDataMap().putString("TAGNAME", tagname);
        dataMap.getDataMap().putString("ACTSTARTTIME", actStartTime);
        dataMap.getDataMap().putString("ACTENDTIME", actEndTime);

        // 현재 보내는 텍스트와 지난번 보냈던 텍스트가 같으면
        // onDataChanged() 메소드가 실행되지 않는다.
        // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
        dataMap.getDataMap().putInt("count", sendCount++);

        // 데이터 맵으로 전송할 요청 객체를 생성한다.
        PutDataRequest request = dataMap.asPutDataRequest();

        // 데이터 전송 및 전송 후 실행 될 콜백 함수 지정
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback2);
    }

    // 모바일로 데이터 및 메시지를 전송 후 실행되는 메소드
    private ResultCallback resultCallback2 = new ResultCallback() {
        @Override
        public void onResult(Result result) {
            //Log.d("data Send!", "in wearable new data");
            //데이터 전송은 잘됨..
        }
    };

    private int sendCount2 = 0;
    //DB에 있는 데이터의 ACTSTARTTIME과 ACTENDTIME을 실제 값으로 바꾸기 위함
    //인자 : DATE, TODO, TODOCHECK, ACTSTARTTIME, ACTENDTIME
    public void sendDataStringForChangeData(String date, String todo, int beforeTodocheck,int afterTodocheck, String actStartTime, String actEndTime) {
        // 모바일로 전송할 데이터 묶음인 데이터 맵을 생성한다.
        PutDataMapRequest dataMap = PutDataMapRequest.create("/CHANGEDATA_PATH");

        // 전송할 텍스트를 지정한다.
        dataMap.getDataMap().putString("DATE", date);
        dataMap.getDataMap().putString("TODO", todo);
        dataMap.getDataMap().putInt("BEFORETODOCHECK", beforeTodocheck);
        dataMap.getDataMap().putInt("TODOCHECK", afterTodocheck);
        dataMap.getDataMap().putString("ACTSTARTTIME", actStartTime);
        dataMap.getDataMap().putString("ACTENDTIME", actEndTime);

        // 현재 보내는 텍스트와 지난번 보냈던 텍스트가 같으면
        // onDataChanged() 메소드가 실행되지 않는다.
        // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
        dataMap.getDataMap().putInt("count", sendCount2++);

        // 데이터 맵으로 전송할 요청 객체를 생성한다.
        PutDataRequest request = dataMap.asPutDataRequest();

        // 데이터 전송 및 전송 후 실행 될 콜백 함수 지정
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback);
    }

    //----------------------------------------------------------------
    //다수의 데이터를 받았을 때 호출됨 (YearMonthDay의 데이터를 받는 함수)
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //Toast.makeText(getApplicationContext(), "getMessage", Toast.LENGTH_LONG).show();
        // 데이터 이벤트 횟수별로 동작한다.
        for (DataEvent event : dataEvents) {

            // 데이터 변경 이벤트일 때 실행된다.
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // 동작을 구분할 패스를 가져온다.
                String path = event.getDataItem().getUri().getPath();

                // 패스가 문자 데이터 일 때
                if (path.equals("/STRING_DATA_PATH")) {
                    //Log.d("for문 돌음", "for문 돌음");      //1번돌음
                    // todoName과 expStartTime, expEndTime을 저장한 변수 선언
                    String exptodoName = "Nothing";
                    String expTime = "";

                    // 이벤트 객체로부터 데이터 맵을 가져온다.
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-DD HH:mm");
                    dataCount = dataMapItem.getDataMap().getInt("dataCount");

                    ArrayList<ArrayList<String>> tmpDataArrayList = new ArrayList<ArrayList<String>>();
                    ArrayList<Integer> tmpCheckBoxArray = new ArrayList<Integer>();
                    for(int i = 0; i < dataCount; i++) {
                        ArrayList<String> oneTodoData = dataMapItem.getDataMap().getStringArrayList("todoData" + Integer.toString(i));

                        tmpDataArrayList.add(oneTodoData);      //wearable의 모든 activity에서 사용하는 database 데이터들은 이 mainActivity에서 관리
                        tmpCheckBoxArray.add(dataMapItem.getDataMap().getInt("checkBox" + Integer.toString(i)));

                        /*
                        if(oneTodoData.get(3).equals("None")) {     //checkBox가 0,1,2일때의 조건문으로 거르는 것이 안되면 이거 사용
                            continue;
                        }
                        */

                        if(tmpCheckBoxArray.get(i) == 0 || tmpCheckBoxArray.get(i) == 1 || tmpCheckBoxArray.get(i) == 2) {
                            try {   //for show to-do
                                //[위의 if문으로 해결!]data compare exception 뜸 -> etc데이터 가져오는 과정에서 문제생긴듯 (즉 todolist에는 진행것까지 모두떠야하기 때문에 데이터를 받아오는건 etc work까지 받아와야함. but todo를 띄우기 위해 체크하는 이과정에서는 제외시켜야)
                                Date startTime;
                                String[] nowHourMinuteParts = oneTodoData.get(3).split(":");
                                if(Integer.parseInt(nowHourMinuteParts[0]) >= 4) {          //특수한 경우 여기서 nowHourMinuteParts[0]이 None값이여서 exception 발생...
                                    startTime = formatter.parse(oneTodoData.get(0) + " " + oneTodoData.get(3));
                                }
                                else {
                                    String afterDay = getAfterTheDay(oneTodoData.get(0));
                                    startTime = formatter.parse(afterDay + " " + oneTodoData.get(3));
                                }

                                Date endTime;
                                String[] nowHourMinuteParts2 = oneTodoData.get(4).split(":");
                                if(Integer.parseInt(nowHourMinuteParts2[0]) >= 4) {          //후에 설정을 통해 바꿀수 있도록...
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

                    // UI 스레드를 실행하여 텍스트 뷰의 값을 수정한다.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            todoName.setText(inputTodoName);
                            todoTime.setText(inputExpTime);
                        }
                    });

                    //여기가 데이터를 받고 화면에 띄운 시점, 이때까지 waitbar를 띄움
                    progressLinear.setVisibility(View.INVISIBLE);
                    mainLinear.setVisibility(View.VISIBLE);

                    /*
                    for(int i = 0; i < dataCount; i++) {
                        Log.d(Integer.toString(i) + "번째data", dataArrayList.get(i).get(0) + ", " + dataArrayList.get(i).get(1) + ", " + dataArrayList.get(i).get(2) + ", " + Integer.toString(checkBoxArray.get(i)) + ", " + dataArrayList.get(i).get(3) + ", " + dataArrayList.get(i).get(4));
                    }
                    */
                }

                // 데이터 삭제 이벤트일 때 실행된다.
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // 데이터가 삭제됐을 때 수행할 동작
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
