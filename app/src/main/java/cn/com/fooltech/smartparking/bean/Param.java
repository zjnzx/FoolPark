package cn.com.fooltech.smartparking.bean;

/**
 * Created by YY on 2016/9/18.
 */
public class Param {
    public String key;
    public String value;

    public Param() {
    }

    public Param(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Param{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
