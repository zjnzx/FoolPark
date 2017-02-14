package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.SpaceInfo;

/**
 * Created by YY on 2016/9/21.
 */
public class GetParkingInfoContent {
    private long parkId;
    private List<String> floorList;
    private List<List<SpaceInfo>> spaceList;

    @Override
    public String toString() {
        return "GetParkingInfoContent{" +
                "parkId=" + parkId +
                ", floorList=" + floorList +
                ", spaceList=" + spaceList +
                '}';
    }

    public long getParkId() {
        return parkId;
    }

    public void setParkId(long parkId) {
        this.parkId = parkId;
    }

    public List<String> getFloorList() {
        return floorList;
    }

    public void setFloorList(List<String> floorList) {
        this.floorList = floorList;
    }

    public List<List<SpaceInfo>> getSpaceList() {
        return spaceList;
    }

    public void setSpaceList(List<List<SpaceInfo>> spaceList) {
        this.spaceList = spaceList;
    }
}
