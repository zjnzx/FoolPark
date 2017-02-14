package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.FloorInfo;

/**
 * Created by YY on 2016/8/15.
 */
public class GetFloor {
    private int code;
    private List<FloorInfo> content;

    @Override
    public String toString() {
        return "GetFloor{" +
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

    public List<FloorInfo> getContent() {
        return content;
    }

    public void setContent(List<FloorInfo> content) {
        this.content = content;
    }
}
