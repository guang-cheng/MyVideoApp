package com.qxshikong.myvideoapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qxshikong.myvideoapp.R;
import com.qxshikong.myvideoapp.VideoActivity;
import com.qxshikong.myvideoapp.util.Constants;
import com.qxshikong.myvideoapp.util.DeviceInfo;
import com.qxshikong.myvideoapp.util.HttpUtil;
import com.qxshikong.myvideoapp.util.LogUtil;
import com.qxshikong.myvideoapp.util.ToastUtils;
import com.qxshikong.myvideoapp.util.UsedUtil;
import com.qxshikong.myvideoapp.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoGridFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener{

	@ViewInject(R.id.swipe_container)
	private SwipeRefreshLayout mSwipeLayout;
	@ViewInject(R.id.gridview)
	private MyGridView gridview;
	@ViewInject(R.id.MainGridViewScroll)
	private ScrollView scrollView;
	@ViewInject(R.id.MainGridViewScrollLinear)
	private LinearLayout ll_Scrollview_layout;
	@ViewInject(R.id.MainGridViewFooterLinear)
	private LinearLayout ll_footlayout;
	@ViewInject(R.id.tv_loadmore)
	private TextView tv_loadmore;


	private final String TAG = "VideoGridFragment";
	/** 是fragment 懒加载已加载过 */
	private boolean isLoad;
	// 标志位，标志已经初始化完成。
	private boolean isPrepared;
	private static Activity activity;
	private String httpURL = HttpUtil.News_URL;
	private ArrayAdapter<HolderInfo> adapter;
	private static ImageLoader imageLoader;
	private static DisplayImageOptions options;
	private Cache cache;
	private int page = 1;
	private static int imageWidth;
	private static int imageHeight;

	private static String IMSI;
	public VideoGridFragment(){

	}
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.frg_news, null);
		x.view().inject(this, view);
		activity = this.getActivity();
		String order = getArguments().getString("order");
		LogUtil.e("ORDER="+order);
		if(order != null){
			if("hot".equals(order)){
				httpURL = HttpUtil.Hot_URL;
			}else if("vip".equals(order)){
				httpURL = HttpUtil.VIP_URL;
			}else{
				httpURL = HttpUtil.News_URL;
			}
		}
		IMSI = UsedUtil.getIMSI(activity);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		isPrepared = true;
		imageLoader = ImageLoader.getInstance();
		options = UsedUtil.getDisplayImageOptions();
		cache = new Cache();
		imageWidth = (DeviceInfo.screenWidth - DeviceInfo.dip2px(31))/2;
		imageHeight = (int)(imageWidth*0.618);
		//下拉判断
		scrollView.setOnTouchListener(new View.OnTouchListener() {

			private int lastY = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					lastY = scrollView.getScrollY() + 100;
					LogUtil.e("tag", "lastY=" + lastY);
					if (lastY > (ll_Scrollview_layout.getHeight() - scrollView.getHeight()) ) {
						LogUtil.e("tag", "ll.getHeight()=" + ll_Scrollview_layout.getHeight() + ",sv.getHeight())=" + scrollView.getHeight() + ",lastY=" + lastY);
						tv_loadmore.setText(getResources().getString(R.string.loadmore_loading));
						ll_footlayout.setVisibility(View.VISIBLE);
						//addMoreData();
						//loadMoreData(103);
						page();

					}
				}
				return false;
			}
		});

		adapter = new ArrayAdapter<HolderInfo>(inflater.getContext(),
				R.layout.item_gridview_home) {

			public android.view.View getView(int position,
									 android.view.View convertView, android.view.ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_gridview_home, null);
			holder = new ViewHolder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HolderInfo itm = getItem(position);
		holder.initview(itm);

		return convertView;
	}

};
		lazyLoad();

		//onRefresh();
		
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*
		 * setRetainInstance(true); //set the preference xml to the content view
		 * addPreferencesFromResource(R.xml.menu);
		 */

	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub

		if (!isPrepared || !isVisible) {

			return;
		}

		if (!isLoad) {
			isLoad = true;
			mHandler.sendMessage(mHandler.obtainMessage(113));
			RequestParams params = new RequestParams(httpURL);
			LogUtil.e(TAG, "URL===" + httpURL );
		httpPost(params, 103);
		}
		LogUtil.e(TAG, "lazyLoad=false=" + httpURL + ",isPrepared=" + isPrepared + ",isVisible=" + isVisible);

	}

	private void page(){
		RequestParams params = new RequestParams(httpURL );
		params.addBodyParameter("page", String.valueOf(page));

		httpPost(params, 101);
	}

	@Override
	public void onRefresh() {
		RequestParams params = new RequestParams(httpURL);

		httpPost(params, 103);
	}

	private final static class HolderInfo {
		String vid;
		String v_title;//
		String v_video_link;// 的、video地址
		String v_pic_link;//
		String v_video_time;// 时常
		String v_clicks;// 点击次数
		String v_createTime;// 文章详情
		String v_is_vip;//是否是vip类型

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
	public static class ViewHolder {
		/*
		 * @ViewInject(R.id.ll_item) LinearLayout linearlayout;
		 */
		@ViewInject(R.id.iv_video)
		ImageView iv_video;
		@ViewInject(R.id.tv_play_count)
		TextView tv_play_count;
		@ViewInject(R.id.tv_time_length)
		TextView tv_time_length;
		@ViewInject(R.id.tv_title)
		TextView tv_title;

		String url;
		String vid;
		String title;
		String pic;
		String v_is_vip;
		public void initview(HolderInfo item) {
			url = item.v_video_link;
			vid = item.vid;
			title = item.v_title;
			pic = item.v_pic_link;
			v_is_vip = item.v_is_vip;
			iv_video.setLayoutParams(new LinearLayout.LayoutParams(imageWidth ,imageHeight));
			if(item.v_pic_link != null && item.v_pic_link.startsWith("http"))
				imageLoader.displayImage(item.v_pic_link,iv_video,options);
			if(item.v_title != null)
				tv_title.setText(item.v_title);
			if(item.v_clicks != null)
				tv_play_count.setText(item.v_clicks);
			if(item.v_video_time != null)
				tv_time_length.setText(item.v_video_time);

		}
			@Event(value = R.id.ll_item ,type = View.OnClickListener.class)
			private void  ll_itemClick(View view){
				if(IMSI == null){
					ToastUtils.showLongToast(activity,activity.getResources().getString(R.string.login_faile));
					return;
				}

				Intent intent = new Intent(activity, VideoActivity.class);
				intent.putExtra("url",url);
				intent.putExtra("vid",vid);
				intent.putExtra("title",title);
				intent.putExtra("pic",pic);
				intent.putExtra("v_is_vip",v_is_vip);

				activity.startActivity(intent);
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
					adapter.addAll(cache.generateLocalList());
				}
				gridview.setAdapter(adapter);
				sendEmptyMessage(110);
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
			 case 110:
				 mSwipeLayout.setRefreshing(false);//刷新停止

                 /*xlistview.stopRefresh();
                 xlistview.stopLoadMore();
                 xlistview.setRefreshTime("刚刚");*/
                 break;
			 case 111:
				 //加载失败
				 tv_loadmore.setText(getResources().getString(R.string.loadmore_nodata));
				 break;
			 case 112:
					//网络错误请重试
					tv_loadmore.setText(getResources().getString(R.string.loadmore_loading_faile));
					break;
			case 113:
					//自动下拉操作
				mSwipeLayout.setRefreshing(true);//刷新停止
					break;

			default:
				break;
			}

		};
	};

	private void httpPost(RequestParams params, final int other) {
		if(HttpUtil.VIP_URL.equals(httpURL)){
			if(IMSI == null){
				ToastUtils.showLongToast(activity,activity.getResources().getString(R.string.login_faile));
				mSwipeLayout.setRefreshing(false);//刷新停止
				return;
			}else{
				params.addBodyParameter("imsi", IMSI);
			}
		}
		params.addBodyParameter("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
		params.addBodyParameter("uak", Constants.UAK);
		List<KeyValue> keyList = params.getQueryStringParams();
		String[] keystr = new String[keyList.size()];
		for(int i = 0;i<keyList.size();i++){
			LogUtil.e(keyList.get(i).toString());
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

		//LogUtil.e("ak=" + ak);
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
								holderInfo.vid = jsonArray.getJSONObject(i).getString("vid");
								holderInfo.v_title = jsonArray.getJSONObject(i).getString("v_title");
								String video_url = jsonArray.getJSONObject(i).getString("v_video_link");
								holderInfo.v_video_link = video_url;
								holderInfo.v_pic_link = jsonArray.getJSONObject(i).getString("v_pic_link");
								holderInfo.v_video_time = jsonArray.getJSONObject(i).getString("v_video_time");
								holderInfo.v_clicks = jsonArray.getJSONObject(i).getString("v_clicks");
								holderInfo.v_createTime = jsonArray.getJSONObject(i).getString("v_createTime");
								holderInfo.v_is_vip = jsonArray.getJSONObject(i).getString("v_is_vip");
									holderInfoList.add(holderInfo);

							}
							LogUtil.e("holderInfoList="+holderInfoList.size());
							if(other == 101){
								page++;
								mHandler.sendMessage(mHandler.obtainMessage(102, holderInfoList));
							}else if(other == 103){
								page = 1 + 1;
								mHandler.sendMessage(mHandler.obtainMessage(103, holderInfoList));
							}
						//Toast.makeText(x.app(), "ArrayList", Toast.LENGTH_LONG).show();
					}else{
						if(other == 103) {
							ToastUtils.showLongToast(activity,getResources().getString(R.string.history_no_data));
							mHandler.sendMessage(mHandler.obtainMessage(110));
						}else {
							mHandler.sendMessage(mHandler.obtainMessage(111));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if(other==103)
					mHandler.sendMessage(mHandler.obtainMessage(110));
				else
					mHandler.sendMessage(mHandler.obtainMessage(112));
				ToastUtils.showLongToast(activity,getResources().getString(R.string.service_error));

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
