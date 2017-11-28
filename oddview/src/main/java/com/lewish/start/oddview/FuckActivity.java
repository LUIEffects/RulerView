package com.lewish.start.oddview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FuckActivity extends AppCompatActivity {
    private TextView mTv;
    private Button mBtnSX;
    private Button mBtnD;
    private OddView mOddView;
    private Boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuck);
        mOddView = (OddView) findViewById(R.id.mOddView);
        mTv = (TextView) findViewById(R.id.mTv);
        findViewById(R.id.mBtnSX).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTv.setVisibility(mTv.getVisibility()!=View.GONE?View.GONE:View.VISIBLE);
            }
        });
        findViewById(R.id.mBtnD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOddView.setOffset(10f,10000);
            }
        });
    }
}
