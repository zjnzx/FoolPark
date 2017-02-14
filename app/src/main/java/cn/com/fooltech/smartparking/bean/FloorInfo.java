package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/8/15.
 */
public class FloorInfo {
    private String floor;

    @Override
    public String toString() {
        return "FloorInfo{" +
                "floor='" + floor + '\'' +
                '}';
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
}
