package com.example.x240.timemanagement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;


// MainActivity에 intent 생성해서 startService(intent)를 해주는 것이 매우 중요!!
public class MainService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient; // 구글 플레이 서비스 API 객체
    ContactDBHelper dbHelper = null;

    public MainService() {
    }

    private TextView mTextView; // 텍스트를 출력할 뷰

    @Override // Activity
    public void onCreate() {
        super.onCreate();

        init_tables();

        // 구글 플레이 서비스 객체를 시계 설정으로 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // 구글 플레이 서비스에 접속돼 있지 않다면 접속한다.
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    private void init_tables() {
        dbHelper = new ContactDBHelper(this);
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    // 구글 플레이 서비스에 접속 됐을 때 실행
    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle bundle) {

    }

    // 구글 플레이 서비스에 접속이 일시정지 됐을 때 실행
    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int i) {

    }

    // 구글 플레이 서비스에 접속을 실패했을 때 실행
    @Override // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // 페어링이 되면 실행된다.
    @Override // NodeApi.NodeListener
    public void onPeerConnected(Node node) {

    }

    // 페어링이 해제되면 실행된다.
    @Override // NodeApi.NodeListener
    public void onPeerDisconnected(Node node) {

    }

    // 메시지가 수신되면 실행되는 메소드
    @Override // MessageApi.MessageListener
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/GETDATA_PATH")) {
            final String YearMonthDay = new String(messageEvent.getData(), 0, messageEvent.getData().length);
            SendTodoData(YearMonthDay);
        }

    }


    // 데이터 전송 횟수이다.
    // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
    private int sendCount = 0;

    public void SendTodoData(String YearMonthDay) {
        // 전송할 텍스트를 생성한다.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + YearMonthDay + "'", null);

        // 모바일로 전송할 데이터 묶음인 데이터 맵을 생성한다.
        PutDataMapRequest dataMap = PutDataMapRequest.create("/STRING_DATA_PATH");
        int dataNumber = 0;
        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                ArrayList<String> oneTodoData = new ArrayList<String>();

                oneTodoData.add(cursor.getString(0));
                oneTodoData.add(cursor.getString(1));
                oneTodoData.add(cursor.getString(2));
                oneTodoData.add(cursor.getString(4));
                oneTodoData.add(cursor.getString(5));

                int checkBox = cursor.getInt(3);          //TODOCHECK

                // 전송할 텍스트를 지정한다.
                dataMap.getDataMap().putStringArrayList("todoData" + Integer.toString(dataNumber), oneTodoData);    //tododata0부터 시작
                dataMap.getDataMap().putInt("checkBox" + Integer.toString(dataNumber), checkBox);                   //checkBox0부터 시작

                dataNumber++;
                cursor.moveToNext();
            }
        }

        dataMap.getDataMap().putInt("dataCount", dataNumber);
        // 현재 보내는 텍스트와 지난번 보냈던 텍스트가 같으면
        // onDataChanged() 메소드가 실행되지 않는다.
        // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
        dataMap.getDataMap().putInt("count", sendCount++);

        // 데이터 맵으로 전송할 요청 객체를 생성한다.
        PutDataRequest request = dataMap.asPutDataRequest();

        // 데이터 전송 및 전송 후 실행 될 콜백 함수 지정
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback1);
    }

    // 모바일로 데이터 및 메시지를 전송 후 실행되는 메소드
    private ResultCallback resultCallback1 = new ResultCallback() {
        @Override
        public void onResult(Result result) {
        }
    };

    // 구글 플레이 서비스의 데이터가 변경되면 실행된다.
    @Override // DataApi.DataListener
    public void onDataChanged(DataEventBuffer dataEvents) {

        // 데이터 이벤트 횟수별로 동작한다.
        for (DataEvent event : dataEvents) {

            // 데이터 변경 이벤트일 때 실행된다.
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // 동작을 구분할 패스를 가져온다.
                String path = event.getDataItem().getUri().getPath();

                // 패스가 문자 데이터 일 때
                if (path.equals("/ADDNEWDATA_PATH")) {      //etc data
                    // 이벤트 객체로부터 데이터 맵을 가져온다.
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    // 데이터맵으로부터 수신한 문자열을 가져온다.
                    String date = dataMapItem.getDataMap().getString("DATE");
                    String tagname = dataMapItem.getDataMap().getString("TAGNAME");
                    String actStartTime = dataMapItem.getDataMap().getString("ACTSTARTTIME");
                    String actEndTime = dataMapItem.getDataMap().getString("ACTENDTIME");

                    //DB에 추가 (save_values 함수 참고)
                    save_etc_values(date, tagname, actStartTime, actEndTime);   //data가 추가되면 현재 checkBox를 2로 해놓았기 때문에(원래는 10으로 할거) wear, phone todo리스트에도 떠야하는데 뜨지도 않고... database dataCount도 안바뀌고..
                    //[해결]확인 결과 이 함수(onDataChanged)가 아예 호출이 안됨
                }
                else if(path.equals("/CHANGEDATA_PATH")) {
                    // 이벤트 객체로부터 데이터 맵을 가져온다.
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    // 데이터맵으로부터 수신한 문자열을 가져온다.
                    String date = dataMapItem.getDataMap().getString("DATE");
                    String todo = dataMapItem.getDataMap().getString("TODO");
                    int beforeTodocheck = dataMapItem.getDataMap().getInt("BEFORETODOCHECK");
                    int todocheck = dataMapItem.getDataMap().getInt("TODOCHECK");         //이 값이 1 -> doing 눌림, 2 -> done 눌림
                    String actstarttime = dataMapItem.getDataMap().getString("ACTSTARTTIME");
                    String actendtime = dataMapItem.getDataMap().getString("ACTENDTIME");

                    //이번행부터 265행부분만 수정했음. 안되면 이부분 원상복귀(나머지 지우고 else문 안에 있는거만 사용)
                    if(beforeTodocheck == 1 && todocheck == 1) {    //저장된 데이터의 checkBox가 1인 경우 (한번 데이터를 수정한 경우) & doing 버튼을 누른경우 (1->1)
                        String tagname = getTagName(todo);
                        save_extra_doing_values(date, tagname, todo, actstarttime, actendtime);
                    }
                    else if(beforeTodocheck == 1 && todocheck == 2) {     //저장된 데이터의 checkBox가 1인 경우 (한번 데이터를 수정한 경우) & done 버튼을 누른경우 (1->2)
                        String tagname = getTagName(todo);
                        save_extra_doing_values(date, tagname, todo, actstarttime, actendtime);

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("TODOCHECK", todocheck);
                        db.update("TodoDatabase", contentValues, "DATE = ? AND TODO = ? AND TODOCHECK = ?",new String[] { date, todo, Integer.toString(1) });       //여기서 TODOCHECK를 안넣으면 전에 추가되었던(checkbox가 5였던) 데이터들까지도 모두 checkbox가 2로 바뀜
                    }
                    else {          //나머지 경우(0->1, 0->2)
                        //DB에 있는 값을 가져와 일부분을 수정해 주어야 함
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("TODOCHECK", todocheck);
                        contentValues.put("ACTSTARTTIME", actstarttime);
                        contentValues.put("ACTENDTIME", actendtime);
                        db.update("TodoDatabase", contentValues, "DATE = ? AND TODO = ?",new String[] { date, todo });
                    }

                   /*
                    //DB에 있는 값을 가져와 일부분을 수정해 주어야 함
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("TODOCHECK", todocheck);
                    contentValues.put("ACTSTARTTIME", actstarttime);
                    contentValues.put("ACTENDTIME", actendtime);
                    db.update("TodoDatabase", contentValues, "DATE = ? AND TODO = ?",new String[] { date, todo });
                    */


                }

                // 데이터 삭제 이벤트일 때 실행된다.
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // 데이터가 삭제됐을 때 수행할 동작
            }
        }
    }

    private String getTagName(String todo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE TODO='" + todo + "'", null);

        String tagname = "Study";
        if(cursor.moveToFirst()) {
            tagname = cursor.getString(1);
        }
        return tagname;
    }

    //OK! 잘됨!
    private void save_extra_doing_values(String date, String tagname, String todo, String actStartTime, String actEndTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sqlInsert = ContactDBCtrct.SQL_INSERT +
                " ('" +
                date + "', " +
                "'" + tagname + "', " +
                "'" + todo + "', " +
                "5" + ", " +                   //추가데이터 todocheck는 5으로 정함
                "'" + "None" + "', " +
                "'" + "None" + "', " +
                "'" + actStartTime + "', " +
                "'" + actEndTime + "')";

        db.execSQL(sqlInsert);
    }

    private void save_etc_values(String date, String tagname, String actStartTime, String actEndTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sqlInsert = ContactDBCtrct.SQL_INSERT +
                " ('" +
                date + "', " +
                "'" + tagname + "', " +
                "'" + tagname + "', " +         //todo와 tagname과 같음
                "4" + ", " +                   //etc todocheck는 4으로 정함
                "'" + "None" + "', " +
                "'" + "None" + "', " +
                "'" + actStartTime + "', " +
                "'" + actEndTime + "')";

        db.execSQL(sqlInsert);
    }
}
