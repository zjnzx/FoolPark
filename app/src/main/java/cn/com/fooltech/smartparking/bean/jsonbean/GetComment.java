package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.CommentInfo;

/**
 * Created by YY on 2016/10/13.
 */
public class GetComment {
    private int code;
    private GetCommentContent content;

    @Override
    public String toString() {
        return "GetComment{" +
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

    public GetCommentContent getContent() {
        return content;
    }

    public void setContent(GetCommentContent content) {
        this.content = content;
    }
}
