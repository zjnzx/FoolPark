package cn.com.fooltech.smartparking.bean.jsonbean;

import cn.com.fooltech.smartparking.bean.AppInfo;

/**
 * Created by YY on 2016/9/14.
 */
public class GetAppInfo {
    private int code;
    private AppInfo content;

    @Override
    public String toString() {
        return "GetAppInfo{" +
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

    public AppInfo getContent() {
        return content;
    }

    public void setContent(AppInfo content) {
        this.content = content;
    }
}
