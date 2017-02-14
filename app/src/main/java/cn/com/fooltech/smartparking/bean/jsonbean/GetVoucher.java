package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.VoucherInfo;

/**
 * Created by YY on 2016/7/26.
 */
public class GetVoucher {
    private int code;
    private List<VoucherInfo> content;

    @Override
    public String toString() {
        return "GetVoucher{" +
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

    public List<VoucherInfo> getContent() {
        return content;
    }

    public void setContent(List<VoucherInfo> content) {
        this.content = content;
    }
}
