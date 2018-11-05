package com.qxshikong.myvideoapp.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA. User: ryou Date: 1/18/12 Time: 1:00 AM To change
 * this template use File | Settings | File Templates.
 */
public class DeviceInfo {
    public static String deviceID = "";
    public static int screenWidth;
    public static int screenHeight;
    public static float density;
    public static int densityDpi;
    public static String screenResolution = "";
    public static String androidID = "";
    public static String imei = "";
    public static String simSerialNum = "";
    public static String mac = "";
    public static Map<String, String> deviceMap;
    public static JSONObject deviceJSON = new JSONObject();
    private static boolean isInited = false;

    public static void initDeviceInfo(Context context) {
        if (isInited)
            return;
        deviceMap = new HashMap<String, String>();
        initDeviceID(context);
        initScreenResolution(context);

        isInited = true;
    }

    /**
     * @param url
     * @param event
     * @param time
     * @param cdata
     * @param vcode
     * @param tagString
     * @param dataMap
     * @return
     */
    public static String replaceData(String url, String event, String time,
                                     String cdata, String vcode, String tagString,
                                     Map<String, String> dataMap) {
        return "";
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf
                        .getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port
                                // suffix
                                return delim < 0 ? sAddr : sAddr.substring(0,
                                        delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

    // 0 闈瀝oot鏉冮檺
    // 1 root鏉冮檺
    public static int isRoot() {
        int result = 0;
        File file = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        for (int i = 0; i < kSuSearchPaths.length; i++) {
            file = new File(kSuSearchPaths[i] + "su");
            if (file.exists()) {
                result = 1;
                break;
            }
        }
        return result;
    }

    static final String getMacAddress(Context context) {
        if (context != null) {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            return wifiManager.getConnectionInfo().getMacAddress();
        } else {
            return mac;
        }
    }

    private static final void initDeviceID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            imei += telephonyManager.getDeviceId();
            simSerialNum += telephonyManager.getSimSerialNumber();
        }

        androidID += Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

		UUID deviceUUID = new UUID(androidID.hashCode(),
				((long) imei.hashCode() << 32) | simSerialNum.hashCode());
		deviceID = deviceUUID.toString();

        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        mac = wifiManager.getConnectionInfo().getMacAddress();

    }

    static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    static final void initScreenResolution(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;
        densityDpi = displayMetrics.densityDpi;
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        if (width > height) {
            screenResolution = width + "x" + height;
            screenWidth = height;
            screenHeight = width;
        } else {
            screenResolution = height + "x" + width;
            screenWidth = width;
            screenHeight = height;
        }
    }

    private static String carrierInfo = null;

    // 0 绉诲姩 1鑱旈�� 2鐢典俊
    public static final String getCarrierInfo(Context context) {
        if (carrierInfo != null)
            return carrierInfo;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA
                    && telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                carrierInfo = telephonyManager.getNetworkOperator();
            } else if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                carrierInfo = telephonyManager.getSimOperator();
            }
            carrierInfo = analysisMCCMNCInfo();
        } else
            carrierInfo = "0";// modify by mm 2012-4-9 n/a change na
        return carrierInfo;
    }

    private static String analysisMCCMNCInfo() {
        if (carrierInfo == null || "".equals(carrierInfo) || carrierInfo == "")
            return "na"; // modify by mm 2012-4-9 n/a change na
        int mcc = Integer.valueOf(carrierInfo.substring(0, 3)).intValue();
        int mnc = Integer.valueOf(carrierInfo.substring(3)).intValue();

        if (mcc != 460)
            return carrierInfo;
        if (mnc == 0 || mnc == 2 || mnc == 7)
            carrierInfo = "0";
        else if (mnc == 1 || mnc == 6)
            carrierInfo = "1";
        else if (mnc == 3 || mnc == 5)
            carrierInfo = "2";
        else if (mnc == 4)
            carrierInfo = "";
        return carrierInfo;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {

        return (int) (dpValue * density + 0.5f);
    }
}