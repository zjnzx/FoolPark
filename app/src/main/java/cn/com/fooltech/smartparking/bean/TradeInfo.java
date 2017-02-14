package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/8/2.
 * 交易信息
 */
public class TradeInfo {
    private long tradeId;
    private long carId;
    private long userId;
    private String mobile;
    private String nickName;
    private int tradeAmount;
    private int tradeType;
    private int tradePayment;
    private String orderNo;
    private Date tradeTime;
    private String tradeRemark;
    private String tradeResult;

    @Override
    public String toString() {
        return "TradeInfo{" +
                "tradeId=" + tradeId +
                ", carId=" + carId +
                ", userId=" + userId +
                ", mobile='" + mobile + '\'' +
                ", nickName='" + nickName + '\'' +
                ", tradeAmount=" + tradeAmount +
                ", tradeType=" + tradeType +
                ", tradePayment=" + tradePayment +
                ", orderNo='" + orderNo + '\'' +
                ", tradeTime=" + tradeTime +
                ", tradeRemark='" + tradeRemark + '\'' +
                ", tradeResult='" + tradeResult + '\'' +
                '}';
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(int tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public int getTradePayment() {
        return tradePayment;
    }

    public void setTradePayment(int tradePayment) {
        this.tradePayment = tradePayment;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeRemark() {
        return tradeRemark;
    }

    public void setTradeRemark(String tradeRemark) {
        this.tradeRemark = tradeRemark;
    }

    public String getTradeResult() {
        return tradeResult;
    }

    public void setTradeResult(String tradeResult) {
        this.tradeResult = tradeResult;
    }
}
