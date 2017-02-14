package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.MessageInfo;

/**
 * Created by YY on 2016/7/27.
 */
public class GetMessage {
    private int code;
    private List<MessageInfo> content;

    @Override
    public String toString() {
        return "GetMessage{" +
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

    public List<MessageInfo> getContent() {
        return content;
    }

    public void setContent(List<MessageInfo> content) {
        this.content = content;
    }
}
