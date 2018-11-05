package com.qxshikong.myvideoapp.util;

import android.util.Log;

public class LogUtil {
	 private static  final  boolean isLog = false;

	 	public static void  d(String tag){
	        if(isLog)
	        Log.d("tag",tag);
	    }
	    
	    public static void  d(String tag,String msg){
	        if(isLog)
	        Log.d(tag,msg);
	    }
	 
	    public static void  e(String tag){
	        if(isLog){
				if(tag == null)
					tag="空";
				Log.e("tag",tag);
			}

	    }
	    
	    public static void  e(String tag,String msg){
	        if(isLog)
	        Log.e(tag,msg);
	    }
}
