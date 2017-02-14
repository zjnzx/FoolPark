package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/9/14.
 */
public class AppInfo {
    private int forceUpgrade;
    private String currentVer;
    private String downloadUrl;

    @Override
    public String toString() {
        return "AppInfo{" +
                "forceUpgrade=" + forceUpgrade +
                ", currentVer='" + currentVer + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }

    public int getForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(int forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public String getCurrentVer() {
        return currentVer;
    }

    public void setCurrentVer(String currentVer) {
        this.currentVer = currentVer;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
