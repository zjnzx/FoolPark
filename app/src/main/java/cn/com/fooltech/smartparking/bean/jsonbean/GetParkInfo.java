package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.ArrayList;

import cn.com.fooltech.smartparking.bean.ParkInfo;

/**
 * Created by YY on 2016/8/4.
 */
public class GetParkInfo {
    private int code;
    private ArrayList<ParkInfo> content;

    @Override
    public String toString() {
        return "GetParkInfo{" +
                "code=" + code +
                ", content=" + content +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<ParkInfo> getContent() {
        return content;
    }

    public void setContent(ArrayList<ParkInfo> content) {
        this.content = content;
    }
}
