package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.PointInfo;

/**
 * Created by YY on 2016/7/26.
 */
public class GetPoint {
    private int code;
    private List<PointInfo> content;

    @Override
    public String toString() {
        return "GetPoint{" +
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

    public List<PointInfo> getContent() {
        return content;
    }

    public void setContent(List<PointInfo> content) {
        this.content = content;
    }
}
