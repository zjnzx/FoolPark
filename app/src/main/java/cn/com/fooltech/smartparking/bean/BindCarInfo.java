package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/7/22.
 * 绑定车辆信息
 */
public class BindCarInfo {
    private long bindId;
    private long carId;
    private String plateNumber;
    private long userId;
    private int imageIndex;
    private int balance;
    private long ownerId;
    private long driverId;

    public BindCarInfo() {
    }


    public BindCarInfo(String plateNumber, int imageIndex) {
        this.plateNumber = plateNumber;
        this.imageIndex = imageIndex;
    }

    @Override
    public String toString() {
        return "BindCarInfo{" +
                "bindId=" + bindId +
                ", carId=" + carId +
                ", plateNumber='" + plateNumber + '\'' +
                ", userId=" + userId +
                ", imageIndex=" + imageIndex +
                ", balance=" + balance +
                ", ownerId=" + ownerId +
                ", driverId=" + driverId +
                '}';
    }

    public long getBindId() {
        return bindId;
    }

    public void setBindId(long bindId) {
        this.bindId = bindId;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }
}
