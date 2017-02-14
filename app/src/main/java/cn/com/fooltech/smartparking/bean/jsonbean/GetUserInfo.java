package cn.com.fooltech.smartparking.bean.jsonbean;

import cn.com.fooltech.smartparking.bean.UserInfo;

/**
 * Created by YY on 2016/7/18.
 */
public class GetUserInfo {
    private int code;
    private UserInfo content;

    @Override
    public String toString() {
        return "LoginJsonBean{" +
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

    public UserInfo getContent() {
        return content;
    }

    public void setContent(UserInfo content) {
        this.content = content;
    }
}
