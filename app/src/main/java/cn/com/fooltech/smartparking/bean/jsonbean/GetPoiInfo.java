package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.ArrayList;

import cn.com.fooltech.smartparking.bean.PoiInfo;

/**
 * Created by YY on 2016/8/24.
 */
public class GetPoiInfo {
    private int code;
    private ArrayList<PoiInfo> content;

    @Override
    public String toString() {
        return "GetPoiInfo{" +
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

    public ArrayList<PoiInfo> getContent() {
        return content;
    }

    public void setContent(ArrayList<PoiInfo> content) {
        this.content = content;
    }
}
