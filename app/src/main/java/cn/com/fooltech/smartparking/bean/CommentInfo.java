package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/10/13.
 */
public class CommentInfo {
    private long commentId;
    private String nickname;
    private String mobile;
    private String avatarUrl;
    private long parkId;
    private int parkLevel;
    private String parkComment;
    private int supportCount;
    private String submitTime;

    @Override
    public String toString() {
        return "CommentInfo{" +
                "commentId=" + commentId +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", parkId=" + parkId +
                ", parkLevel=" + parkLevel +
                ", parkComment='" + parkComment + '\'' +
                ", supportCount=" + supportCount +
                ", submitTime='" + submitTime + '\'' +
                '}';
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getParkId() {
        return parkId;
    }

    public void setParkId(long parkId) {
        this.parkId = parkId;
    }

    public int getParkLevel() {
        return parkLevel;
    }

    public void setParkLevel(int parkLevel) {
        this.parkLevel = parkLevel;
    }

    public String getParkComment() {
        return parkComment;
    }

    public void setParkComment(String parkComment) {
        this.parkComment = parkComment;
    }

    public int getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(int supportCount) {
        this.supportCount = supportCount;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }
}
