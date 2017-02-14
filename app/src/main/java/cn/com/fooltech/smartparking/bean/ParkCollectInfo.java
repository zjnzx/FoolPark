package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/7/26.
 * 收藏记录
 */
public class ParkCollectInfo extends ParkInfo{
    private long collectId;
    private long userId;
//    private long parkId;
//    private String parkName;
//    private String parkAddress;
//    private float parkLng;
//    private float parkLat;
    private Date collectTime;

    @Override
    public String toString() {
        return "CollectParkInfo{" +
                "collectId=" + collectId +
                ", userId=" + userId +
//                ", parkId=" + parkId +
//                ", parkName='" + parkName + '\'' +
//                ", parkAddress='" + parkAddress + '\'' +
//                ", parkLng=" + parkLng +
//                ", parkLat=" + parkLat +
                ", collectTime=" + collectTime +
                '}';
    }

    public long getCollectId() {
        return collectId;
    }

    public void setCollectId(long collectId) {
        this.collectId = collectId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

//    public long getParkId() {
//        return parkId;
//    }
//
//    public void setParkId(long parkId) {
//        this.parkId = parkId;
//    }

//    public String getParkName() {
//        return parkName;
//    }
//
//    public void setParkName(String parkName) {
//        this.parkName = parkName;
//    }
//
//    public String getParkAddress() {
//        return parkAddress;
//    }
//
//    public void setParkAddress(String parkAddress) {
//        this.parkAddress = parkAddress;
//    }
//
//    public float getParkLng() {
//        return parkLng;
//    }
//
//    public void setParkLng(float parkLng) {
//        this.parkLng = parkLng;
//    }
//
//    public float getParkLat() {
//        return parkLat;
//    }
//
//    public void setParkLat(float parkLat) {
//        this.parkLat = parkLat;
//    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }
}
