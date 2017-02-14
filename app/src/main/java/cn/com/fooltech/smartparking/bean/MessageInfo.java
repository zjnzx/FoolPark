package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/7/27.
 */
public class MessageInfo {
    private long messageId;
    private int messageType;
    private String messageText;
    private long sendUser;
    private String nickname;
    private String mobile;
    private String avatarUrl;
    private String extraInfo1;
    private String extraInfo2;
    private long recvUser;
    private Date sendTime;
    private int messageStatus;

    @Override
    public String toString() {
        return "MessageInfo{" +
                "messageId=" + messageId +
                ", messageType=" + messageType +
                ", messageText='" + messageText + '\'' +
                ", sendUser=" + sendUser +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", extraInfo1='" + extraInfo1 + '\'' +
                ", extraInfo2='" + extraInfo2 + '\'' +
                ", recvUser=" + recvUser +
                ", sendTime=" + sendTime +
                ", messageStatus=" + messageStatus +
                '}';
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getSendUser() {
        return sendUser;
    }

    public void setSendUser(long sendUser) {
        this.sendUser = sendUser;
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

    public String getExtraInfo1() {
        return extraInfo1;
    }

    public void setExtraInfo1(String extraInfo1) {
        this.extraInfo1 = extraInfo1;
    }

    public String getExtraInfo2() {
        return extraInfo2;
    }

    public void setExtraInfo2(String extraInfo2) {
        this.extraInfo2 = extraInfo2;
    }

    public long getRecvUser() {
        return recvUser;
    }

    public void setRecvUser(long recvUser) {
        this.recvUser = recvUser;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }
}
