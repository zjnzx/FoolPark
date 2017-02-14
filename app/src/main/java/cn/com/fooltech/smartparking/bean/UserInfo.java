package cn.com.fooltech.smartparking.bean;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by YY on 2016/7/5.用户
 */
public class UserInfo implements Serializable{
    private long userId;
    private String token;
    private String nickName;
    private String password;
    private int sex;
    private Date birthday;
    private String mobile;
    private String avatarUrl;
    private int points;
    private int plateCount;
    private int voucherCount;
    private int cardCount;
    private int balance;

    public UserInfo() {
    }
    public UserInfo(long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", token='" + token + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", mobile='" + mobile + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", points=" + points +
                ", plateCount=" + plateCount +
                ", voucherCount=" + voucherCount +
                ", cardCount=" + cardCount +
                ", balance=" + balance +
                '}';
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPlateCount() {
        return plateCount;
    }

    public void setPlateCount(int plateCount) {
        this.plateCount = plateCount;
    }

    public int getVoucherCount() {
        return voucherCount;
    }

    public void setVoucherCount(int voucherCount) {
        this.voucherCount = voucherCount;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }
}
