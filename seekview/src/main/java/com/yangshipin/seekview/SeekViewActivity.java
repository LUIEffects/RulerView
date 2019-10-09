package com.yangshipin.seekview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeekViewActivity extends AppCompatActivity {
    private static final String TAG = "SeekViewActivity";
    private SeeBackView seekView;
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekview_test);
        seekView = (SeeBackView) findViewById(R.id.mSeekView);
        /**
         * 准备数据
         */
        final SeekViewDataObj seekViewDataObj = new SeekViewDataObj();
        final List<SeekViewDataObj.ScaleMsgObj> list = new ArrayList<>();
        mTv = (TextView) findViewById(R.id.mTv);
        findViewById(R.id.mBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekView.setVisibility(seekView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                seekView.refreshData(seekViewDataObj);
            }
        });

        //2019 10 9
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570554000, "1:00", "舒克和贝塔"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570558200, "2:10", "猫和老鼠"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570561800, "3:10", "海绵宝宝"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570568400, "5:00", "早上新闻"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570575600, "7:00", "洗脑新闻"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570579200, "8:00", "破冰行动"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570599000, "13:30", "午休结束"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570605600, "15:20", "下午茶"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570618200, "18:50", "晚间新闻"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570629600, "22:00", "走进科学"));
//        list.add(new SeekViewDataObj.ScaleMsgObj(1570633200, "23:00", "午夜凶铃"));

//        seekViewDataObj.setScaleMsgObjList(list);
//        seekViewDataObj.setPlayBackStart(1570550400);
//        seekViewDataObj.setPlayBackTime(1570575600);
//        seekViewDataObj.setPlayEndTime(1570644000);

        ;
        Log.d(TAG, "date: "+stampToDate("1570633200000"));

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
        seekView.refreshData(seekViewDataObj);
        seekView.setOnInteractListener(new SeeBackView.OnInteractListener() {
            @Override
            public void onProgressUpdate(long progress) {
                mTv.setText("" + progress);
            }
        });
    }

    public static String stampToDate(String s){
        String ss;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        long t = Long.valueOf(s);
        Date date = new Date(t);
        ss = simpleDateFormat.format(date);
        return ss;
    }
}
