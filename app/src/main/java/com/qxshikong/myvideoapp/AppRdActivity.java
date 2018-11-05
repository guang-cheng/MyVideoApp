package com.qxshikong.myvideoapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qxshikong.myvideoapp.pulltorefresh.XListView;
import com.qxshikong.myvideoapp.util.Constants;
import com.qxshikong.myvideoapp.util.HttpUtil;
import com.qxshikong.myvideoapp.util.LogUtil;
import com.qxshikong.myvideoapp.util.ToastUtils;
import com.qxshikong.myvideoapp.util.UsedUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.act_apprd)
public class AppRdActivity extends BaseActivity  implements XListView.IXListViewListener {

	//private static  cancelable;

	@ViewInject(R.id.title_layout_tv)
	private TextView tv_title;

	@ViewInject(R.id.listview)
	private XListView xlistview;
	private ArrayAdapter<HolderInfo> adapter;
	private static ImageLoader imageLoader;
	private static DisplayImageOptions options;
	private Cache cache;
	private LayoutInflater layoutInflater;
	private static Context context;
	private Map<Integer,Callback.Cancelable> mapCancelable = new HashMap<Integer,Callback.Cancelable>();

	private  final String DOWNLOAD = "Download";
	private  final String STOP = "Stop";
	private  final String INSTALL = "Install";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		context = this;
		cache = new Cache();
		tv_title.setText(getResources().getString(R.string.menu_apprecommend));
		layoutInflater = layoutInflater.from(this);
		imageLoader = ImageLoader.getInstance();
		imageLoader = ImageLoader.getInstance();
		options = UsedUtil.getDisplayImageOptions();

		xlistview.setPullLoadEnable(false);
		xlistview.setPullRefreshEnable(false);
		xlistview.setXListViewListener(this);
		adapter = new ArrayAdapter<HolderInfo>(this,
				R.layout.item_listview_app) {

			public android.view.View getView(int position,
											 android.view.View convertView, android.view.ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.item_listview_app, null);
					holder = new ViewHolder();
					x.view().inject(holder, convertView);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				HolderInfo itm = getItem(position);
				holder.initview(itm,position);

				return convertView;
			}

		};
		RequestParams params = new RequestParams(HttpUtil.Download_URL);
		httpPost(params, 101);
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Event(value = R.id.title_leftLayout, type = View.OnClickListener.class)
	private void title_leftLayoutClick(View view) {
		finish();
	}
	@Event(value = R.id.main_rightLayout, type = View.OnClickListener.class)
	private void main_rightLayoutClick(View view) {
		//finish();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}

	private final static class HolderInfo {
		String aid;
		String a_title;//
		String a_pic;
		String a_size;//
		String a_desc;//
		String a_url;//
		int a_prossbar;//下载进度
		String download = "Download";
		HolderInfo() {

		}
	}

	/*
	 * private final static class Holder{
	 *
	 * @ViewInject(R.id.tv_item_topic) TextView title;
	 *
	 * public void initView(HolderInfo item){ if(item.title != null)
	 * title.setText(item.title); }
	 *
	 * Holder(){
	 *
	 * } }
	 */
	public final class ViewHolder {
		/*
		 * @ViewInject(R.id.ll_item) LinearLayout linearlayout;
		 */
		@ViewInject(R.id.iv_app_item)
		ImageView iv_app_item;

		@ViewInject(R.id.tv_app_name)
		TextView tv_app_name;
		@ViewInject(R.id.tv_app_size)
		TextView tv_app_size;
		@ViewInject(R.id.tv_app_content)
		TextView tv_app_content;
		@ViewInject(R.id.tv_download_pross)
		TextView tv_download_pross;
		@ViewInject(R.id.btn_download)
		Button btn_download;

		String title;
		int index;
		public void initview(HolderInfo item,int position) {
			index = position;
			title = item.a_title;
			if(item.a_pic != null)
				imageLoader.displayImage(item.a_pic, iv_app_item, options);

			if(item.a_title != null)
				tv_app_name.setText(item.a_title);
			if(item.a_size != null)
				tv_app_size.setText(item.a_size);
			if(item.a_desc != null)
				tv_app_content.setText(item.a_desc);

			//btn_download.setText(item.download);
			//if(item.download.equals(INSTALL))
			//	tv_download_pross.setVisibility(View.GONE);
			//tv_download_pross.setText(item.a_prossbar + "%");
		}
		@Event(value = R.id.ll_item ,type = View.OnClickListener.class)
		private void  ll_itemClick(View view){

		}
		@Event(value = R.id.btn_download ,type = View.OnClickListener.class)
		private void  btn_downloadClick(View view){
			if(btn_download.getText().toString().equals(STOP)){
				btn_download.setText(DOWNLOAD);
				mapCancelable.get(index).cancel();
				mapCancelable.remove(mapCancelable.get(index));//移除暂停項
			}else if(btn_download.getText().toString().equals(DOWNLOAD)){
				btn_download.setText(STOP);
				tv_download_pross.setVisibility(View.VISIBLE);

				mapCancelable.put(index, download("http://apk.hiapk.com/appdown/com.songheng.eastnews/9", title, index,tv_download_pross,btn_download));
			}else{
				mHandler.sendMessage(mHandler.obtainMessage(105,index,0));
			}
		}
		public ViewHolder() {
			// TODO Auto-generated constructor stub
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 101:
					//xlistview.setAdapter(adapter);
					adapter.clear();
					if(adapter != null){
						ArrayList<HolderInfo> infoList = cache.generateLocalList();
						if(infoList.size() > 9){
							xlistview.setPullLoadEnable(true);
						}
						for(HolderInfo info: infoList){
							adapter.add(info);
						}

					}
					xlistview.setAdapter(adapter);
					//sendEmptyMessage(110);
					break;
				case 102:
					//加载数据
					cache.update((ArrayList<HolderInfo>) msg.obj);

					sendEmptyMessage(101);
					break;
				case 103:
					//加载数据
					cache.clear();
					cache.update((ArrayList<HolderInfo>) msg.obj);
					sendEmptyMessage(101);
					break;
				case 104:
					LogUtil.e("msg.arg1"+msg.arg1+",msg.arg2=="+msg.arg2);
						adapter.getItem(msg.arg1).a_prossbar = msg.arg2;

					adapter.notifyDataSetChanged();
					break;
				case 105:
					//adapter.getItem(msg.arg1).a_prossbar = 100;
					//adapter.notifyDataSetChanged();
					azApk(adapter.getItem(msg.arg1).a_title);
					break;
				default:
					break;
			}

		};
	};

	public Callback.Cancelable download(String url,String fileName,final int index,final TextView tv_progress,final Button btn_download){
		RequestParams params = new RequestParams(url);
		params.setAutoResume(true);

		params.setSaveFilePath(createAPKFolder(fileName));
		Callback.Cancelable cancelable = x.http().get(params,  new Callback.ProgressCallback<File>() {

			@Override
			public void onWaiting() {

			}

			@Override
			public void onStarted() {

			}

			@Override
			public void onLoading(long total, long current,boolean isDownloading) {
					int arg2 = (int)(current*100/total);
				LogUtil.e("l="+total+",ll=="+current+",isDownloading="+isDownloading);
				tv_progress.setText(arg2+"%");
				mHandler.sendMessage(mHandler.obtainMessage(104,index,arg2 ));
			}

			@Override
			public void onSuccess(File file) {
				LogUtil.e("on-----------onSuccess");
				tv_progress.setText("100%");
				btn_download.setText(INSTALL);
				mHandler.sendMessage(mHandler.obtainMessage(105,index,0));
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtils.showLongToast(context, context.getResources().getString(R.string.service_error));
			}

			@Override
			public void onCancelled(CancelledException cex) {
				//ToastUtils.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFinished() {
				LogUtil.e("on-----------Finished");

			}
		});

		return cancelable;
	}

	private void httpPost(RequestParams params, final int other) {

		params.addBodyParameter("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
		params.addBodyParameter("uak", Constants.UAK);
		List<KeyValue> keyList = params.getQueryStringParams();
		String[] keystr = new String[keyList.size()];
		for(int i = 0;i<keyList.size();i++){
			keystr[i] = keyList.get(i).key;
		}
		Arrays.sort(keystr, String.CASE_INSENSITIVE_ORDER);
		StringBuffer str = new StringBuffer();
		for(int i = 0;i<keystr.length;i++){
			str.append(keystr[i]).append("=").append(params.getStringParameter(keystr[i])).append("&");
		}
		String ak = MD5.md5(str.toString().substring(0, str.length() - 1));
		params.addQueryStringParameter("ak", ak);
		params.removeParameter("uak");
		// params.addQueryStringParameter("wd", "xUtils");
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					LogUtil.e("tag", result);
					JSONObject jsonObj = new JSONObject(result);
					int status = jsonObj.getInt(HttpUtil.Status);
					String info = jsonObj.getString(HttpUtil.Info);
					if (status == 1) {
						List<HolderInfo> holderInfoList = new ArrayList<HolderInfo>();
						JSONArray jsonArray = jsonObj.getJSONArray(HttpUtil.Data);
						for (int i = 0; i < jsonArray.length(); i++) {
							HolderInfo holderInfo = new HolderInfo();
							holderInfo.aid = jsonArray.getJSONObject(i).getString("aid");
							holderInfo.a_title = jsonArray.getJSONObject(i).getString("a_title");
							holderInfo.a_size = jsonArray.getJSONObject(i).getString("a_size");
							holderInfo.a_desc = jsonArray.getJSONObject(i).getString("a_desc");
							holderInfo.a_url = jsonArray.getJSONObject(i).getString("a_url");

							holderInfoList.add(holderInfo);

						}
						LogUtil.e("holderInfoList="+holderInfoList.size());
						if(other == 101){
							mHandler.sendMessage(mHandler.obtainMessage(102, holderInfoList));
						}else if(other == 103){
							mHandler.sendMessage(mHandler.obtainMessage(103, holderInfoList));
						}
						//Toast.makeText(x.app(), "ArrayList", Toast.LENGTH_LONG).show();
					}else{
						ToastUtils.showLongToast(context, getResources().getString(R.string.history_no_data));
						//mHandler.sendMessage(mHandler.obtainMessage(110));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mHandler.sendMessage(mHandler.obtainMessage(110));
				ToastUtils.showLongToast(context, getResources().getString(R.string.service_error));
			}

			@Override
			public void onCancelled(CancelledException cex) {
				//ToastUtils.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFinished() {

			}
		});
	}

	/**
	 * 安装apk
	 * @param fileNmae
	 */
	private  void azApk(String fileNmae){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + createAPKFolder(fileNmae)),"application/vnd.android.package-archive");
		startActivity(intent);
	}
	/**
	 * 获取apk保存 地址
	 */
	public static  String createAPKFolder(String appName){
		File dirPath = Environment.getExternalStorageDirectory();
		File fileCache = new File(dirPath.toString(), "apk");
		if (!fileCache.exists()) {
			fileCache.mkdir();
		}

		File filePath = new File(fileCache.toString(), appName+".apk");
		if (!filePath.exists())
			try {
				filePath.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 获取文件名


		return filePath.getPath();

	}
	private static final class Cache {
		ArrayList<HolderInfo> cacheList;

		public Cache() {
			cacheList = new ArrayList<HolderInfo>();
		}

		synchronized ArrayList<HolderInfo> generateLocalList() {
			ArrayList<HolderInfo> local = new ArrayList<HolderInfo>();
			local.addAll(cacheList);
			return local;
		}

		synchronized void update(ArrayList<HolderInfo> list) {
			for (HolderInfo info : list) {
				cacheList.add(info);
			}
		}
		synchronized void update_nextPullto(ArrayList<HolderInfo> list) {
			for (HolderInfo info : list) {
				//cacheList.add(info);
				cacheList.add(0, info);
			}
		}

		synchronized void clearAndUpdate(ArrayList<HolderInfo> list) {
			cacheList.clear();
			for (HolderInfo info : list) {
				cacheList.add(info);
			}
		}

		synchronized void clear() {
			cacheList.clear();
		}
	}
}
