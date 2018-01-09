package wearable.example.com.datalayerexamplefromweartophone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView mTextView; // 모바일로 전송 할 텍스트뷰
    private String mSendingText; // 모바일로 전송 할 텍스트

    private GoogleApiClient mGoogleApiClient; // 구글 플레이 서비스 API 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 모바일로 전송 할 텍스트뷰
        mTextView = (TextView) findViewById(R.id.text);

        // 구글 플레이 서비스 객체를 시계 설정으로 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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

    // 구글 플레이 서비스에 접속 됐을 때 실행
    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
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
    }

    // Send Message 버튼을 클릭했을 때 실행
    public void onSendMessage(View view) {

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
                            String message = "Message : " + mTextView.getText();
                            byte[] bytes = message.getBytes();

                            // 메시지 전송 및 전송 후 실행 될 콜백 함수 지정
                            Wearable.MessageApi.sendMessage(mGoogleApiClient,
                                    node.getId(), "/MESSAGE_PATH", bytes)
                                    .setResultCallback(resultCallback);
                        }
                    }
                });
    }

    // 모바일로 데이터 및 메시지를 전송 후 실행되는 메소드
    private ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void onResult(Result result) {
            String resultString = "Sending Result : " + result.getStatus().isSuccess();

            Toast.makeText(getApplication(), resultString, Toast.LENGTH_SHORT).show();
        }
    };

    // 데이터 전송 횟수이다.
    // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
    private int sendCount = 0;

    // Send Data String 버튼을 클릭했을 때 실행
    public void onSendDataString(View view) {
        // 전송할 텍스트를 생성한다.
        String text = "String Data : " + mTextView.getText().toString();

        // 모바일로 전송할 데이터 묶음인 데이터 맵을 생성한다.
        PutDataMapRequest dataMap = PutDataMapRequest.create("/STRING_DATA_PATH");

        // 전송할 텍스트를 지정한다.
        dataMap.getDataMap().putString("sendString", text);

        // 현재 보내는 텍스트와 지난번 보냈던 텍스트가 같으면
        // onDataChanged() 메소드가 실행되지 않는다.
        // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
        dataMap.getDataMap().putInt("count", sendCount++);

        // 데이터 맵으로 전송할 요청 객체를 생성한다.
        PutDataRequest request = dataMap.asPutDataRequest();

        // 데이터 전송 및 전송 후 실행 될 콜백 함수 지정
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(resultCallback);
    }
}
