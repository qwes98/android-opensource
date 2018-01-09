package wearable.example.com.datalayerexamplefromweartophone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView mTextView; // 텍스트를 출력할 뷰


    @Override // Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        Intent intent = new Intent(getApplicationContext(), MainService.class);  // 중요!!!
        startService(intent);   // 중요!!!

    }
}
