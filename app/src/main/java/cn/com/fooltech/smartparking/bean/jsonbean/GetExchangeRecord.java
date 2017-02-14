package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.ExchangeRecordInfo;

/**
 * Created by YY on 2016/8/15.
 */
public class GetExchangeRecord {
    private int code;
    private List<ExchangeRecordInfo> content;

    @Override
    public String toString() {
        return "GetExchangeRecord{" +
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

    public List<ExchangeRecordInfo> getContent() {
        return content;
    }

    public void setContent(List<ExchangeRecordInfo> content) {
        this.content = content;
    }
}
