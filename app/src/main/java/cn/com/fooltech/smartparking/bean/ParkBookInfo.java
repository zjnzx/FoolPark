package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/7/27.
 */
public class ParkBookInfo extends ParkInfo{
    private long bookId;
//    private long parkId;
//    private String parkName;
//    private String parkAddress;
//    private float parkLng;
//    private float parkLat;
//    private String parkPrice;
    private String plateNumber;
    private Date bookTime;
    private int bookStatus;
//    private int distance;


    @Override
    public String toString() {
        return "ParkBookInfo{" +
                "bookId=" + bookId +
                ", plateNumber='" + plateNumber + '\'' +
                ", bookTime=" + bookTime +
                ", bookStatus=" + bookStatus +
                '}';
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Date getBookTime() {
        return bookTime;
    }

    public void setBookTime(Date bookTime) {
        this.bookTime = bookTime;
    }

    public int getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(int bookStatus) {
        this.bookStatus = bookStatus;
    }

}
