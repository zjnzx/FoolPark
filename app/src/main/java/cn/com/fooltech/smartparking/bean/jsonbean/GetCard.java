package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.CardInfo;

/**
 * Created by YY on 2016/10/17.
 */
public class GetCard {
    private int code;
    private List<CardInfo> content;

    @Override
    public String toString() {
        return "GetCard{" +
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

    public List<CardInfo> getContent() {
        return content;
    }

    public void setContent(List<CardInfo> content) {
        this.content = content;
    }
}
