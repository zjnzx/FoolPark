package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.CommentInfo;

/**
 * Created by YY on 2016/10/17.
 */
public class GetCommentContent {
    private int avgLevel;
    private List<CommentInfo> commentList;

    @Override
    public String toString() {
        return "GetCommentContent{" +
                "avgLevel=" + avgLevel +
                ", commentList=" + commentList +
                '}';
    }

    public int getAvgLevel() {
        return avgLevel;
    }

    public void setAvgLevel(int avgLevel) {
        this.avgLevel = avgLevel;
    }

    public List<CommentInfo> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentInfo> commentList) {
        this.commentList = commentList;
    }
}
