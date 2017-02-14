package cn.com.fooltech.smartparking.bean.jsonbean;

/**
 * Created by YY on 2016/9/21.
 */
public class GetParkingInfo {
    private int code;
    private GetParkingInfoContent content;

    @Override
    public String toString() {
        return "GetParkingInfo{" +
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

    public GetParkingInfoContent getContent() {
        return content;
    }

    public void setContent(GetParkingInfoContent content) {
        this.content = content;
    }
}
