package cn.com.fooltech.smartparking.bean.jsonbean;

import cn.com.fooltech.smartparking.bean.ParkInfo;

/**
 * Created by YY on 2016/8/5.
 */
public class GetParkInfoDetail {
    private int code;
    private ParkInfo content;

    @Override
    public String toString() {
        return "GetParkInfoDetail{" +
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

    public ParkInfo getContent() {
        return content;
    }

    public void setContent(ParkInfo content) {
        this.content = content;
    }
}
