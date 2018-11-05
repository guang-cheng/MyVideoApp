package com.qxshikong.myvideoapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.qxshikong.myvideoapp.R;
import com.qxshikong.myvideoapp.SearchActivity;
import com.qxshikong.myvideoapp.util.Constants;
import com.qxshikong.myvideoapp.util.HttpUtil;
import com.qxshikong.myvideoapp.util.LogUtil;
import com.qxshikong.myvideoapp.view.FlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagFragment extends LazyFragment{

	@ViewInject(R.id.flowLayout)
	private FlowLayout flowLayout;
	private final String TAG = "VideoGridFragment";
	/** 是fragment 懒加载已加载过 */
	private boolean isLoad;
	// 标志位，标志已经初始化完成。
	private boolean isPrepared;
	private static Activity activity;
	private LayoutInflater layoutInflater = null;
	public TagFragment(){

	}
/*	public VideoGridFragment(String class_id) {

		this.class_id = class_id;
	}*/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.frg_tags, null);
		x.view().inject(this, view);
		activity = this.getActivity();
		isPrepared = true;
		layoutInflater = inflater;
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
			RequestParams params = new RequestParams(HttpUtil.Tags_URL );
			httpPost(params, 101);
		}

	}

	private final static class HolderInfo {
		String tid;
		String t_title;//
		String t_clicks;// 的、video地址
		HolderInfo() {

		}
	}


	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 101:
				//xlistview.setAdapter(adapter);
				//if(adapter != null)
			//	adapter.add_updat(cache.generateLocalList());
				//sendEmptyMessage(110);
				final ArrayList<HolderInfo> holderInfoList = (ArrayList<HolderInfo>) msg.obj;
				for(int i = 0;i < holderInfoList.size();i++) {
					final HolderInfo info = holderInfoList.get(i);
					View view = layoutInflater.inflate(R.layout.layout_tags_button, null);
					Button tagBtn = (Button) view.findViewById(R.id.btn_tags);
					tagBtn.setText(holderInfoList.get(i).t_title);
					tagBtn.setOnClickListener(new  View.OnClickListener(){
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(activity, SearchActivity.class);
							intent.putExtra(HttpUtil.Search,info.t_title);
							intent.putExtra(HttpUtil.Tid,info.tid);
							startActivity(intent);
						}
					});
					flowLayout.addView(view);
				}
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
					if (status == 1) {
						List<HolderInfo> infoList = new ArrayList<HolderInfo>();
						if(other == 101){
							JSONArray jsonArray = jsonObj.getJSONArray("data");
							for(int i=0;i < jsonArray.length();i++) {
								HolderInfo holderInfo = new HolderInfo();
								holderInfo.tid = jsonArray.getJSONObject(i).getString("tid");
								holderInfo.t_title = jsonArray.getJSONObject(i).getString("t_title");
								holderInfo.t_clicks = jsonArray.getJSONObject(i).getString("t_clicks");
								infoList.add(holderInfo);
							}
							mHandler.sendMessage(mHandler.obtainMessage(101,infoList));
						}
						//Toast.makeText(x.app(), "ArrayList", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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



}
