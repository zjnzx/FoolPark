package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/8/15.
 */
public class ExchangeRecordInfo {
    private String exchangeInfo;
    private String exchangeDate;

    public ExchangeRecordInfo() {
    }

    public ExchangeRecordInfo(String exchangeInfo, String exchangeDate) {
        this.exchangeInfo = exchangeInfo;
        this.exchangeDate = exchangeDate;
    }

    @Override
    public String toString() {
        return "ExchangeRecordInfo{" +
                "exchangeInfo='" + exchangeInfo + '\'' +
                ", exchangeDate='" + exchangeDate + '\'' +
                '}';
    }

    public String getExchangeInfo() {
        return exchangeInfo;
    }

    public void setExchangeInfo(String exchangeInfo) {
        this.exchangeInfo = exchangeInfo;
    }

    public String getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(String exchangeDate) {
        this.exchangeDate = exchangeDate;
    }
}
