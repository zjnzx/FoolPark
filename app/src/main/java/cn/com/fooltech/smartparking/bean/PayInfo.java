package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/8/3.
 */
public class PayInfo {
    private int alreadyPay;
    private int totalFee;
    private String enterTime;
    private int parkPrice;
    private int useVoucher;
    private String serverTime;

    @Override
    public String toString() {
        return "PayInfo{" +
                "alreadyPay=" + alreadyPay +
                ", totalFee=" + totalFee +
                ", enterTime='" + enterTime + '\'' +
                ", parkPrice=" + parkPrice +
                ", useVoucher=" + useVoucher +
                ", serverTime='" + serverTime + '\'' +
                '}';
    }

    public int getAlreadyPay() {
        return alreadyPay;
    }

    public void setAlreadyPay(int alreadyPay) {
        this.alreadyPay = alreadyPay;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }

    public int getParkPrice() {
        return parkPrice;
    }

    public void setParkPrice(int parkPrice) {
        this.parkPrice = parkPrice;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public int getUseVoucher() {
        return useVoucher;
    }

    public void setUseVoucher(int useVoucher) {
        this.useVoucher = useVoucher;
    }
}
