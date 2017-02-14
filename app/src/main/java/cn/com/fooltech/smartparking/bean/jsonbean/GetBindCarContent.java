package cn.com.fooltech.smartparking.bean.jsonbean;

import java.io.Serializable;

/**
 * Created by YY on 2016/7/28.
 */
public class GetBindCarContent implements Serializable{
    private long carId;
    private String plateNumber;
    private int balance;
    private int imageIndex;
//    private List<UserInfo> users;


    @Override
    public String toString() {
        return "GetBindCarContent{" +
                "carId=" + carId +
                ", plateNumber='" + plateNumber + '\'' +
                ", balance=" + balance +
                ", imageIndex=" + imageIndex +
                '}';
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    //    public List<UserInfo> getUsers() {
//        return users;
//    }
//
//    public void setUsers(List<UserInfo> users) {
//        this.users = users;
//    }
}
