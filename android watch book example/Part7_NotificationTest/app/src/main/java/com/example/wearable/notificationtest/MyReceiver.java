package com.example.wearable.notificationtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 인텐트에 포함된 값을 가져온다.
        String extraMessage = intent.getStringExtra(MyActivity.EXTRA_RESULT_KEY);

        // 알림에서 전달된 음성 인식 결과를 가져온다.
        CharSequence voiceMessage = getMessageText(intent);

        // 출력 할 값을 지정한다.
        String outputText = "ExtraMessage : " + extraMessage + "\n"
                          + "VoiceMessage : " + voiceMessage;

        // 토스트로 메시지를 출력한다.
        Toast.makeText(context, outputText, Toast.LENGTH_LONG).show();
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
}
