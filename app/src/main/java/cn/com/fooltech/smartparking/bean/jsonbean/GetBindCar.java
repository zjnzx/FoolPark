package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.BindCarInfo;

/**
 * Created by YY on 2016/7/22.
 */
public class GetBindCar {
    private int code;
    private List<BindCarInfo> content;

    @Override
    public String toString() {
        return "GetBindCar{" +
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

    public List<BindCarInfo> getContent() {
        return content;
    }

    public void setContent(List<BindCarInfo> content) {
        this.content = content;
    }
}
