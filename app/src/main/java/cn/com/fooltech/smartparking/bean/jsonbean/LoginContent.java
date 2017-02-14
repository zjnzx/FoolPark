package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.bean.UserInfo;

/**
 * Created by YY on 2016/7/21.
 */
public class LoginContent {
    private List<BindCarInfo> car;
    private String token;
    private int newMessage;
    private UserInfo user;

    @Override
    public String toString() {
        return "LoginContent{" +
                "car=" + car +
                ", token='" + token + '\'' +
                ", newMessage=" + newMessage +
                ", user=" + user +
                '}';
    }

    public List<BindCarInfo> getCar() {
        return car;
    }

    public void setCar(List<BindCarInfo> car) {
        this.car = car;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(int newMessage) {
        this.newMessage = newMessage;
    }
}
