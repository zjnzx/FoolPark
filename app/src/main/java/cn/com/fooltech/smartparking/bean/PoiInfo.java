package cn.com.fooltech.smartparking.bean;

import java.util.Map;

/**
 * Created by YY on 2016/8/24.
 */
public class PoiInfo {
    private String poiId;
    private float poiLng;
    private float poiLat;
    private int distance;
    private int poiType;
    private String poiName;
    private String poiAddress;
    private ParkInfo detailInfo;

    @Override
    public String toString() {
        return "PoiInfo{" +
                "poiId='" + poiId + '\'' +
                ", poiLng=" + poiLng +
                ", poiLat=" + poiLat +
                ", distance=" + distance +
                ", poiType=" + poiType +
                ", poiName='" + poiName + '\'' +
                ", poiAddress='" + poiAddress + '\'' +
                ", detailInfo=" + detailInfo +
                '}';
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public float getPoiLng() {
        return poiLng;
    }

    public void setPoiLng(float poiLng) {
        this.poiLng = poiLng;
    }

    public float getPoiLat() {
        return poiLat;
    }

    public void setPoiLat(float poiLat) {
        this.poiLat = poiLat;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getPoiType() {
        return poiType;
    }

    public void setPoiType(int poiType) {
        this.poiType = poiType;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getPoiAddress() {
        return poiAddress;
    }

    public void setPoiAddress(String poiAddress) {
        this.poiAddress = poiAddress;
    }

    public ParkInfo getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(ParkInfo detailInfo) {
        this.detailInfo = detailInfo;
    }
}
