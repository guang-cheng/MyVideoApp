package com.qxshikong.myvideoapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharedPreferencesUtil {

    private static SharedPreferences Userinfo;
    private static boolean isInited = false;
    private static Context ctx;

    public static void initSharedPreferencesUtil(Context context) {
        if (isInited)
            return;
        ctx = context;
        Userinfo = context.getSharedPreferences("user_info", 0);
        isInited = true;
    }

    public static boolean isLogin() {
        if (Userinfo == null) {
            return false;
        }
        return Userinfo.contains("id");
    }

    public static boolean saveMap(Map<String, String> map) {
        if (Userinfo == null) {
            return false;
        }
        SharedPreferences.Editor editor = Userinfo.edit();
        for (String key : map.keySet()) {
            editor.putString(key, map.get(key));
        }
        return editor.commit();
    }

    public static void clear() {
        if (Userinfo == null) {
            return;
        }
        SharedPreferences.Editor editor = Userinfo.edit();
        editor.clear();
        editor.commit();
    }

    public static String getValue(Context context, String key) {
        if (Userinfo == null) {
            Userinfo = context.getSharedPreferences("user_info", 0);
        }
        return Userinfo.getString(key, null);
    }

    public static String getValue(String key) {
        if (Userinfo == null) {
            Userinfo = ctx.getSharedPreferences("user_info", 0);
        }
        return Userinfo.getString(key, null);
    }

    public static Map<String, ?> getShareMap() {
        if (Userinfo == null) {
            return null;
        }
        return Userinfo.getAll();
    }

    public static boolean saveKeyValue(String key, String value) {
        if (Userinfo == null) {
            return false;
        }
        SharedPreferences.Editor editor = Userinfo.edit();

        editor.putString(key, value);

        return editor.commit();
    }
}
