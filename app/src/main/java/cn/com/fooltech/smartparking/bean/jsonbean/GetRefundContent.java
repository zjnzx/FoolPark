package cn.com.fooltech.smartparking.bean.jsonbean;

import java.util.List;

import cn.com.fooltech.smartparking.bean.OrderInfo;

/**
 * Created by YY on 2016/7/30.
 */
public class GetRefundContent {
    private int balance;
    private List<OrderInfo> orders;

    @Override
    public String toString() {
        return "GetRefundContent{" +
                "balance=" + balance +
                ", orders=" + orders +
                '}';
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<OrderInfo> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderInfo> orders) {
        this.orders = orders;
    }
}
