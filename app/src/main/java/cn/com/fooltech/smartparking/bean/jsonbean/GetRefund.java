package cn.com.fooltech.smartparking.bean.jsonbean;

/**
 * Created by YY on 2016/7/30.
 */
public class GetRefund {
    private int code;
    private GetRefundContent content;

    @Override
    public String toString() {
        return "GetRefund{" +
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

    public GetRefundContent getContent() {
        return content;
    }

    public void setContent(GetRefundContent content) {
        this.content = content;
    }
}
