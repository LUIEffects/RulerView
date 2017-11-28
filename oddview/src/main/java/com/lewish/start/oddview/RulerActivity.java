package com.lewish.start.oddview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class RulerActivity extends AppCompatActivity {
    private VerticalRulerView mRv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);
        mRv = (VerticalRulerView)findViewById(R.id.mRv);
        mRv.setOnChooseResulterListener(new VerticalRulerView.OnChooseResulterListener() {
            @Override
            public void onEndResult(String result) {
                Toast.makeText(RulerActivity.this, result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScrollResult(String result) {

            }
        });
    }
}
