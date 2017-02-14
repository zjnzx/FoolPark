package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/7/15.
 */
public class SpaceInfo {
    private long spaceId;
    private String parkId;
    private String spaceLabel;
    private float spaceLng;
    private float spaceLat;
    private String spaceFloor;
    private int spaceAltitude;
    private int spaceStatus;

    public SpaceInfo() {
    }

    public SpaceInfo(long spaceId, String spaceLabel) {
        this.spaceId = spaceId;
        this.spaceLabel = spaceLabel;
    }

    @Override
    public String toString() {
        return "SpaceInfo{" +
                "spaceId=" + spaceId +
                ", parkId='" + parkId + '\'' +
                ", spaceLabel='" + spaceLabel + '\'' +
                ", spaceLng=" + spaceLng +
                ", spaceLat=" + spaceLat +
                ", spaceFloor='" + spaceFloor + '\'' +
                ", spaceAltitude=" + spaceAltitude +
                ", spaceStatus=" + spaceStatus +
                '}';
    }

    public long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    public String getSpaceLabel() {
        return spaceLabel;
    }

    public void setSpaceLabel(String spaceLabel) {
        this.spaceLabel = spaceLabel;
    }

    public float getSpaceLng() {
        return spaceLng;
    }

    public void setSpaceLng(float spaceLng) {
        this.spaceLng = spaceLng;
    }

    public float getSpaceLat() {
        return spaceLat;
    }

    public void setSpaceLat(float spaceLat) {
        this.spaceLat = spaceLat;
    }

    public String getSpaceFloor() {
        return spaceFloor;
    }

    public void setSpaceFloor(String spaceFloor) {
        this.spaceFloor = spaceFloor;
    }

    public int getSpaceAltitude() {
        return spaceAltitude;
    }

    public void setSpaceAltitude(int spaceAltitude) {
        this.spaceAltitude = spaceAltitude;
    }

    public int getSpaceStatus() {
        return spaceStatus;
    }

    public void setSpaceStatus(int spaceStatus) {
        this.spaceStatus = spaceStatus;
    }
}
