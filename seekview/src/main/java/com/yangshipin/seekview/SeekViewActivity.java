package com.yangshipin.seekview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SeekViewActivity extends AppCompatActivity {
    private SeekView seekView;
    private TextView mTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekview_test);
        seekView = (SeekView) findViewById(R.id.mSeekView);
        mTv = (TextView) findViewById(R.id.mTv);
        seekView.setOnInteractListener(new SeekView.OnInteractListener() {
            @Override
            public void onProgressUpdate(int progress) {
                mTv.setText(""+progress);
            }
        });
    }
}
