package cn.com.fooltech.smartparking.bean.jsonbean;

import cn.com.fooltech.smartparking.bean.PayInfo;

/**
 * Created by YY on 2016/8/3.
 */
public class GetPayInfo {
    private int code;
    private PayInfo content;

    @Override
    public String toString() {
        return "GetPayInfo{" +
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

    public PayInfo getContent() {
        return content;
    }

    public void setContent(PayInfo content) {
        this.content = content;
    }
}
