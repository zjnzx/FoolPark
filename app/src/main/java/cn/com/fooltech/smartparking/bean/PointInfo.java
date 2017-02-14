package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/7/26.
 */
public class PointInfo {
    private long pointsId;
    private long userId;
    private int pointsType;
    private String pointsRemark;
    private int pointsNum;
    private Date updateTime;

    @Override
    public String toString() {
        return "PointInfo{" +
                "pointsId=" + pointsId +
                ", userId=" + userId +
                ", pointsType=" + pointsType +
                ", pointsRemark='" + pointsRemark + '\'' +
                ", pointsNum=" + pointsNum +
                ", updateTime=" + updateTime +
                '}';
    }

    public long getPointsId() {
        return pointsId;
    }

    public void setPointsId(long pointsId) {
        this.pointsId = pointsId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getPointsType() {
        return pointsType;
    }

    public void setPointsType(int pointsType) {
        this.pointsType = pointsType;
    }

    public String getPointsRemark() {
        return pointsRemark;
    }

    public void setPointsRemark(String pointsRemark) {
        this.pointsRemark = pointsRemark;
    }

    public int getPointsNum() {
        return pointsNum;
    }

    public void setPointsNum(int pointsNum) {
        this.pointsNum = pointsNum;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
