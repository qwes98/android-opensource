package com.example.wearable.notificationtest;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MyActivity extends Activity {

    // 4장
    static final int BASIC_NOTIFICATION_ID = 0; // 기본 알림 일련번호
    static final int ACTION_NOTIFICATION_ID = 1; // 액션 알림 일련번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 옵션 메뉴를 생성한다.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 옵션 메뉴의 아이템에 대한 이벤트를 지정한다.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Basic Notification 버튼을 누르면 실행되는 메소드이다.
    public void showBasicNotification(View view) {
        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("BasicNotificationTitle")
                .setContentText("BasicNotificationText")
                .setSmallIcon(R.drawable.ic_launcher)
                .build();

        // 알림 매니저 객체를 생성한다.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // 알림 매니저로 알림을 화면에 보인다.
        notificationManager.notify(BASIC_NOTIFICATION_ID, notification);

    }

    // Action Notification 버튼을 누르면 실행되는 메소드이다.
    public void showActionNotification(View view) {
        // 액티비티 인텐트 생성
        Intent viewIntent = new Intent(this, MyActivity.class);

        // 액션을 실행했을 때까지 대기할 팬딩인텐트 생성
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        // 알림(Notification)을 생성하며, 액션을 추가한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("ActionNotificationTitle")
                .setContentText("ActionNotificationText")
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(R.drawable.ic_launcher, "ActionText", viewPendingIntent)
                .build();

        // 알림 매니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(ACTION_NOTIFICATION_ID, notification);
    }
}
