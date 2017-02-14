package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.ActivityInfo;

/**
 * Created by YY on 2016/7/23.
 */
public class GetActivity {
    private int code;
    private List<ActivityInfo> content;

    @Override
    public String toString() {
        return "GetActivity{" +
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

    public List<ActivityInfo> getContent() {
        return content;
    }

    public void setContent(List<ActivityInfo> content) {
        this.content = content;
    }
}
