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

/**
 * Created by One on 2017-09-01.
 * Author : Jiwon Park
 * This is MainService. This service always wait user to send data using their smartwatch.
 * In MainActivity, we have to start this service using intent!
 */

public class MainService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient; // Google Play Service API Object
    ContactDBHelper dbHelper = null;

    public MainService() {
    }

    private TextView mTextView;

    @Override
    public void onCreate() {
        super.onCreate();

        init_tables();

        // Initialize Google Paly Service Object as Wearable
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle bundle) {

    }

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int i) {

    }

    @Override // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // Execute when pairing
    @Override // NodeApi.NodeListener
    public void onPeerConnected(Node node) {

    }

    @Override // NodeApi.NodeListener
    public void onPeerDisconnected(Node node) {

    }

    // Callback function when getting message
    @Override // MessageApi.MessageListener
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/GETDATA_PATH")) {
            final String YearMonthDay = new String(messageEvent.getData(), 0, messageEvent.getData().length);
            SendTodoData(YearMonthDay);
        }

    }

    private int sendCount = 0;

    public void SendTodoData(String YearMonthDay) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoDatabase WHERE DATE='" + YearMonthDay + "'", null);

        // Create data map for sending data
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

                dataMap.getDataMap().putStringArrayList("todoData" + Integer.toString(dataNumber), oneTodoData);    // Start : tododata0
                dataMap.getDataMap().putInt("checkBox" + Integer.toString(dataNumber), checkBox);                   // Start : checkBox0

                dataNumber++;
                cursor.moveToNext();
            }
        }

        dataMap.getDataMap().putInt("dataCount", dataNumber);
        // If sending text is the same as the text sended already, onDataChanged() is not called.
        // So for preventing this problem, we should send 'Count' value that is always have different number.
        dataMap.getDataMap().putInt("count", sendCount++);

        // Create a Request Object to transfer to the data map.
        PutDataRequest request = dataMap.asPutDataRequest();

        // Set Callback function.
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback1);
    }

    private ResultCallback resultCallback1 = new ResultCallback() {
        @Override
        public void onResult(Result result) {
        }
    };

    @Override // DataApi.DataListener
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {

                String path = event.getDataItem().getUri().getPath();

                if (path.equals("/ADDNEWDATA_PATH")) {      // Etc data
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    String date = dataMapItem.getDataMap().getString("DATE");
                    String tagname = dataMapItem.getDataMap().getString("TAGNAME");
                    String actStartTime = dataMapItem.getDataMap().getString("ACTSTARTTIME");
                    String actEndTime = dataMapItem.getDataMap().getString("ACTENDTIME");

                    // Add data in Database
                    save_etc_values(date, tagname, actStartTime, actEndTime);
                }
                else if(path.equals("/CHANGEDATA_PATH")) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    String date = dataMapItem.getDataMap().getString("DATE");
                    String todo = dataMapItem.getDataMap().getString("TODO");
                    int beforeTodocheck = dataMapItem.getDataMap().getInt("BEFORETODOCHECK");
                    int todocheck = dataMapItem.getDataMap().getInt("TODOCHECK");         // 1 -> pushed doing button, 2 -> pushed done button
                    String actstarttime = dataMapItem.getDataMap().getString("ACTSTARTTIME");
                    String actendtime = dataMapItem.getDataMap().getString("ACTENDTIME");

                    if(beforeTodocheck == 1 && todocheck == 1) {    // CheckBox == 1 (data is changed once) & push doing button (1->1)
                        String tagname = getTagName(todo);
                        save_extra_doing_values(date, tagname, todo, actstarttime, actendtime);
                    }
                    else if(beforeTodocheck == 1 && todocheck == 2) {     // CheckBox == 1 (data is changed once) & push done button (1->2)
                        String tagname = getTagName(todo);
                        save_extra_doing_values(date, tagname, todo, actstarttime, actendtime);

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("TODOCHECK", todocheck);
                        db.update("TodoDatabase", contentValues, "DATE = ? AND TODO = ? AND TODOCHECK = ?",new String[] { date, todo, Integer.toString(1) });
                    }
                    else {          // 0->1 or 0->2
                        // Change Database data.
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("TODOCHECK", todocheck);
                        contentValues.put("ACTSTARTTIME", actstarttime);
                        contentValues.put("ACTENDTIME", actendtime);
                        db.update("TodoDatabase", contentValues, "DATE = ? AND TODO = ?",new String[] { date, todo });
                    }
                }
                else if(path.equals("/CHANGEMEMO_PATH")) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    String date = dataMapItem.getDataMap().getString("DATE");
                    String todo = dataMapItem.getDataMap().getString("TODO");

                    // Change Database data.
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("TODOCHECK", 7);
                    db.update("TodoDatabase", contentValues, "DATE = ? AND TODO = ?",new String[] { date, todo });
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) { }
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

    private void save_extra_doing_values(String date, String tagname, String todo, String actStartTime, String actEndTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sqlInsert = ContactDBCtrct.SQL_INSERT +
                " ('" +
                date + "', " +
                "'" + tagname + "', " +
                "'" + todo + "', " +
                "5" + ", " +                   // Extra's todocheck is 5.
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
                "'" + tagname + "', " +         // Etc data's todoname is same as tagname
                "4" + ", " +                   // Etc todocheck is 4
                "'" + "None" + "', " +
                "'" + "None" + "', " +
                "'" + actStartTime + "', " +
                "'" + actEndTime + "')";

        db.execSQL(sqlInsert);
    }
}
