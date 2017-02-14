package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.ParkBookInfo;

/**
 * Created by YY on 2016/7/27.
 */
public class GetBookPark {
    private int code;
    private String serverTime;
    private List<ParkBookInfo> content;

    @Override
    public String toString() {
        return "GetBookPark{" +
                "code=" + code +
                ", serverTime='" + serverTime + '\'' +
                ", content=" + content +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ParkBookInfo> getContent() {
        return content;
    }

    public void setContent(List<ParkBookInfo> content) {
        this.content = content;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }
}
