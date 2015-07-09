package com.example.tujia.comic.util;

import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceManager {

  private int screenHeight = 0;
  private int screenWidth = 0;
  private int densityDpi = 240;
  private String versionName;
  private String channel;
  private int networkinfo;
  private String userDeviceInfo;
  
  private String deviceId;
  private String phoneNumber;
  private String simSerialNumber;
  private int sdkVersion;
  private String wifiMacAddress;
  private String oldVersion;
  private boolean wifiStatus;
  
  private static class DeviceManagerContainer {

    private static DeviceManager instance = new DeviceManager();
  }

  public static DeviceManager getInstance() {
    return DeviceManagerContainer.instance;
  }

  private DeviceManager() {
  }
  
  public String getVersionName() {
    return versionName;
  }

  public String getChannel() {
    return channel;
  }

  public void setScreenDimension(int w, int h) {
    screenWidth = w;
    screenHeight = h;
  }

  public void setDensityDpi(int densityDpi) {
    this.densityDpi = densityDpi;
  }

  public boolean isXhdpi() {
    return densityDpi >= 320;
  }

  public int getScreenWidth() {
    return screenWidth;
  }

  public int getScreenHeight() {
    return screenHeight;
  }

  public String getUserDeviceInfo() {
	return userDeviceInfo;
  }
  
  public String getPf() {
	return "huyu_m-" + getChannel() + "-android-weiman";
  }

  public void init(Application application) {
    final WindowManager windowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
    final Display display = windowManager.getDefaultDisplay();
    setScreenDimension(display.getWidth(), display.getHeight());
    final DisplayMetrics outMetrics = new DisplayMetrics();
    display.getMetrics(outMetrics);
    setDensityDpi(outMetrics.densityDpi);

    try {
      final String packageName = application.getPackageName();
      final PackageInfo pi = application.getPackageManager().getPackageInfo(packageName, 0);
      versionName = pi.versionName;

      final ApplicationInfo ai =
          application.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
      channel = ai.metaData.get("CHANNEL").toString();
    } catch (final NameNotFoundException e) {
    }
    
    final Context context = application.getApplicationContext();
    final TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    deviceId = telephonyManager.getDeviceId();
    phoneNumber = telephonyManager.getLine1Number();
    simSerialNumber = telephonyManager.getSimSerialNumber();

    final ConnectivityManager connMgr =
            (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
    if (activeInfo == null || !activeInfo.isAvailable()) {
      networkinfo = -1;
    } else {
      networkinfo = activeInfo.getType();
    }
    //check wifi status
    State state = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
    if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络
      wifiStatus = true;
    }

    try {
      sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
    } catch (final NumberFormatException e) {
    }

    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    wifiMacAddress = (wifiInfo == null ? null : wifiInfo.getMacAddress());

    userDeviceInfo = "";
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getSimSerialNumber() {
    return simSerialNumber;
  }

  public int getAndroidSdkVersion() {
    return sdkVersion;
  }

  public String getWifiMacAddress() {
    return wifiMacAddress;
  }
  
  public String getOldVersion() {
    return oldVersion;
  }
  
  public boolean isWifiStatus() {
    return wifiStatus;
  }

  public String refineDeviceScreenSize(String url){
    if (!StringUtil.isNullOrEmpty(url)) {
      if (screenHeight <= 480 && screenWidth <= 320) {
        url = url.substring(0, url.length() - 1) + "100";
      }else if (screenHeight <= 854 && screenWidth <= 480) {
        url = url.substring(0, url.length() - 1) + "133";
      }
    }
    return url;
  }
  
}
