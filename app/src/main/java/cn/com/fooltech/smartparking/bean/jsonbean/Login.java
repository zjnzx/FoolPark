package cn.com.fooltech.smartparking.bean.jsonbean;

/**
 * Created by YY on 2016/7/18.
 */
public class Login {
    private int code;
    private LoginContent content;

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

    public LoginContent getContent() {
        return content;
    }

    public void setContent(LoginContent content) {
        this.content = content;
    }
}
