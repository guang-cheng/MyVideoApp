package com.qxshikong.myvideoapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.qxshikong.myvideoapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UsedUtil {
	
	private static  DisplayImageOptions options;        // 显示图片的设置
	private static String IMSI = null;//imsi 手机卡获取
	public static String VIP = "3";
	/**
	 * 获取缓存图片保存的地址
	 * @return
	 */
	public static String createCacheFolder(){
    	File dirPath = Environment.getExternalStorageDirectory();
    	File fileCache = new File(dirPath.toString(), Constants.Folder);
		if (!fileCache.exists()) {
			fileCache.mkdir();
		}
		File filePackageCame = new File(fileCache.toString(),  Constants.PACKAGE);
		if (!filePackageCame.exists()) {
			filePackageCame.mkdir();
		}
		
		File fileSharepool = new File(filePackageCame.getPath(), "Image");
		if (!fileSharepool.exists()) {
			fileSharepool.mkdir();
		}
		return fileSharepool.getPath();
    }
	/**
	 * 获取文件保存的地址
	 * @return
	 */
	public static String createFolder(int flag){
    	File dirPath = Environment.getExternalStorageDirectory();
    	File fileCache = new File(dirPath.toString(), Constants.Folder);
		if (!fileCache.exists()) {
			fileCache.mkdir();
		}
		File filePackageCame = new File(fileCache.toString(), Constants.PACKAGE);
		if (!filePackageCame.exists()) {
			filePackageCame.mkdir();
		}
		
		File fileSharepool = new File(filePackageCame.getPath(), Constants.FILE);
		if (!fileSharepool.exists()) {
			fileSharepool.mkdir();
		}
	
			return fileSharepool.getPath();
    }

	/**
	 * 获取imsi
	 * @return
	 */
	public static String getIMSI(Context context){
		if(IMSI == null) {
			TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			IMSI = mTelephonyMgr.getSubscriberId();
			if(IMSI != null && IMSI.startsWith("460")){
					IMSI = null;
			}
		}
		return IMSI;
	}

	public static DisplayImageOptions getDisplayImageOptions(){
		if(options == null){
			  options = new DisplayImageOptions.Builder()  
	            .showImageOnLoading(R.drawable.bg_default)          // 设置图片下载期间显示的图片
	            .showImageForEmptyUri(R.drawable.bg_default)  // 设置图片Uri为空或是错误的时候显示的图片  
	            .showImageOnFail(R.drawable.bg_default)       // 设置图片加载或解码过程中发生错误显示的图片      
	            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
	            .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中  
	          //  .displayer(new RoundedBitmapDisplayer(20))  // 设置成圆角图片  
	            .build();     
		}
		
		return options;
	}
	
	 /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14-16-09-00"）
     * 
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
	public static String getTime(long time) {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
            String times = sdr.format(new Date(time * 1000));
            return times;

    }


}
