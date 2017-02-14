package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.PositionRecordInfo;

/**
 * Created by YY on 2016/8/11.
 */
public class GetPositionRecord {
    private int code;
    private List<PositionRecordInfo> content;

    @Override
    public String toString() {
        return "GetPositionRecord{" +
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

    public List<PositionRecordInfo> getContent() {
        return content;
    }

    public void setContent(List<PositionRecordInfo> content) {
        this.content = content;
    }
}
