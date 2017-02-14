package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/7/14.
 */
public class BannerInfo {
    private long bannerId;
    private String bannerImage;
    private int imageOrder;
    private String linkUrl;

    public BannerInfo() {
    }

    public BannerInfo(long bannerId, String bannerImage) {
        this.bannerId = bannerId;
        this.bannerImage = bannerImage;
    }

    @Override
    public String toString() {
        return "BannerInfo{" +
                "bannerId=" + bannerId +
                ", bannerImage='" + bannerImage + '\'' +
                ", imageOrder=" + imageOrder +
                ", linkUrl='" + linkUrl + '\'' +
                '}';
    }

    public long getBannerId() {
        return bannerId;
    }

    public void setBannerId(long bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public int getImageOrder() {
        return imageOrder;
    }

    public void setImageOrder(int imageOrder) {
        this.imageOrder = imageOrder;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
