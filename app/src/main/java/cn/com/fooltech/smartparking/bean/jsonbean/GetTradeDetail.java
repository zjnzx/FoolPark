package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.TradeInfo;

/**
 * Created by YY on 2016/8/2.
 */
public class GetTradeDetail {
    private int code;
    private List<TradeInfo> content;

    @Override
    public String toString() {
        return "GetTradeDetail{" +
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

    public List<TradeInfo> getContent() {
        return content;
    }

    public void setContent(List<TradeInfo> content) {
        this.content = content;
    }
}
