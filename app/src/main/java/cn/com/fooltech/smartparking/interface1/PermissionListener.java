package cn.com.fooltech.smartparking.interface1;

import java.util.List;

/**
 * Created by YY on 2016/12/29.
 */
public interface PermissionListener {
    void onGranted();
    void onDenied(List<String> deniedPermission);
}
