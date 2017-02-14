package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.SpaceInfo;

/**
 * Created by YY on 2016/7/22.
 */
public class GetSpaceStatus {
    private int code;
    private List<SpaceInfo> content;

    @Override
    public String toString() {
        return "GetSpaceStatus{" +
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

    public List<SpaceInfo> getContent() {
        return content;
    }

    public void setContent(List<SpaceInfo> content) {
        this.content = content;
    }
}
