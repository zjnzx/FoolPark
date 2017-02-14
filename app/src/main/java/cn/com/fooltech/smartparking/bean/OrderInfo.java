package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/7/30.
 * 提现信息
 */
public class OrderInfo {
    private String orderNo;
    private String orderTime;
    private int orderAmount;
    private int orderPayment;
    private int refundAmount;
    private String orderStatus;

    @Override
    public String toString() {
        return "RefundInfo{" +
                "orderNo='" + orderNo + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", orderAmount=" + orderAmount +
                ", orderPayment=" + orderPayment +
                ", refundAmount=" + refundAmount +
                ", orderStatus='" + orderStatus + '\'' +
                '}';
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public int getOrderPayment() {
        return orderPayment;
    }

    public void setOrderPayment(int orderPayment) {
        this.orderPayment = orderPayment;
    }

    public int getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(int refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
