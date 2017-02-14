package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/9/6.
 */
public class PayInfoScan {
    private String sign;
    private String timestamp;
    private String totalFee;
    private String parkId;
    private String orderNo;

    @Override
    public String toString() {
        return "PayInfoScan{" +
                "sign='" + sign + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", totalFee='" + totalFee + '\'' +
                ", parkId='" + parkId + '\'' +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
