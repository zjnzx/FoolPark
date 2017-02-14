package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/7/26.
 * 代金券
 */
public class VoucherInfo {
    private long voucherId;
    private long ownerId;
    private String voucherRemark;
    private int voucherType;
    private String expiryDate;
    private String voucherStatus;
    private int voucherValue;

    public VoucherInfo(){

    }

    public VoucherInfo(long voucherId, long ownerId, String voucherRemark, int voucherType, String expiryDate, String voucherStatus, int voucherValue) {
        this.voucherId = voucherId;
        this.ownerId = ownerId;
        this.voucherRemark = voucherRemark;
        this.voucherType = voucherType;
        this.expiryDate = expiryDate;
        this.voucherStatus = voucherStatus;
        this.voucherValue = voucherValue;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof VoucherInfo)) return false;
//
//        VoucherInfo info = (VoucherInfo) o;
//
//        if (ownerId != info.ownerId) return false;
//        if (voucherId != info.voucherId) return false;
//        if (voucherType != info.voucherType) return false;
//        if (voucherValue != info.voucherValue) return false;
//        if (!expiryDate.equals(info.expiryDate)) return false;
//        if (!voucherRemark.equals(info.voucherRemark)) return false;
//        if (!voucherStatus.equals(info.voucherStatus)) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = (int) (voucherId ^ (voucherId >>> 32));
//        result = 31 * result + (int) (ownerId ^ (ownerId >>> 32));
//        result = 31 * result + voucherRemark.hashCode();
//        result = 31 * result + voucherType;
//        result = 31 * result + expiryDate.hashCode();
//        result = 31 * result + voucherStatus.hashCode();
//        result = 31 * result + voucherValue;
//        return result;
//    }

    @Override
    public String toString() {
        return "VoucherInfo{" +
                "voucherId=" + voucherId +
                ", ownerId=" + ownerId +
                ", voucherRemark='" + voucherRemark + '\'' +
                ", voucherType='" + voucherType + '\'' +
                ", expiryDate=" + expiryDate +
                ", voucherStatus=" + voucherStatus +
                '}';
    }

    public long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(long voucherId) {
        this.voucherId = voucherId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getVoucherRemark() {
        return voucherRemark;
    }

    public void setVoucherRemark(String voucherRemark) {
        this.voucherRemark = voucherRemark;
    }

    public int getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(int voucherValue) {
        this.voucherValue = voucherValue;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(int voucherType) {
        this.voucherType = voucherType;
    }

    public String getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        this.voucherStatus = voucherStatus;
    }
}
