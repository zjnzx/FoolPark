package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/7/23.
 * 和车辆关联的用户信息
 */
public class BindUserInfo {
    private long userId;
    private String nickname;
    private String mobile;
    private String avatarUrl;
    private int isOwner; //是否为车主。0-否、1-是
    private int isDriver;//是否为当前使用者。0-否、1-是
    private int balance;

    @Override
    public String toString() {
        return "BindUserInfo{" +
                "userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", isOwner=" + isOwner +
                ", isDriver=" + isDriver +
                ", balance=" + balance +
                '}';
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public int getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }

    public int getIsDriver() {
        return isDriver;
    }

    public void setIsDriver(int isDriver) {
        this.isDriver = isDriver;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
