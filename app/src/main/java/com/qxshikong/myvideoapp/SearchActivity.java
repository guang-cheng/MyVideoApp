package com.qxshikong.myvideoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qxshikong.myvideoapp.util.Constants;
import com.qxshikong.myvideoapp.util.DeviceInfo;
import com.qxshikong.myvideoapp.util.HttpUtil;
import com.qxshikong.myvideoapp.util.LoadDialog;
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
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ContentView(R.layout.act_search)
public class SearchActivity extends BaseActivity {

	@ViewInject(R.id.gridview)
	private MyGridView search_gridview;

	@ViewInject(R.id.MainGridViewScroll)
	private ScrollView scrollView;
	@ViewInject(R.id.MainGridViewScrollLinear)
	private LinearLayout ll_Scrollview_layout;
	@ViewInject(R.id.MainGridViewFooterLinear)
	private LinearLayout ll_footlayout;
	@ViewInject(R.id.tv_loadmore)
	private TextView tv_loadmore;

	@ViewInject(R.id.search_listview)
	private ListView search_listview;
	@ViewInject(R.id.searchBtn)
	private ImageView searchBtn;
	@ViewInject(R.id.keywordEt)
	private EditText keywordEt;
	private  String editString;
	private boolean isEtlengtn;

	private ArrayAdapter<String> adapter;
	private ArrayAdapter<HolderInfo> gridViewAdapter;
	private  static Context context;

	private int size;
	private boolean isLoadMore;
	private static ImageLoader imageLoader;
	private static DisplayImageOptions options;
	private int page = 1;
	private String tid;
	private static int imageWidth;
	private static int imageHeight;
	private LoadDialog loadDialog;
	private static String IMSI;
	private Cache cache;
	 LayoutInflater inflater = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.act_search);
		x.view().inject(this);

		context = this;
		cache = new Cache();
		isLoadMore = false;
		imageLoader = ImageLoader.getInstance();
		options = UsedUtil.getDisplayImageOptions();
		imageWidth = (DeviceInfo.screenWidth - DeviceInfo.dip2px(31))/2;
		imageHeight = (int)(imageWidth*0.618);
		inflater = LayoutInflater.from(this);
		IMSI = UsedUtil.getIMSI(context);
		adapter = new ArrayAdapter<String>(inflater.getContext(),
				R.layout.item_search_adapte) {

			public android.view.View getView(int position,
											 android.view.View convertView, android.view.ViewGroup parent) {
				Holder holder = null;
				if (convertView == null) {
					convertView = inflater.inflate(
							R.layout.item_search_adapte, null);
					holder = new Holder();
					x.view().inject(holder, convertView);
					convertView.setTag(holder);
				} else {
					holder = (Holder) convertView.getTag();
				}

				String itm = getItem(position);
				// if(!TextUtils.isEmpty(itm.image))
				holder.initView(itm);
				return convertView;
			}

		};
		gridViewAdapter = new ArrayAdapter<HolderInfo>(this,
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
		Intent intent = getIntent();
		if(intent.hasExtra("tid")) {
			keywordEt.setFocusableInTouchMode(false);
			editString = intent.getStringExtra(HttpUtil.Search);
			 tid = intent.getStringExtra(HttpUtil.Tid);
			if (tid != null) {
				loadMoreData(103);

			}
		}

		RequestParams params = new RequestParams(HttpUtil.Search_LIST_URL );
		httpPost(params, 101);
		View view = inflater.inflate(R.layout.layout_search_footer,null);
		search_listview.addFooterView(view);
		view.findViewById(R.id.search_clear_history).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				search_listview.setVisibility(View.GONE);

			}
		});


		keywordEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub
				/*
				 * LogUtil.e("tag", "start1======"+start); LogUtil.e("tag",
				 * "count1======"+count); LogUtil.e("tag",
				 * "after1======"+after);
				 */
				if (!isEtlengtn && (keywordEt.getText().toString().trim().length() > 0)) {
					isEtlengtn = true;
					mHandler.sendMessage(mHandler.obtainMessage(1));
				} else {
					isEtlengtn = false;
					mHandler.sendMessage(mHandler.obtainMessage(1));
				}

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				// TODO Auto-generated method stub
				/*
				 * LogUtil.e("tag", "start="+start); LogUtil.e("tag",
				 * "before="+before); LogUtil.e("tag", "count="+count);
				 */
			}
		});
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
						loadMoreData(103);

					}
				}
				return false;
			}
		});
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


	@Event(value = R.id.btn_back)
	private void btn_backClick(View view){
		finish();
	}


	@Event(value = R.id.searchBtn)
    private  void searchBtnClick(View viwe){
		editString = keywordEt.getText().toString().trim();

		if(editString == null || "".equals(editString)) {
			ToastUtils.showShortToast(this,"请输入搜索关键词");
			return;
		}
		tid = null;
		loadDialog();
		LogUtil.e("tag","page====="+page);
		page = 1;
		RequestParams params = new RequestParams(HttpUtil.Search_URL);
		String u8 = "";
		try {
			u8 = URLEncoder.encode(editString,"utf-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("search", u8);
		params.addBodyParameter("page", String.valueOf(1));
		search_listview.setVisibility(View.GONE);
		httpPost(params, 104);
	}

	/**
	 * 加载数据
	 */
	private void loadMoreData(int other){
		LogUtil.e("load---------MoreData");
		if(tid != null){
			pageTid(other);
			return;
		}

		if(editString == null || "".equals(editString)) {
			ToastUtils.showShortToast(this,"请输入搜索关键词");
			return;
		}

		page(editString, other);
	}

	/**
	 * 加载数据
	 */
	private void pageTid(int other){
		RequestParams params = new RequestParams(HttpUtil.TID_URL);
		params.addBodyParameter("tags", tid);
		params.addBodyParameter("page", String.valueOf(page));
		search_listview.setVisibility(View.GONE);

		httpPost(params, other);
	}

	private void page(String search,int other){
		size = 0;

		if(other == 104)
			page = 1;
		RequestParams params = new RequestParams(HttpUtil.Search_URL);
		String u8 = "";
		try {
			u8 = URLEncoder.encode(editString,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("search", u8);
		params.addBodyParameter("page", String.valueOf(page));
		search_listview.setVisibility(View.GONE);

		httpPost(params, other);
	}

	private final class Holder {

		@ViewInject(R.id.search_item_word_tv)
		TextView search_item_word_tv;
		/*@ViewInject(R.id.search_relativeLayout)
		RelativeLayout search_relativeLayout;*/
        String title;

		public void initView(String title2) {
			title = title2;
			if(title != null)
			search_item_word_tv.setText(title);

		}
		@Event(value = R.id.search_Layout)
		private void search_relativeClick(View v){
			//if(itm.search_item_word_tv != null)
				//search(itm.search_item_word_tv);
			//LogUtil.e("title ="+title);
			if(title != null) {
				keywordEt.setText(title);
				editString = title;
				page(title, 104);

			}
		}

		Holder(){

		}

	}

	@Event(value = R.id.keywordEt)
	private void keywordEt(View v) {
		keywordEt.setFocusableInTouchMode(true);
		search_listview.setVisibility(View.VISIBLE);

	}




	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					LogUtil.e("tag", "et===" + keywordEt.getText());
					String et = keywordEt.getText().toString().trim();
					/*if (et.length() > 0)
						searchBtn.setText(res.getString(R.string.search));
					else
						searchBtn.setText(res.getString(R.string.cancel));*/
					break;
				case 101:
					ArrayList<String> listStirng = (ArrayList<String>) msg.obj;
						if(adapter != null){
							adapter.clear();
							for(int i=0;i<listStirng.size();i++){
								adapter.add(listStirng.get(i));
							}
						}
					adapter.notifyDataSetChanged();
					search_listview.setAdapter(adapter);
					break;
				case 102:

					gridViewAdapter.clear();
					if(gridViewAdapter != null){
						gridViewAdapter.addAll(cache.generateLocalList());
					}
					size = gridViewAdapter.getCount();
					search_gridview.setAdapter(gridViewAdapter);
					break;
				case 103:
					ll_footlayout.setVisibility(View.GONE);
					search_listview.setVisibility(View.GONE);
					//加载数据
					cache.update((ArrayList<HolderInfo>) msg.obj);

					sendEmptyMessage(102);
					/*for(int i = 0;i<10;i++){
						holderInfos.add(holderInfos.get(0));
					}*/

					//onCompletedSucceed(holderInfos);


					break;
				case 104:
					LogUtil.e("page==========="+page);
					search_listview.setVisibility(View.GONE);
					//加载数据
					cache.clear();
					cache.update((ArrayList<HolderInfo>) msg.obj);
					sendEmptyMessage(102);


					break;
				case 105:
					//空的
					search_listview.setVisibility(View.GONE);

					if(msg.arg2 == 104) {
						cache.clear();
						gridViewAdapter.clear();
						gridViewAdapter.notifyDataSetChanged();
						ToastUtils.showShortToast(SearchActivity.this, "没有搜索到内容");
					}else {
						tv_loadmore.setText(getResources().getString(R.string.loadmore_nodata));
						ToastUtils.showShortToast(SearchActivity.this, "没有更多内容了");
						//gridViewAdapter.setFootreViewEnable(false);
					}

					break;


				default:
					break;
			}

		};
	};

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


	private void httpPost(RequestParams params, final int other) {
		params.addBodyParameter("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
		params.addBodyParameter("uak", Constants.UAK);
		List<KeyValue> keyList = params.getQueryStringParams();
		String[] keystr = new String[keyList.size()];
		boolean isSearch = false;
		for(int i = 0;i<keyList.size();i++){
			keystr[i] = keyList.get(i).key;
			if("search".equals(keystr[i]))
				isSearch = true;
		}
		Arrays.sort(keystr, String.CASE_INSENSITIVE_ORDER);
		StringBuffer str = new StringBuffer();
		for(int i = 0;i<keystr.length;i++){
			str.append(keystr[i]).append("=").append(params.getStringParameter(keystr[i])).append("&");
		}
		String ak = MD5.md5(str.toString().substring(0, str.length() - 1));
		params.addQueryStringParameter("ak", ak);
		if(isSearch){
			params.removeParameter("search");
			params.addQueryStringParameter("search", editString);
		}
		// params.addQueryStringParameter("wd", "xUtils");
		params.removeParameter("uak");

	/*	List<KeyValue> keyList2 = params.getQueryStringParams();
		for (int i = 0; i < keyList2.size(); i++) {
			LogUtil.e(keyList2.get(i).toString());
		}
*/
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
						if(other == 101){
						List<String> listString = new ArrayList<String>();
						JSONArray jsonArray = jsonObj.getJSONArray(HttpUtil.Data);
						for (int i = 0; i < jsonArray.length(); i++) {

							listString.add(jsonArray.getJSONObject(i).getString("s_title"));

						}

							mHandler.sendMessage(mHandler.obtainMessage(101, listString));
						}else if(other == 103 || other == 104){
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
							if(other == 103) {
								page++;
								mHandler.sendMessage(mHandler.obtainMessage(103, holderInfoList));
							}else {
								page = 1;
								mHandler.sendMessage(mHandler.obtainMessage(104, holderInfoList));
							}

						}
						//Toast.makeText(x.app(), "ArrayList", Toast.LENGTH_LONG).show();
					}else{
						mHandler.sendMessage(mHandler.obtainMessage(105,0,other));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mHandler.sendMessage(mHandler.obtainMessage(110));
				isLoadDialog();
				ToastUtils.showLongToast(SearchActivity.this, getResources().getString(R.string.service_error));
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
