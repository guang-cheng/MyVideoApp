package com.qxshikong.myvideoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qxshikong.myvideoapp.pulltorefresh.XListView;
import com.qxshikong.myvideoapp.util.Constants;
import com.qxshikong.myvideoapp.util.HttpUtil;
import com.qxshikong.myvideoapp.util.LoadDialog;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ContentView(R.layout.act_history)
public class HistorActivity extends BaseActivity  implements XListView.IXListViewListener {

	@ViewInject(R.id.title_layout_tv)
	private TextView tv_title;
	@ViewInject(R.id.main_rightLayout)
	private LinearLayout layout_right;

	@ViewInject(R.id.listview)
	private XListView xlistview;
	private ArrayAdapter<HolderInfo> adapter;
	private static ImageLoader imageLoader;
	private static DisplayImageOptions options;
	private Cache cache;	private LayoutInflater layoutInflater;
	private static Context context;

	private String type;
	private static String IMSI;
	private int page = 1;

	private LoadDialog loadDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		context = this;
		cache = new Cache();
		layoutInflater = layoutInflater.from(this);
		imageLoader = ImageLoader.getInstance();
		options = UsedUtil.getDisplayImageOptions();
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		type = intent.getStringExtra("type");
		if(type .equals("0")) {
			type = "collection";
		}else if(type .equals("1")) {
			type = "history";
			layout_right.setVisibility(View.VISIBLE);
		}
		IMSI = UsedUtil.getIMSI(context);
		if(title != null)
		tv_title.setText(title);

		xlistview.setPullLoadEnable(true);
		xlistview.setPullRefreshEnable(false);
		xlistview.setXListViewListener(this);
		adapter = new ArrayAdapter<HolderInfo>(this,
				R.layout.item_listview_history) {

			public android.view.View getView(int position,
											 android.view.View convertView, android.view.ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.item_listview_history, null);
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
		loadDialog();
			page();
	}

	private void loadDialog(){
		loadDialog = new LoadDialog(this,R.style.MyDialogStyle);

		loadDialog.show();
	}
	private void isLoadDialog(){
		if(loadDialog != null && loadDialog.isShowing()){
			loadDialog.dismiss();
			loadDialog = null;
		}
	}
	/**
	 * 请求数据
	 * @param
	 */
	private void page( ){

		if(IMSI == null){
			isLoadDialog();
			ToastUtils.showLongToast(this,getResources().getString(R.string.login_faile));
			return;
		}
		RequestParams params = new RequestParams(HttpUtil.History_URL);
		params.addBodyParameter("u_imsi",IMSI);
		params.addBodyParameter("type",type);
		params.addBodyParameter("page",String.valueOf(page));
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
		LogUtil.e("main_========rightLayout");
		loadDialog();
		RequestParams params = new RequestParams(HttpUtil.DelHistory_URL);
		params.addBodyParameter("u_imsi",IMSI);

		httpPost(params,102);
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
		page();
	}

	private final static class HolderInfo {
		String vid;
		String v_title;//
		String v_video_link;// 的、video地址
		String v_pic_link;//
		String v_video_time;// 时常
		String v_clicks;// 点击次数
		String v_createTime;// 文章详情
		String v_is_vip;
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
		@ViewInject(R.id.iv_item)
		ImageView iv_item;

		@ViewInject(R.id.tv_time)
		TextView tv_time;
		@ViewInject(R.id.tv_topic)
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
			if(item.v_pic_link != null && item.v_pic_link.startsWith("http"))
				imageLoader.displayImage(item.v_pic_link, iv_item, options);
			if(item.v_title != null)
				tv_title.setText(item.v_title);
			/*if(item.v_clicks != null)
				tv_play_count.setText(item.v_clicks);*/
			if(item.v_video_time != null)
				tv_time.setText(item.v_video_time);

		}
		@Event(value = R.id.ll_item1 ,type = View.OnClickListener.class)
		private void  ll_itemClick(View view){
			if(IMSI == null){
				ToastUtils.showLongToast(context,context.getResources().getString(R.string.login_faile));
				return;
			}
			Intent intent = new Intent(context, VideoActivity.class);
			intent.putExtra("url",url);
			intent.putExtra("vid",vid);
			intent.putExtra("title",title);
			intent.putExtra("pic",pic);
			intent.putExtra("v_is_vip",v_is_vip);
			context.startActivity(intent);
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
					xlistview.setAdapter(adapter);
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
				case 104:
					isLoadDialog();
					//ToastUtils.showLongToast(context, getResources().getString(R.string.history_clear_success));
					if(adapter != null){
						adapter.clear();
						adapter.notifyDataSetChanged();
					}

					break;
				case 110:

					xlistview.stopLoadMore();
					break;
				case 111:
					//刷新或 加载失败
					xlistview.stopRefresh();
					xlistview.stopLoadMore();

					xlistview.setRefreshTime(getResources().getString(R.string.xlistview_footer_hint_more_time));
					xlistview.setmFooterTextView(getResources().getString(R.string.xlistview_footer_no_more));
					//ToastUtils.showLongToast(activity, "加载失败");
					break;
				case 112:
					//刷新或 加载失败
					xlistview.stopRefresh();
					xlistview.stopLoadMore();
					xlistview.setRefreshTime(getResources().getString(R.string.xlistview_footer_hint_more_time));
					xlistview.setmFooterTextView(getResources().getString(R.string.xlistview_footer_more_faile));
					//ToastUtils.showLongToast(activity, "加载失败");
					break;
				default:
					break;
			}

		};
	};



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
					isLoadDialog();
					if (status == 1) {
						if(other == 101) {
							List<HolderInfo> holderInfoList = new ArrayList<HolderInfo>();
							JSONArray jsonArray = jsonObj.getJSONArray(HttpUtil.Data);
							for (int i = 0; i < jsonArray.length(); i++) {
								HolderInfo holderInfo = new HolderInfo();
								holderInfo.vid = jsonArray.getJSONObject(i).getString("vid");
								holderInfo.v_title = jsonArray.getJSONObject(i).getString("v_title");
								holderInfo.v_video_link = jsonArray.getJSONObject(i).getString("v_video_link");
								holderInfo.v_pic_link = jsonArray.getJSONObject(i).getString("v_pic_link");
								holderInfo.v_createTime = jsonArray.getJSONObject(i).getString("createTime");
								holderInfo.v_is_vip = jsonArray.getJSONObject(i).getString("v_is_vip");
								holderInfoList.add(holderInfo);

							}
							page++;
							mHandler.sendMessage(mHandler.obtainMessage(102, holderInfoList));
							LogUtil.e("holderInfoList=" + holderInfoList.size());
						}else if(other == 102){
							ToastUtils.showLongToast(context, getResources().getString(R.string.history_clear_success));
							mHandler.sendMessage(mHandler.obtainMessage(104));
						}/*else if(other == 103){
							mHandler.sendMessage(mHandler.obtainMessage(103, holderInfoList));
						}*/
						//Toast.makeText(x.app(), "ArrayList", Toast.LENGTH_LONG).show();
					}else{
						if(other == 102){
							ToastUtils.showLongToast(context, getResources().getString(R.string.history_clear_faile));
						}else {
							ToastUtils.showLongToast(context, getResources().getString(R.string.history_no_data));
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
				if(other == 101 && page > 1){
					mHandler.sendMessage(mHandler.obtainMessage(112));
				}else if(other == 102){
					//isLoadDialog();
				}else
				mHandler.sendMessage(mHandler.obtainMessage(110));
				isLoadDialog();
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
