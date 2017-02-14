package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/8/11.
 */
public class PositionRecordInfo extends SpaceInfo{
    private long positionId;
    private long userId;
//    private long parkId;
    private String parkName;
    private String parkAddress;
    private String plateNumber;
    private int distance;
    private Date recordTime;
    private String spaceLabel;
    private String spaceFloor;

    public PositionRecordInfo() {
    }

    public PositionRecordInfo(long positionId, String parkName, String parkAddress, int distance,String spaceLabel,String spaceFloor) {
        this.positionId = positionId;
        this.parkName = parkName;
        this.parkAddress = parkAddress;
        this.distance = distance;
        this.spaceLabel = spaceLabel;
        this.spaceFloor = spaceFloor;
    }

    @Override
    public String toString() {
        return "PositionRecordInfo{" +
                "positionId=" + positionId +
                ", userId=" + userId +
                ", parkName='" + parkName + '\'' +
                ", parkAddress='" + parkAddress + '\'' +
                ", plateNumber='" + plateNumber + '\'' +
                ", distance=" + distance +
                ", recordTime=" + recordTime +
                '}';
    }

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getParkAddress() {
        return parkAddress;
    }

    public void setParkAddress(String parkAddress) {
        this.parkAddress = parkAddress;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String getSpaceLabel() {
        return spaceLabel;
    }

    @Override
    public void setSpaceLabel(String spaceLabel) {
        this.spaceLabel = spaceLabel;
    }

    @Override
    public String getSpaceFloor() {
        return spaceFloor;
    }

    @Override
    public void setSpaceFloor(String spaceFloor) {
        this.spaceFloor = spaceFloor;
    }
}
