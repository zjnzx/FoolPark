package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.BannerInfo;

/**
 * Created by YY on 2016/7/22.
 */
public class GetBanner {
    private int code;
    private List<BannerInfo> content;

    @Override
    public String toString() {
        return "GetBanner{" +
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

    public List<BannerInfo> getContent() {
        return content;
    }

    public void setContent(List<BannerInfo> content) {
        this.content = content;
    }
}
