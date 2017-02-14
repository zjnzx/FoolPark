package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/10/17.
 */
public class CardInfo {
    private int isVerified;
    private String parkName;
    private String fromDate;
    private String parkAddress;
    private String plateNumber;
    private String toDate;
    private int cardType;
    private int cardPrice;

    @Override
    public String toString() {
        return "CardInfo{" +
                "isVerified=" + isVerified +
                ", parkName='" + parkName + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", parkAddress='" + parkAddress + '\'' +
                ", plateNumber='" + plateNumber + '\'' +
                ", toDate='" + toDate + '\'' +
                ", cardType=" + cardType +
                ", cardPrice=" + cardPrice +
                '}';
    }

    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
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

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public int getCardPrice() {
        return cardPrice;
    }

    public void setCardPrice(int cardPrice) {
        this.cardPrice = cardPrice;
    }
}
