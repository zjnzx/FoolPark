package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.BindUserInfo;

/**
 * Created by YY on 2016/7/23.
 */
public class GetBindUsers {
    private int code;
    private List<BindUserInfo> content;

    @Override
    public String toString() {
        return "GetBindUsers{" +
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

    public List<BindUserInfo> getContent() {
        return content;
    }

    public void setContent(List<BindUserInfo> content) {
        this.content = content;
    }
}
