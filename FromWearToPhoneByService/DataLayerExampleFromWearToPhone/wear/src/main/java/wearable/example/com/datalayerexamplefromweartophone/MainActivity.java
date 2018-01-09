package wearable.example.com.datalayerexamplefromweartophone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        NodeApi.NodeListener,
        MessageApi.MessageListener,
        DataApi.DataListener {

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

    // 구글 플레이 서비스에 접속 됐을 때 실행
    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        // 노드, 메시지, 데이터 이벤트를 활용할 수 있도록 이벤트 리스너 지정
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
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

        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
    }

    // Send Message 버튼을 클릭했을 때 실행 -> 간단한 메시지를 보낼때 사용
    public void onSendMessage(View view) {
        Toast.makeText(getApplicationContext(), "send!", Toast.LENGTH_LONG).show();

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
                            String message = mTextView.getText().toString();
                            byte[] bytes = message.getBytes();

                            // 메시지 전송 및 전송 후 실행 될 콜백 함수 지정
                            Wearable.MessageApi.sendMessage(mGoogleApiClient,
                                    node.getId(), "/TODOLIST_PATH", bytes)
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

            //Toast.makeText(getApplication(), resultString, Toast.LENGTH_SHORT).show();
        }
    };

    // 데이터 전송 횟수이다.
    // 텍스트가 같더라도 데이터가 계속 변할 수 있도록 count 값을 같이 보낸다.
    private int sendCount = 0;

    /*
    // Send Data String 버튼을 클릭했을 때 실행 -> 여러 개의 데이터나 이미지 같은 큰 데이터를 보낼때 사용
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
    */

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // 데이터 이벤트 횟수별로 동작한다.
        for (DataEvent event : dataEvents) {

            // 데이터 변경 이벤트일 때 실행된다.
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // 동작을 구분할 패스를 가져온다.
                String path = event.getDataItem().getUri().getPath();

                // 패스가 문자 데이터 일 때
                if (path.equals("/STRING_DATA_PATH")) {
                    // 이벤트 객체로부터 데이터 맵을 가져온다.
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    // 데이터맵으로부터 수신한 문자열을 가져온다.
                    final String receiveString = dataMapItem.getDataMap().getString("sendString");
                    final String receiveString2 = dataMapItem.getDataMap().getString("sendString2");
                    final String receiveString3 = dataMapItem.getDataMap().getString("sendString3");

                    final TextView receive1 = (TextView) findViewById(R.id.receive1);
                    final TextView receive2 = (TextView) findViewById(R.id.receive2);
                    final TextView receive3 = (TextView) findViewById(R.id.receive3);

                    // UI 스레드를 실행하여 텍스트 뷰의 값을 수정한다.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            receive1.setText(receiveString);
                            receive2.setText(receiveString2);
                            receive3.setText(receiveString3);
                        }
                    });
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

    @Override
    public void onPeerConnected(Node node) {
        Toast.makeText(this, "Peer Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Toast.makeText(this, "Peer Disconnected", Toast.LENGTH_SHORT).show();
    }
}
