package com.example.wearable.wearableapptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import android.support.wearable.view.DelayedConfirmationView;
import android.widget.Toast;

import java.util.List;

import android.speech.RecognizerIntent;

public class MyActivity extends Activity {

    private TextView mTextView;

    private GestureDetectorCompat mGestureDetector;
    private DismissOverlayView mDismissOverlayView;

    private DelayedConfirmationView delayedConfirmationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // activity_my 레이아웃을 소스 컨텐트 뷰로 설정한다.
        setContentView(R.layout.activity_my);

        // WatchViewStub 클래스로 stub 객체를 생성한다.
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        // stub 객체가 화면의 종류를 검증한 다음 발생할 이벤트를 설정한다.
        stub.setOnLayoutInflatedListener(
                new WatchViewStub.OnLayoutInflatedListener() {
                    @Override
                    public void onLayoutInflated(WatchViewStub stub) {
                        mTextView = (TextView) stub.findViewById(R.id.text);
                    }
                });

        // 종료 오버레이 뷰를 지정
        mDismissOverlayView = (DismissOverlayView) findViewById(R.id.dismiss_overlay);

        // 시계의 터치로 제스쳐를 감지한다.
        mGestureDetector = new GestureDetectorCompat(this, new LongPressListener());

        // 지연 확인 뷰를 지정
        delayedConfirmationView = (DelayedConfirmationView) findViewById(R.id.delayed_confirmation);

        // 지연 확인 뷰의 시간을 지정 (1000ms = 1초)
        delayedConfirmationView.setTotalTimeMs(1 * 1000);
    }

    // Success Activity 버튼을 눌렀을 때 실행되는 메소드
    public void onSuccessActivity(View view) {
        // 확인 액티비티 인텐트 생성
        Intent intent = new Intent(this, ConfirmationActivity.class);

        // 확인 액티비티 인텐트 종류 및 메시지 입력
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Success Animation!");

        // 확인 액티비티 실행
        startActivity(intent);
    }

    // Failure Activity 버튼을 눌렀을 때 실행되는 메소드
    public void onFailureActivity(View view) {
        // 확인 액티비티 인텐트 생성
        Intent intent = new Intent(this, ConfirmationActivity.class);

        // 확인 액티비티 인텐트 종류 및 메시지 입력
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Failure Animation!");

        // 확인 액티비티 실행
        startActivity(intent);
    }

    // Open On Phone Activity 버튼을 눌렀을 때 실행되는 메소드
    public void onOpenOnPhoneActivity(View view) {
        // 확인 액티비티 인텐트 생성
        Intent intent = new Intent(this, ConfirmationActivity.class);

        // 확인 액티비티 인텐트 종류 및 메시지 입력
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Phone Animation!");

        // 확인 액티비티 실행
        startActivity(intent);
    }

    // 음성 인식 요청 코드
    private static final int SPEECH_REQUEST_CODE = 0;

    // Voice Recognize Activity 버튼을 눌렀을 때 실행되는 메소드
    public void onVoiceRecognize(View view) {

        // 음성 인식 액티비티 인텐트 생성
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // 음성 인식 액티비티에 음성 인식 속성을 입력한다.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // 음성 인식 액티비티를 실행한다.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // 음성 인식이 완료된 후 실행되는 메소드이다.
    @Override // Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 요청 코드가 SPEECH_REQUEST_CODE이고, 음성인식을 성공한 경우 실행
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            // 음성으로 입력된 결과 리스트를 가져온다.
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            // 시계에서는 하나의 입력이 가능하므로 첫 번째 결과값을 가져온다.
            String spokenText = results.get(0);

            // 음성 인식된 결과를 텍스트 뷰의 텍스트로 입력한다.
            mTextView.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 액티비티의 터치 이벤트가 발생하면 제스처 디텍터로 터치 정보를 전달한다.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event) || super.dispatchTouchEvent(event);
    }

    // 제스처 디텍터가 오래 누르고 있는 이벤트를 감지하면
    // 종료 오버레이 뷰를 실행한다.
    private class LongPressListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent event) {
            mDismissOverlayView.show();
        }
    }

    // Start Timer 버튼을 누르면 실행되는 메소드
    public void onStartTimer(View view) {

        // 뷰의 타이머를 시작
        delayedConfirmationView.start();

        // 뷰의 타이머 관련 이벤트 리스너를 지정
        delayedConfirmationView.setListener(
            new DelayedConfirmationView.DelayedConfirmationListener() {

                // 타이머가 종료되면 실행되는 메소드
                @Override
                public void onTimerFinished(View view) {
                    // 완료 후 발생할 확인 액티비티 인텐트 생성
                    Intent intent = new Intent(getApplication(), ConfirmationActivity.class);

                    // 에니메이션 타입 및 메시지 입력
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.SUCCESS_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Succeesed!");

                    // 액티비티 실행
                    startActivity(intent);
                }

                // 지연 확인 뷰가 선택되면 실행되는 메소드
                @Override
                public void onTimerSelected(View view) {
                    // 메시지 발생
                    Toast.makeText(getApplication(), "Timer Selected!", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
