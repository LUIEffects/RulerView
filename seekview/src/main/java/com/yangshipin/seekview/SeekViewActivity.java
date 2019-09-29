package com.yangshipin.seekview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SeekViewActivity extends AppCompatActivity {
    private SeekView seekView;
    private TextView mTv;
    private List<ScaleMsgObj> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekview_test);
        seekView = (SeekView) findViewById(R.id.mSeekView);
        mTv = (TextView) findViewById(R.id.mTv);
        list.add(new ScaleMsgObj(6,"1:00","舒克和贝塔"));
        list.add(new ScaleMsgObj(13,"2:10","猫和老鼠"));
        list.add(new ScaleMsgObj(20,"3:10","海绵宝宝"));
        list.add(new ScaleMsgObj(31,"5:00","早上新闻"));
        list.add(new ScaleMsgObj(43,"7:00","洗脑新闻"));
        list.add(new ScaleMsgObj(49,"8:00","破冰行动"));
        list.add(new ScaleMsgObj(82,"13:30","午休结束"));
        list.add(new ScaleMsgObj(93,"15:20","下午茶"));
        list.add(new ScaleMsgObj(114,"18:50","晚间新闻"));
        list.add(new ScaleMsgObj(127,"22:00","走进科学"));
        list.add(new ScaleMsgObj(139,"23:00","午夜凶铃"));
        seekView.refreshData(list);
        seekView.setOnInteractListener(new SeekView.OnInteractListener() {
            @Override
            public void onProgressUpdate(int progress) {
                mTv.setText(""+progress);
            }
        });
    }
}
