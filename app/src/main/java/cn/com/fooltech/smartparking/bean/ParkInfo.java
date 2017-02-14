package cn.com.fooltech.smartparking.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by YY on 2016/7/5. 停车场
 */
public class ParkInfo implements Serializable {
    private long parkId;
    private String parkName;
    private int parkType;
    private float parkLng;
    private float parkLat;
    private int distance;
    private int orderSpace;
    private int isCollected;//0:未收藏  1:已收藏
    private int freeSpace;
    private int totalSpace;
    private String parkPrice;
    private int isOpen;
    private int canBook;
    private String parkAddress;
    private String beginTime;
    private String closeTime;
    private String parkRemark;
    private String parkTel;
    private List<String> urlList;
    private String[] entryImages;
    private int monthPrice;
    private int seasonPrice;
    private int yearPrice;

    @Override
    public String toString() {
        return "ParkInfo{" +
                "parkId=" + parkId +
                ", parkName='" + parkName + '\'' +
                ", parkType=" + parkType +
                ", parkLng=" + parkLng +
                ", parkLat=" + parkLat +
                ", distance=" + distance +
                ", orderSpace=" + orderSpace +
                ", isCollected=" + isCollected +
                ", freeSpace=" + freeSpace +
                ", totalSpace=" + totalSpace +
                ", parkPrice='" + parkPrice + '\'' +
                ", isOpen=" + isOpen +
                ", canBook=" + canBook +
                ", parkAddress='" + parkAddress + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", closeTime='" + closeTime + '\'' +
                ", parkRemark='" + parkRemark + '\'' +
                ", parkTel='" + parkTel + '\'' +
                ", urlList=" + urlList +
                ", entryImages=" + Arrays.toString(entryImages) +
                ", monthPrice=" + monthPrice +
                ", seasonPrice=" + seasonPrice +
                ", yearPrice=" + yearPrice +
                '}';
    }

    public long getParkId() {
        return parkId;
    }

    public void setParkId(long parkId) {
        this.parkId = parkId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public int getParkType() {
        return parkType;
    }

    public void setParkType(int parkType) {
        this.parkType = parkType;
    }

    public float getParkLng() {
        return parkLng;
    }

    public void setParkLng(float parkLng) {
        this.parkLng = parkLng;
    }

    public float getParkLat() {
        return parkLat;
    }

    public void setParkLat(float parkLat) {
        this.parkLat = parkLat;
    }

    public int getOrderSpace() {
        return orderSpace;
    }

    public void setOrderSpace(int orderSpace) {
        this.orderSpace = orderSpace;
    }

    public int getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(int freeSpace) {
        this.freeSpace = freeSpace;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(int totalSpace) {
        this.totalSpace = totalSpace;
    }

    public String getParkPrice() {
        return parkPrice;
    }

    public void setParkPrice(String parkPrice) {
        this.parkPrice = parkPrice;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public String getParkAddress() {
        return parkAddress;
    }

    public void setParkAddress(String parkAddress) {
        this.parkAddress = parkAddress;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getParkTel() {
        return parkTel;
    }

    public void setParkTel(String parkTel) {
        this.parkTel = parkTel;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public int getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(int isCollected) {
        this.isCollected = isCollected;
    }

    public int getCanBook() {
        return canBook;
    }

    public void setCanBook(int canBook) {
        this.canBook = canBook;
    }

    public String getParkRemark() {
        return parkRemark;
    }

    public void setParkRemark(String parkRemark) {
        this.parkRemark = parkRemark;
    }

    public String[] getEntryImages() {
        return entryImages;
    }

    public void setEntryImages(String[] entryImages) {
        this.entryImages = entryImages;
    }

    public int getMonthPrice() {
        return monthPrice;
    }

    public void setMonthPrice(int monthPrice) {
        this.monthPrice = monthPrice;
    }

    public int getSeasonPrice() {
        return seasonPrice;
    }

    public void setSeasonPrice(int seasonPrice) {
        this.seasonPrice = seasonPrice;
    }

    public int getYearPrice() {
        return yearPrice;
    }

    public void setYearPrice(int yearPrice) {
        this.yearPrice = yearPrice;
    }
}
