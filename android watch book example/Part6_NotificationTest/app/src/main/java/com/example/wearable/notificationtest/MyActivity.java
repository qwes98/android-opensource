package com.example.wearable.notificationtest;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MyActivity extends Activity {

    static final int BASIC_NOTIFICATION_ID = 0; // 기본 알림 일련번호
    static final int ACTION_NOTIFICATION_ID = 1; // 액션 알림 일련번호
    static final int BIG_PICTURE_NOTIFICATION_ID = 3; // 큰 사진 알림 일련번호
    static final int BIG_TEXT_NOTIFICATION_ID = 4; // 큰 글자 알림 일련번호
    static final int INBOX_NOTIFICATION_ID = 5; // 인박스 알림 일련번호

    static final int GROUP_NOTIFICATION1_ID = 6; // 그룹 알림1 일련번호
    static final int GROUP_NOTIFICATION2_ID = 7; // 그룹 알림2 일련번호
    static final int GROUP_NOTIFICATION3_ID = 8; // 그룹 알림3 일련번호
    final static String GROUP_KEY = "group_key";  // 그룹 키

    static final int SUMMARY_NOTIFICATION_ID = 9; // 요약 알림 일련번호
    static final int PAGE_NOTIFICATION_ID = 10; // 페이지 알림 일련번호
    static final int BACKGROUND_NOTIFICATION_ID = 11; // 배경 알림 일련번호
    static final int ICON_NOTIFICATION_ID = 12; // 아이콘 알림 일련번호
    static final int GRAVITY_NOTIFICATION_ID = 13; // 정렬 알림 일련번호
    static final int CONTENT_ACTION_NOTIFICATION_ID = 14; // 컨텐트 액션 알림 일련번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Basic Notification 버튼을 누르면 실행되는 메소드이다.
    public void showBasicNotification(View view) {

        // 큰 아이콘을 리소스로부터 가져온다.
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_action_accept);

        // 한시간 후 시간을 지정한다.
        long afterOneHour = System.currentTimeMillis() + 3600000;

        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("BasicNotificationTitle")  // 타이틀
                .setContentText("BasicNotificationText")    // 메인텍스트
                .setSubText("SubText")                        // 서브텍스트
                .setTicker("TickerText")                     // 티커텍스트
                .setUsesChronometer(true)                     // 알림 발생 후 시간 보이기
                .setWhen(afterOneHour)                        // 한시간 후를 표시
                .setNumber(100)                                // 숫자 표시
                .setContentInfo("ContentInfo")               // 우측 하단 텍스트
                .setSmallIcon(R.drawable.ic_action_call)     // 작은 아이콘 설정
                .setLargeIcon(largeIcon)                       // 큰 아이콘 설정
                .build();

        // 알림 메니저 객체를 생성한다.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // 알림을 보인다.
        notificationManager.notify(BASIC_NOTIFICATION_ID, notification);
    }

    // Action Notification 버튼을 누르면 실행되는 메소드이다.
    public void showActionNotification(View view) {

        // 액티비티 인텐트 생성
        Intent viewIntent = new Intent(this, MyActivity.class);

        // 액션을 실행했을 때까지 대기할 팬딩인텐트 생성
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, ACTION_NOTIFICATION_ID, viewIntent, 0);

        // 알림(Notification)을 생성하며, 액션을 추가한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("ActionNotificationTitle")
                .setContentText("ActionNotificationText")
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(R.drawable.ic_action_call, "Call", viewPendingIntent)
                .addAction(R.drawable.ic_action_cut, "Cut", viewPendingIntent)
                .addAction(R.drawable.ic_action_accept, "Accept", viewPendingIntent)
                .setContentIntent(viewPendingIntent)
                .setAutoCancel(true)
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this)
                .notify(ACTION_NOTIFICATION_ID, notification);
    }

    // BIg Picture Notification 버튼을 누르면 실행되는 메소드이다.
    public void showBigPictureNotification(View view) {

        // 리소스로부터 사진을 가져온다.
        Bitmap bigPicture = BitmapFactory.decodeResource(getResources(),
                R.drawable.example_big_picture);

        // 알림을 위한 큰 사진 스타일을 생성한다.
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.bigPicture(bigPicture);                    // 리소스의 사진을 적용한다.
        style.setBigContentTitle("BigContentTitle");  // 사진을 펼쳤을 때의 타이틀 적용
        style.setSummaryText("SummaryText");           // 사진을 펼쳤을 때이 텍스트

        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Title")                  // 타이틀
                .setContentText("Text")                    // 메인텍스트
                .setSmallIcon(R.drawable.ic_action_call)  // 작은 아이콘 설정
                .setStyle(style)                            // 스타일 적용
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(BIG_PICTURE_NOTIFICATION_ID, notification);
    }

    // BIg Text Notification 버튼을 누르면 실행되는 메소드이다.
    public void showBigTextNotification(View view) {

        // 타이틀을 스타일을 적용하여 생성한다.
        SpannableStringBuilder title = new SpannableStringBuilder();
        title.append("Stylized Title");
        title.setSpan(new RelativeSizeSpan(1.25f), 0, 8, 0); // Styliezed 크게 키우기
        title.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, 8, 0); // Styliezed 굵게기울기

        // 텍스트를 스타일을 적용하여 생성한다.
        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append("Stylized Text");
        text.setSpan(new RelativeSizeSpan(1.25f), 0, 8, 0); // Styliezed 크게 키우기
        text.setSpan(new ForegroundColorSpan(Color.RED), 0, 3, 0);   //Sty 빨간색
        text.setSpan(new ForegroundColorSpan(Color.GREEN), 3, 6, 0); //liz 녹색
        text.setSpan(new ForegroundColorSpan(Color.BLUE), 6, 8, 0);  //ed 파란색

        // 알림을 위한 큰 글자(Big Text) 스타일을 생성한다.
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title); //타이틀을 적용한다.
        style.bigText(text);             //내용을 적용한다.

        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Title")                  // 타이틀
                .setContentText("Text")                    // 메인텍스트
                .setSmallIcon(R.drawable.ic_action_call)  // 작은 아이콘 설정
                .setStyle(style)                            // 스타일 적용
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(BIG_TEXT_NOTIFICATION_ID, notification);
    }

    // Inbox Notification 버튼을 누르면 실행되는 메소드이다.
    public void showInboxNotification(View view) {

        // 알림을 위한 인박스(Inbox) 스타일을 생성한다.
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.addLine("Inbox Style Text Example Line 1"); // 첫 번째 라인
        style.addLine("Inbox Style Text Example Line 2"); // 두 번째 라인
        style.addLine("Inbox Style Text Example Line 3"); // 세 번째 라인
        style.setBigContentTitle("Inbox Title");             // 인박스 타이틀
        style.setSummaryText("Inbox Text");                  // 인박스 텍스트

        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Title")                  // 타이틀
                .setContentText("Text")                    // 메인텍스트
                .setSmallIcon(R.drawable.ic_action_call)  // 작은 아이콘 설정
                .setStyle(style)                            // 스타일 적용
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this)
                .notify(INBOX_NOTIFICATION_ID, notification);
    }

    // Group Notification 1 버튼을 누르면 실행되는 메소드이다.
    public void showGroupNotification1(View view) {

        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Group Title 1")         // 타이틀
                .setContentText("Group Text 1")           // 메인텍스트
                .setSmallIcon(R.drawable.ic_action_call)  // 작은 아이콘 설정
                .setGroup(GROUP_KEY)                      // 그룹 키 지정
                .setSortKey("1")                           // 그룹 정렬
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this)
                .notify(GROUP_NOTIFICATION1_ID, notification);
    }

    // Group Notification 2 버튼을 누르면 실행되는 메소드이다.
    public void showGroupNotification2(View view) {

        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Group Title 2")         // 타이틀
                .setContentText("Group Text 2")           // 메인텍스트
                .setSmallIcon(R.drawable.ic_action_call)  // 작은 아이콘 설정
                .setGroup(GROUP_KEY)                      // 그룹 키 지정
                .setSortKey("2")                           // 그룹 정렬
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(GROUP_NOTIFICATION2_ID, notification);
    }

    // Group Notification 3 버튼을 누르면 실행되는 메소드이다.
    public void showGroupNotification3(View view) {

        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Group Title 3")         // 타이틀
                .setContentText("Group Text 3")           // 메인텍스트
                .setSmallIcon(R.drawable.ic_action_call)  // 작은 아이콘 설정
                .setGroup(GROUP_KEY)                      // 그룹 키 지정
                .setSortKey("3")                           // 그룹 정렬
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(GROUP_NOTIFICATION3_ID, notification);
    }

    // Summary Notification 버튼을 누르면 실행되는 메소드이다.
    public void showSummaryNotification(View view) {

        // 알림을 위한 인박스(Inbox) 스타일을 생성한다.
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.addLine("Group Text 1"); // 첫 번째 라인
        style.addLine("Group Text 2"); // 두 번째 라인
        style.addLine("Group Text 3"); // 세 번째 라인
        style.setBigContentTitle("Summary Title");             // 인박스 타이틀
        style.setSummaryText("Summary Text");                  // 인박스 텍스트

        // 알림(Notification)을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Title")                  // 타이틀
                .setContentText("Text")                    // 메인텍스트
                .setSmallIcon(R.drawable.ic_action_call)  // 작은 아이콘 설정
                .setStyle(style)                            // 스타일 적용
                .setGroup(GROUP_KEY)                      // 그룹 키 지정
                .setGroupSummary(true)                     // 그룹 요약 설정
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this)
                .notify(SUMMARY_NOTIFICATION_ID, notification);
    }

    // Page Notification 버튼을 누르면 실행되는 메소드이다.
    public void showPageNotification(View view) {

        // 두 번째 페이지 웨어러블 옵션을 생성한다.
        NotificationCompat.WearableExtender secondWearableExtender =
                new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.END);

        // 두 번째 페이지를 생성한다.
        Notification secondPage = new NotificationCompat.Builder(this)
                .setContentTitle("Second Page Title")
                .setContentText("Second Page Text")
                .extend(secondWearableExtender)
                .build();

        // 세 번째 페이지를 생성한다.
        Notification thirdPage = new NotificationCompat.Builder(this)
                .setContentTitle("Third Page Title")
                .setContentText("Third Page Text")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.END))
                .build();

        // 첫 번째 페이지의 웨어러블 옵션 객체를 생성한다.
        // 두 번째, 세 번째 페이지를 추가한다.
        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.END)
                        .addPage(secondPage)
                        .addPage(thirdPage);

        // 웨어러블 옵션을 적용한 알림을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Page Title")
                .setContentText("Page Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(PAGE_NOTIFICATION_ID, notification);
    }

    // Background Notification 버튼을 누르면 실행되는 메소드이다.
    public void showBackgroundNotification(View view) {

        // 리소스로부터 사진을 가져온다.
        Bitmap background1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_1);
        Bitmap background2 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_2);
        Bitmap background3 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_3);

        // 두 번째 페이지를 생성한다.
        Notification secondPage = new NotificationCompat.Builder(this)
                .setContentTitle("Background2 Title")
                .setContentText("setHintShowBackgroundOnly(false)")
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(background2)
                        .setHintShowBackgroundOnly(false))
                .build();

        // 세 번째 페이지를 생성한다.
        Notification thirdPage = new NotificationCompat.Builder(this)
                .setContentTitle("Background3 Title")
                .setContentText("setHintShowBackgroundOnly(true)")
                .extend(new NotificationCompat.WearableExtender()
                        .setBackground(background3)
                        .setHintShowBackgroundOnly(true))
                .build();

        // 첫 번째 페이지의 웨어러블 옵션 객체를 생성한다.
        // 두 번째, 세 번째 페이지를 추가한다.
        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender()
                        .setBackground(background1)
                        .addPage(secondPage)
                        .addPage(thirdPage);

        // 웨어러블 옵션을 적용한 알림을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Background1 Title")
                .setContentText("Background1 Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(BACKGROUND_NOTIFICATION_ID, notification);
    }

    // Icon Notification 버튼을 누르면 실행되는 메소드이다.
    public void showIconNotification(View view) {

        // 두 번째 페이지를 생성한다.
        Notification secondPage = new NotificationCompat.Builder(this)
                .setContentTitle("IconGravity Title")
                .setContentText("Gravity.START")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.START))
                .build();

        // 세 번째 페이지를 생성한다.
        Notification thirdPage = new NotificationCompat.Builder(this)
                .setContentTitle("IconGravity Title")
                .setContentText("Gravity.END")
                .extend(new NotificationCompat.WearableExtender()
                        .setContentIcon(R.drawable.ic_launcher)
                        .setContentIconGravity(Gravity.END))
                .build();

        // 첫 번째 페이지의 웨어러블 옵션 객체를 생성한다.
        // 두 번째, 세 번째 페이지를 추가한다.
        // 우측 상단 힌트 아이콘을 숨긴다.
        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .addPage(secondPage)
                        .addPage(thirdPage);

        // 웨어러블 옵션을 적용한 알림을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Icon Title")
                .setContentText("Icon Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(ICON_NOTIFICATION_ID, notification);
    }

    // Gravity Notification 버튼을 누르면 실행되는 메소드이다.
    public void showGravityNotification(View view) {

        // 두 번째 페이지를 생성한다.
        Notification secondPage = new NotificationCompat.Builder(this)
                .setContentTitle("Gravity Title")
                .setContentText("Gravity.CENTER_VERTICAL")
                .extend(new NotificationCompat.WearableExtender().setGravity(Gravity.CENTER_VERTICAL))
                .build();

        // 세 번째 페이지를 생성한다.
        Notification thirdPage = new NotificationCompat.Builder(this)
                .setContentTitle("Gravity Title")
                .setContentText("Gravity.BOTTOM")
                .extend(new NotificationCompat.WearableExtender().setGravity(Gravity.BOTTOM))
                .build();

        // 첫 번째 페이지의 웨어러블 옵션 객체를 생성한다.
        // 두 번째, 세 번째 페이지를 추가한다.
        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender()
                        .setGravity(Gravity.TOP)
                        .addPage(secondPage)
                        .addPage(thirdPage);

        // 웨어러블 옵션을 적용한 알림을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Gravity Title")
                .setContentText("Gravity.TOP")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(GRAVITY_NOTIFICATION_ID, notification);
    }

    // Content Action Notification 버튼을 누르면 실행되는 메소드이다.
    public void showContentActionNotification(View view) {

        // 액티비티 인텐트 생성
        Intent viewIntent = new Intent(this, MyActivity.class);

        // 액션을 실행했을 때까지 대기할 팬딩인텐트 생성
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, ACTION_NOTIFICATION_ID, viewIntent, 0);

        // 첫 번째 액션을 생성한다.
        NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(
                R.drawable.ic_launcher, "Action1", viewPendingIntent).build();

        // 두 번째 액션을 생성한다.
        NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(
                R.drawable.ic_launcher, "Action2", viewPendingIntent).build();

        // 두 번째 페이지를 생성한다.
        Notification secondPage = new NotificationCompat.Builder(this)
                .setContentTitle("Action 1 Title")
                .setContentText("Action 1 Text")
                .extend(new NotificationCompat.WearableExtender().setContentAction(0))
                .build();

        // 세 번째 페이지를 생성한다.
        Notification thirdPage = new NotificationCompat.Builder(this)
                .setContentTitle("Action 2 Title")
                .setContentText("Action 2 Text")
                .extend(new NotificationCompat.WearableExtender().setContentAction(1))
                .build();

        // 첫 번째 페이지의 웨어러블 옵션 객체를 생성한다.
        // 두 번째, 세 번째 페이지를 추가한다.
        // 우측 상단 힌트 아이콘을 숨겼다.
        NotificationCompat.WearableExtender wearableOptions =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .addAction(action1) // action[0]
                        .addAction(action2) // action[1]
                        .addAction(action2) // action[2]
                        .addPage(secondPage)
                        .addPage(thirdPage)
                        .setContentAction(2);

        // 웨어러블 옵션을 적용한 알림을 생성한다.
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Content Action Title")
                .setContentText("Content Action Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .extend(wearableOptions)
                .build();

        // 알림 메니저 객체를 생성하고 실행한다.
        NotificationManagerCompat.from(this).notify(CONTENT_ACTION_NOTIFICATION_ID, notification);
    }
}
