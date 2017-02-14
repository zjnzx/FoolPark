package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/7/14.
 */
public class WeathkerInfo {
    private String temperature;
    private String wind;
    private String weather;
    private String imageUrl;
    private String pm;
    private String des;

    @Override
    public String toString() {
        return "WeathkerInfo{" +
                "temperature='" + temperature + '\'' +
                ", wind='" + wind + '\'' +
                ", weather='" + weather + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", pm='" + pm + '\'' +
                ", des='" + des + '\'' +
                '}';
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
