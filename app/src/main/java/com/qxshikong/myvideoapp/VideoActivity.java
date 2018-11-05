package com.qxshikong.myvideoapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qxshikong.myvideoapp.util.Constants;
import com.qxshikong.myvideoapp.util.DeviceInfo;
import com.qxshikong.myvideoapp.util.HttpUtil;
import com.qxshikong.myvideoapp.util.LoadDialog;
import com.qxshikong.myvideoapp.util.LogUtil;
import com.qxshikong.myvideoapp.util.MobPay;
import com.qxshikong.myvideoapp.util.ToastUtils;
import com.qxshikong.myvideoapp.util.UsedUtil;
import com.qxshikong.myvideoapp.view.CustomVideoView;
import com.qxshikong.myvideoapp.view.FlowLayout;
import com.qxshikong.myvideoapp.view.MediaController;
import com.qxshikong.myvideoapp.view.MyDialog;
import com.qxshikong.myvideoapp.view.MyGridView;

import org.exp.in.OutMd;
import org.exp.in.fbin;
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

@ContentView(R.layout.act_video)
public class VideoActivity extends BaseActivity  implements MediaPlayer.OnPreparedListener {
	@ViewInject(R.id.videoView)
	private CustomVideoView video;
	//@ViewInject(R.id.progressBar)
	//private ProgressBar progressBar;
	@ViewInject(R.id.layout_content)
	private LinearLayout layout_content;
	@ViewInject(R.id.video_layout)
	private RelativeLayout videoLayout;

	@Nullable
	@Override
	public CharSequence onCreateDescription() {
		return super.onCreateDescription();
	}

	@ViewInject(R.id.iv_pic)
	private ImageView iv_pic;

	private boolean ispaly;
	private MyDialog dialog = null;
	@ViewInject(R.id.iv_video_play)
	private ImageView iv_paly;//标签

	@ViewInject(R.id.iv_collection)
	private ImageView iv_collection;//收藏
	private boolean isCollection;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;//标签

	@ViewInject(R.id.flowLayout)
	private FlowLayout flowLayout;//标题
	@ViewInject(R.id.gridview)
	private MyGridView gridview;//标题

	private MediaController mMediaController;
	private Configuration cf;

	private LayoutInflater layoutInflater;


	private ArrayAdapter<HolderInfo> adapter;
	private static ImageLoader imageLoader;
	private static DisplayImageOptions options;

	private int currentPosition = 0;//当前播放的时间

	private int page = 1;
	private static int imageWidth;
	private static int imageHeight;
	private static Context context;
	private String IMSI;
	private String vid;
	private String pic;
	//private String is_vip;//1 vip视频 0 不是
	private LoadDialog loadDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		context= this;
		layoutInflater = LayoutInflater.from(this);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String url = intent.getStringExtra("url");
		 vid = intent.getStringExtra("vid");
		if(intent.hasExtra("pic"))
		pic = intent.getStringExtra("pic");
		//is_vip = intent.getStringExtra("v_is_vip");
		imageLoader = ImageLoader.getInstance();
		options = UsedUtil.getDisplayImageOptions();
		handler.sendMessage(handler.obtainMessage(10));
		imageWidth = (DeviceInfo.screenWidth - DeviceInfo.dip2px(31))/2;
		imageHeight = (int)(imageWidth*0.618);
		if(title != null)
			tv_title.setText(title);
		url = getURL(url);
		cf = this.getResources().getConfiguration();
		Uri uri = Uri.parse(url);
		video.setVideoURI(uri);
		video.setOnPreparedListener(this);
		mMediaController = new MediaController(this);

		video.setMediaController(mMediaController);
		video.setFocusableInTouchMode(false);
		video.setFocusable(false);
		video.setEnabled(false);
		//videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.shouyekaichang));
		//videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);//高画质


		video.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {

			public void onPlay() {
				LogUtil.e("video is playing");
				iv_paly.setVisibility(View.GONE);
				iv_pic.setVisibility(View.GONE);
				videoPause();
			}

			public void onPause() {
				// TODO Auto-generated method stub
				//LogUtil.e("video is paused");
				iv_paly.setVisibility(View.VISIBLE);
			}
		});
		video.start();
		video.pause();
		//video.requestFocus();


		IMSI = UsedUtil.getIMSI(this);
		LogUtil.e("vid==" + vid+",imsi="+IMSI);
		RequestParams params = new RequestParams(HttpUtil.Details_URL);
		params.addBodyParameter("vid",vid);
		params.addBodyParameter("u_imsi",IMSI);

		httpPost(params, 101);

		adapter = new ArrayAdapter<HolderInfo>(layoutInflater.getContext(),
				R.layout.item_gridview_home) {

			public android.view.View getView(int position,
											 android.view.View convertView, android.view.ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.item_gridview_home, null);
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
	private void videoPause(){
		if (currentPosition > 15000 && !"1".equals(UsedUtil.VIP) ) {
			video.pause();
			dialog();
		}
	}

	/**
	 * 解析加密的视频
	 * @param str
	 * @return
	 */
	private String getURL(String str){
		String url = str.substring(0,20) +str.substring(35,str.length());
		// 对base64加密后的数据进行解密
		url = new String(Base64.decode(url.getBytes(), Base64.DEFAULT));
		LogUtil.e("URL=" + url);
		return url;
	}


	 Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 0:
					if(video != null)
					video.pause();
					break;
				case 1:
					/*if(video.isPlaying()){
						iv_paly.setVisibility(View.GONE);
						progressBar.setVisibility(View.GONE);
						tv_progree.setVisibility(View.GONE);
					}else{
						iv_paly.setVisibility(View.VISIBLE);
						int progree = video.getBufferPercentage();
						if(progree < 100) {
							progressBar.setVisibility(View.VISIBLE);
							LogUtil.e("Time==progree======"+progree);
							progressBar.setProgress(progree);
							tv_progree.setText(progree+"%");
						}else{
							progressBar.setVisibility(View.GONE);
							tv_progree.setVisibility(View.GONE);
						}
					}*/
					break;
				case 10:
					if(pic != null)
					imageLoader.displayImage(pic,iv_pic,options);
					break;
				case 101:
					//标签显示
					final ArrayList<TagInfo> holderInfoList = (ArrayList<TagInfo>) msg.obj;
					for(int i = 0;i < holderInfoList.size();i++) {
						final TagInfo info = holderInfoList.get(i);
						View view = layoutInflater.inflate(R.layout.layout_tags_button, null);
						Button tagBtn = (Button) view.findViewById(R.id.btn_tags);
						tagBtn.setText(holderInfoList.get(i).t_title);
						tagBtn.setOnClickListener(new  View.OnClickListener(){
							@Override
							public void onClick(View view) {
								Intent intent = new Intent(VideoActivity.this, SearchActivity.class);
								intent.putExtra(HttpUtil.Search,info.t_title);
								intent.putExtra(HttpUtil.Tid,info.tid);
								startActivity(intent);
							}
						});
						flowLayout.addView(view);
					}
					break;
				case 102:
					String tids = msg.obj.toString();
					tids = tids.substring(1,tids.length()-1);

					tids = tids.replace(",","%2C");

					LogUtil.e("tids==" + tids);
					RequestParams params = new RequestParams(HttpUtil.TID_URL);
					params.addBodyParameter("tags", tids);
					params.addBodyParameter("vid", vid);
					params.addBodyParameter("page", String.valueOf(1));
					httpPost(params, 103);
					break;
				case 103:
					//xlistview.setAdapter(adapter);
					ArrayList<HolderInfo> infoList = (ArrayList<HolderInfo>)msg.obj;
					adapter.clear();
					if(adapter != null){
						for(int i = 0;i < infoList.size();i++)
						adapter.add(infoList.get(i));
					}
					gridview.setAdapter(adapter);
					//sendEmptyMessage(110);
					break;
				case 104:
					//是否收藏
					if(msg.arg2 == 1){
						isCollection = true;
						iv_collection.setImageResource(R.drawable.ic_menu_collection);
					}else{
						isCollection = false;
						iv_collection.setImageResource(R.drawable.ic_video_collection);
					}
					break;
			}
		}
	};
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LogUtil.e("newConfig.orientation="+newConfig.orientation);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// topbar.setVisibility(View.VISIBLE);
			layout_content.setVisibility(View.VISIBLE);

			RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
					android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
					android.widget.RelativeLayout.LayoutParams.MATCH_PARENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			/*int width = BitmapCommonUtils.getScreenSize(this)
					.getWidth();*/
			int width = DeviceInfo.screenWidth;
			int height =  DeviceInfo.dip2px(200);;
			video.getHolder().setFixedSize(width, height);
			video.setLayoutParams(lp);
			video.invalidate();
			video.requestFocus();
			video.requestLayout();
			videoLayout.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					height));
			videoLayout.requestLayout();
			//playFullScreen.setBackgroundResource(R.drawable.plugin_ad_gofull);
		}else{
			layout_content.setVisibility(View.GONE);
			int width = DeviceInfo.screenWidth;
					//.getWidth();
			int height =  DeviceInfo.screenHeight;
					//.getHeight();
			videoLayout.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT));
			android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			video.getHolder().setFixedSize(height, width);
			video.setLayoutParams(lp);
			video.requestLayout();
			video.invalidate();
			videoLayout.requestLayout();
			//playFullScreen.setBackgroundResource(R.drawable.plugin_ad_gosmall);
		}
	}
	@Event(value = R.id.play_back)
	private void back(View v){
		int ori = cf.orientation ; //获取屏幕方向
		LogUtil.e("ori=" + ori + ",cf.ORIENTATION_LANDSCAPE=" + cf.ORIENTATION_LANDSCAPE + ",cf.ORIENTATION_UNDEFINED=" + cf.ORIENTATION_UNDEFINED);
		if (ori == cf.ORIENTATION_LANDSCAPE
				|| ori == cf.ORIENTATION_UNDEFINED) {

			layout_content.setVisibility(View.VISIBLE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			finish();
		}
	}

	/**
	 * 返回首页
	 * @param v
	 */
	@Event(value = R.id.layout_home)
	private void layout_homeClick(View v){
		LogUtil.e("layout_---------home");
		Intent intent = new Intent();
		intent.setClass(context, MainActivity.class);//返回首页
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	@Event(value = R.id.play_full)
	private  void playFullScreenClick(View v) {

		int ori = cf.orientation ; //获取屏幕方向
		LogUtil.e("ori=" + ori + ",cf.ORIENTATION_LANDSCAPE=" + cf.ORIENTATION_LANDSCAPE + ",cf.ORIENTATION_UNDEFINED=" + cf.ORIENTATION_UNDEFINED);
		if (ori == cf.ORIENTATION_LANDSCAPE
				|| ori == cf.ORIENTATION_UNDEFINED) {

			layout_content.setVisibility(View.VISIBLE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			layout_content.setVisibility(View.GONE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	@Event(value = R.id.iv_video_play)
	private void playClick(View view){
		iv_paly.setVisibility(View.GONE);
		iv_pic.setVisibility(View.GONE);
		ispaly = true;
		video.start();
	}

	/**
	 * 添加 或取消收藏
	 * @param view
	 */
	@Event(value = R.id.layout_collection)
	 private void collectionlayoutClick(View view){

		LogUtil.e("isCollection==" + isCollection);
		if(isCollection){
			isCollection = false;
			iv_collection.setImageResource(R.drawable.ic_video_collection);
			RequestParams params = new RequestParams(HttpUtil.DelCollection_URL);
			params.addBodyParameter("u_imsi", IMSI);
			params.addBodyParameter("vid", vid);
			httpPost(params, 104);
		}else{
			isCollection = true;
			iv_collection.setImageResource(R.drawable.ic_menu_collection);
			RequestParams params = new RequestParams(HttpUtil.Collection_URL);
			params.addBodyParameter("u_imsi", IMSI);
			params.addBodyParameter("vid", vid);
			httpPost(params, 105);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		LogUtil.e("onPrepared");
		video.start();
		if(!ispaly)
			handler.sendMessageDelayed(handler.obtainMessage(0), 100);
		//progressBar.setVisibility(View.GONE);
		mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
			int duration;

			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// 获得当前播放时间和当前视频的长度
				currentPosition = video.getCurrentPosition();//毫秒
				duration = video.getDuration();
				if (!video.isPlaying())
					iv_paly.setVisibility(View.VISIBLE);
				else {
					iv_paly.setVisibility(View.GONE);
					videoPause();
				}
				// prog
				//int time = ((currentPosition * 100) / duration);
				//LogUtil.e("currentPosition=" + currentPosition + ",progress==" + video.getBufferPercentage() + ",videoplay=" +
				//	video.isPlaying() + ",percent=" + percent);
				// 设置进度条的主要进度，表示当前的播放时间
				//  SeekBar seekBar = new SeekBar(EsActivity.this);
				// seekBar.setProgress(time);
				// 设置进度条的次要进度，表示视频的缓冲进度
				// seekBar.setSecondaryProgress(percent);
			}
		});

		//

	}

	private final static class TagInfo {
		String tid;
		String t_title;//
		String t_clicks;// 的、video地址
		TagInfo() {

		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//iv_paly.setVisibility(View.GONE);
		LogUtil.e("ispaly======" + ispaly);
		if(video != null) {
			//progressBar.setVisibility(View.VISIBLE);
			video.resume();
			video.seekTo(currentPosition);

		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//video.stopPlayback();
		if(video != null)
			video.pause();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(video != null)
			video.suspend();
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

	private void httpPost(RequestParams params, final int other) {
			params.addBodyParameter("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
			params.addBodyParameter("uak", Constants.UAK);
			List<KeyValue> keyList = params.getQueryStringParams();
			String[] keystr = new String[keyList.size()];
			for (int i = 0; i < keyList.size(); i++) {
				keystr[i] = keyList.get(i).key;
			}
			Arrays.sort(keystr, String.CASE_INSENSITIVE_ORDER);
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < keystr.length; i++) {
				str.append(keystr[i]).append("=").append(params.getStringParameter(keystr[i])).append("&");
			}
			String ak = (str.toString().substring(0, str.length() - 1));
			LogUtil.e("ak==" + ak);
			ak = MD5.md5(ak);
			LogUtil.e("ak==" + ak);
			params.addQueryStringParameter("ak", ak);
			if(other == 103){
				String tags = params.getStringParameter("tags").toString().replace("%2C", ",");
				params.removeParameter("tags");
				params.addQueryStringParameter("tags",tags);

			}//替换

			params.removeParameter("uak");
		/*	List<KeyValue> keyList2 = params.getQueryStringParams();
			for (int i = 0; i < keyList2.size(); i++) {
				LogUtil.e(keyList2.get(i).toString());
			}*/

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
						if(other == 101){
							JSONObject dataObj = jsonObj.getJSONObject("data");
							if(dataObj.has("content")){
								JSONObject contentObj = dataObj.getJSONObject("content");
								int is_collection = contentObj.getInt("is_collection");
								handler.sendMessage(handler.obtainMessage(104, 0,is_collection));

							}

							if(dataObj.has("tids")) {
								JSONArray tidsArray = dataObj.getJSONArray("tids");
							/*	String[] tids = new String[tidsArray.length()];
								for (int i = 0; i < tidsArray.length(); i++) {
									tids[i] = tidsArray.getString(i);
									LogUtil.e("tid========" + tids[i]);
								}*/
								LogUtil.e("tid========" + tidsArray.toString());
								handler.sendMessage(handler.obtainMessage(102, tidsArray.toString()));
							}
							if(dataObj.has("tags")){
								JSONArray tagsArray = dataObj.getJSONArray("tags");
								List<TagInfo> tagInfoList = new ArrayList<TagInfo>();
								for (int i = 0; i < tagsArray.length(); i++) {
									TagInfo tagInfo = new TagInfo();
									tagInfo.tid = tagsArray.getJSONObject(i).getString("tid");
									tagInfo.t_title = tagsArray.getJSONObject(i).getString("t_title");
									tagInfo.t_clicks = tagsArray.getJSONObject(i).getString("t_clicks");
									tagInfoList.add(tagInfo);
								}
								handler.sendMessage(handler.obtainMessage(101, tagInfoList));
							}
						}else if(other == 103){
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
							handler.sendMessage(handler.obtainMessage(103, holderInfoList));
						}else if(other == 104){
							ToastUtils.showShortToast(context,getResources().getString(R.string.collection_del_success));

						}else if(other == 105){
							ToastUtils.showShortToast(context,getResources().getString(R.string.collection_success));
						}
					}else {
						ToastUtils.showShortToast(context, getResources().getString(R.string.load_faile));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtils.showShortToast(context, getResources().getString(R.string.service_error));
			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFinished() {

			}
		});
	}

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

	/**
	 * 订阅功能
	 */
	public  void dialog() {

		if(dialog != null && dialog.isShowing() )
			return;
		View layout = getLayoutInflater().inflate(R.layout.layout_dialog, null);
		  dialog = new MyDialog(this, layout, R.style.mydialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		LogUtil.e("tag", "!isFinishing()=" + !isFinishing());
		if(!isFinishing())
			dialog.show();
		TextView tv_prompt = (TextView) layout.findViewById(R.id.dialog_prompt);
		String traffic1 = OutMd.GetChargingPointInfo(this, "J005");
		tv_prompt.setText(String.format(getResources().getString(R.string.pay_prompt),traffic1));
		Button postBtn = (Button) layout.findViewById(R.id.dialog_postbtn);
		Button closeBtn = (Button) layout.findViewById(R.id.dialog_negabtn);

		//postBtn.setText(context.getResources().getString(R.string.yes_shiping));
		postBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				pay();
			}
		});
		//postBtn.setText(context.getResources().getString(R.string.yes_shiping));
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();

			}
		});

	}

	private void closeDialog(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
			dialog = null;
		}
	}

	/**
	 * 购买vip
	 */
	private void pay(){
		//loadDialog()
		Intent intent = new Intent();
		//pidc 计费点ID 根据你的billconfig。xml 配置文件夹
		String custom = "";//默认不填写 如需要可以添加 道具订单号
		intent.putExtra("pidc", "J005");
		if(custom !=null)
			intent.putExtra("custom", custom);

		MobPay m = new MobPay();
		Bundle bundle = new Bundle();
		bundle.putSerializable("xinda", m);

		intent.putExtras(bundle);//閺�顖欑帛缂佹挻娼崥搴ゎ洣鐠哄疇娴嗛崚鎵畱缁楊兛绗侀弬绗癱tivity缁鎮￿
		intent.setClass(VideoActivity.this, fbin.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		VideoActivity.this.startActivity(intent);
	}

}
