package cn.com.fooltech.smartparking.bean;

import java.util.Date;

/**
 * Created by YY on 2016/7/23.
 */
public class ActivityInfo {
    private long activityId;
    private String activityTitle;
    private String activityImage;
    private String activityDetail;
    private Date beginTime;
    private Date endTime;
    private Date publishTime;
    private String linkUrl;
    private int visitCount;

    @Override
    public String toString() {
        return "ActicityInfo2{" +
                "activityId=" + activityId +
                ", activityTitle='" + activityTitle + '\'' +
                ", activityImage='" + activityImage + '\'' +
                ", activityDetail='" + activityDetail + '\'' +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", publishTime=" + publishTime +
                ", linkUrl='" + linkUrl + '\'' +
                ", visitCount=" + visitCount +
                '}';
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityImage() {
        return activityImage;
    }

    public void setActivityImage(String activityImage) {
        this.activityImage = activityImage;
    }

    public String getActivityDetail() {
        return activityDetail;
    }

    public void setActivityDetail(String activityDetail) {
        this.activityDetail = activityDetail;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}
