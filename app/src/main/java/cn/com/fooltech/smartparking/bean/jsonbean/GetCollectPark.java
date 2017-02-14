package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.ParkCollectInfo;

/**
 * Created by YY on 2016/7/26.
 */
public class GetCollectPark {
    private int code;
    private List<ParkCollectInfo> content;

    @Override
    public String toString() {
        return "GetCollectPark{" +
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

    public List<ParkCollectInfo> getContent() {
        return content;
    }

    public void setContent(List<ParkCollectInfo> content) {
        this.content = content;
    }
}
