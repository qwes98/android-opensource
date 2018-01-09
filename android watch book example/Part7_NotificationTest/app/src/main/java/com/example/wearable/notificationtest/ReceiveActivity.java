package com.example.wearable.notificationtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;

import android.support.v4.app.RemoteInput;

public class ReceiveActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        // 알림에서 호출한 인텐트를 가져온다.
        Intent intent = this.getIntent();

        // 인텐트에 포함된 값을 가져온다.
        String extraMessage = intent.getStringExtra(MyActivity.EXTRA_RESULT_KEY);

        // 알림에서 전달된 음성 인식 결과를 가져온다.
        CharSequence voiceMessage = getMessageText(intent);

        // 레이아웃의 텍스트뷸 가져온다.
        TextView textView = (TextView) findViewById(R.id.receiveText);

        // 레이아웃에 출력할 텍스트를 생성한다.
        String outputText = "ExtraMessage : " + extraMessage + "\n"
                          + "VoiceMessage : " + voiceMessage;

        // 텍스트를 텍스트뷰에 출력한다.
        textView.setText(outputText);
    }

    /**
     * 시계의 음성 인식 텍스트를 반환한다.
     */
    private CharSequence getMessageText(Intent intent) {
        // 인텐트로부터 결과 값을 가져온다.
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        // 인텐트 결과값이 있는 경우 음성을 가져온다.
        if (remoteInput != null) {
            return remoteInput.getCharSequence(MyActivity.VOICE_RESULT_KEY);
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.receive, menu);
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
}
