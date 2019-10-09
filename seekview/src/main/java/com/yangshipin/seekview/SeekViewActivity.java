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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekview_test);
        seekView = (SeekView) findViewById(R.id.mSeekView);
        /**
         * 准备数据
         */
        SeekViewDataObj seekViewDataObj = new SeekViewDataObj();
        List<SeekViewDataObj.ScaleMsgObj> list = new ArrayList<>();
        mTv = (TextView) findViewById(R.id.mTv);
//        list.add(new SeekViewDataObj.ScaleMsgObj(6, "1:00", "舒克和贝塔"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(13, "2:10", "猫和老鼠"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(20, "3:10", "海绵宝宝"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(31, "5:00", "早上新闻"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(43, "7:00", "洗脑新闻"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(49, "8:00", "破冰行动"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(82, "13:30", "午休结束"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(93, "15:20", "下午茶"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(114, "18:50", "晚间新闻"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(127, "22:00", "走进科学"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(139, "23:00", "午夜凶铃"));


        //2019 10 9
        list.add(new SeekViewDataObj.ScaleMsgObj(1570554000, "1:00", "舒克和贝塔"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570558200, "2:10", "猫和老鼠"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570561800, "3:10", "海绵宝宝"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570568400, "5:00", "早上新闻"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570575600, "7:00", "洗脑新闻"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570579200, "8:00", "破冰行动"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570599000, "13:30", "午休结束"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570605600, "15:20", "下午茶"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570618200, "18:50", "晚间新闻"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570629600, "22:00", "走进科学"));
        list.add(new SeekViewDataObj.ScaleMsgObj(1570633200, "23:00", "午夜凶铃"));

        seekViewDataObj.setScaleMsgObjList(list);
        seekViewDataObj.setPlayBackStart(1570550400);
        seekViewDataObj.setPlayBackTime(1570575600);
        seekViewDataObj.setPlayEndTime(1570644000);
        seekView.refreshData(seekViewDataObj);
        seekView.setOnInteractListener(new SeekView.OnInteractListener() {
            @Override
            public void onProgressUpdate(long progress) {
                mTv.setText("" + progress);
            }
        });
    }
}
