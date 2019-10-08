package com.yangshipin.seekview;

import java.util.ArrayList;
import java.util.List;

public class SeekViewDataObj {
    private List<ScaleMsgObj> scaleMsgObjList = new ArrayList<>();
    private long playBackStart;
    private long playBackTime;

    public List<ScaleMsgObj> getScaleMsgObjList() {
        return scaleMsgObjList;
    }

    public void setScaleMsgObjList(List<ScaleMsgObj> scaleMsgObjList) {
        this.scaleMsgObjList = scaleMsgObjList;
    }

    public long getPlayBackStart() {
        return playBackStart;
    }

    public void setPlayBackStart(long playBackStart) {
        this.playBackStart = playBackStart;
    }

    public long getPlayBackTime() {
        return playBackTime;
    }

    public void setPlayBackTime(long playBackTime) {
        this.playBackTime = playBackTime;
    }

    public static class ScaleMsgObj {
        public int pos;
        public String time;
        public String txt;

        public ScaleMsgObj(int pos, String time, String txt) {
            this.pos = pos;
            this.time = time;
            this.txt = txt;
        }
    }
}
