package com.qxshikong.myvideoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.qxshikong.myvideoapp.adapter.FragAdapter;
import com.qxshikong.myvideoapp.fragment.MenuFragment;
import com.qxshikong.myvideoapp.fragment.TagFragment;
import com.qxshikong.myvideoapp.fragment.VideoGridFragment;
import com.qxshikong.myvideoapp.util.Constants;
import com.qxshikong.myvideoapp.util.DeviceInfo;
import com.qxshikong.myvideoapp.util.HttpUtil;
import com.qxshikong.myvideoapp.util.LogUtil;
import com.qxshikong.myvideoapp.util.ToastUtils;
import com.qxshikong.myvideoapp.util.UsedUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateConfig;

import org.exp.in.OutMd;
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

import cn.jpush.android.api.JPushInterface;

//viewUtils加载Activity布局,使用了这句之后oncreate里面不需要调用setcontentView
@ContentView(R.layout.act_main)
public class MainActivity extends FragmentActivity {
	@ViewInject(R.id.mHorizontalScrollView)
	private HorizontalScrollView mHorizontalScrollView;
	@ViewInject(R.id.mRadioGroup_content)
	private RadioGroup mRadioGroup_content; // 加载分类栏
	@ViewInject(R.id.id_drawerlayout)
	private DrawerLayout mDrawerLayout;
	@ViewInject(R.id.id_linearlayout2)
	private LinearLayout id_linearlayout2;
	@ViewInject(R.id.framelayout)
	private FrameLayout framelayout;//侧滑栏 填充布局
	@ViewInject(R.id.viewpager)
	private ViewPager viewPager;

	private long exitTime;
	private final String TAG = "MainActivity";
	
	
	private LayoutInflater mInflater;
	/** Item宽度 */
	private int mItemWidth = 0;
	/** viewpage 适配器 */
	private FragAdapter fragAdapter;

	private String IMSI;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		x.view().inject(this);
		DeviceInfo.initDeviceInfo(this);// 初始化 设备信息
		initMenu();
		String[] categoryList = getResources().getStringArray(R.array.category);

		//K0018  K300
		OutMd.SdkInit(this, "K0018", getResources().getString(R.string.qudao));

		UmengUpdateAgent.setDeltaUpdate(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.forceUpdate(this);
	//	JPushInterface.init(getApplication());
		/*
		RequestParams params = new RequestParams(HttpUtil.News_Category);
		httpGet(params, 101);*/
		login();
		mHandler.sendMessage(mHandler.obtainMessage(101, categoryList));
	}
	private void login(){
		IMSI = UsedUtil.getIMSI(this);
		LogUtil.e("imsi==" + IMSI);
		if(IMSI == null){
			ToastUtils.showLongToast(this,getResources().getString(R.string.login_faile));
			return;
		}
		RequestParams params = new RequestParams(HttpUtil.Login_URL);

		params.addBodyParameter("u_imsi", IMSI);
		params.addBodyParameter("ch_id", getResources().getString(R.string.qudao));
		httpPost(params, 101);
	}

	private void initViewPage(String [] categoryList) {
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		for (int i = 0; i < categoryList.length; i++) {
			Fragment ft ;
			if(i == 2){
				ft = new TagFragment();
			}else{
				ft = new VideoGridFragment();
				Bundle bundle = new Bundle();
				if(i == 0)
					bundle.putString("order", "new");
				else if(i == 3)
					bundle.putString("order", "vip");
				else
					bundle.putString("order", "hot");
				ft.setArguments(bundle);

			}
			fragmentList.add(ft);
		}
		fragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentList);
		viewPager.setAdapter(fragAdapter);
		viewPager.setCurrentItem(0);
		viewPager.setOffscreenPageLimit(categoryList.length);
		setListener();
	}

	/**
	 * 分类栏初始化
	 */
	private void initTabColumn(String [] categoryList) {
		mItemWidth = DeviceInfo.screenWidth / 6;
		mRadioGroup_content.removeAllViews();
		// 获取布局填充器
		mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < categoryList.length; i++) {

			RadioButton rb = (RadioButton) mInflater.inflate(R.layout.layout_radiobtn, null);
			rb.setId(i);
			rb.setText(categoryList[i]);
			rb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			if (i == 0)
				rb.setChecked(true);
			mRadioGroup_content.addView(rb);
		}
	}


	/** onclick */
	private void setListener() {
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				LogUtil.e("num=" + mRadioGroup_content.getChildCount() + ",position=" + position);

				if (mRadioGroup_content != null && mRadioGroup_content.getChildCount() > position) {
					((RadioButton) mRadioGroup_content.getChildAt(position)).performClick();
				}
				if (position == 0) {
					isOpenMenu(true);
				} else {
					isOpenMenu(false);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		mRadioGroup_content.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (mRadioGroup_content.getChildAt(checkedId) != null) {

					viewPager.setCurrentItem(checkedId, false); // ViewPager

					mHorizontalScrollView.smoothScrollTo(
							(checkedId > 1 ? ((RadioButton) mRadioGroup_content.getChildAt(checkedId)).getLeft() : 0)
									- ((RadioButton) mRadioGroup_content.getChildAt(2)).getLeft(),
							0);
				}
			}
		});
	}

	private void initMenu() {
		final MenuFragment menuFragment = new MenuFragment();
		framelayout.setLayoutParams(new LayoutParams(DeviceInfo.screenWidth * 7/10 , LayoutParams.MATCH_PARENT));
		getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, menuFragment).commit();
		mDrawerLayout.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int i, KeyEvent keyEvent) {
				if (mDrawerLayout.isDrawerOpen(id_linearlayout2)) {
					mDrawerLayout.closeDrawers();
				}
				return false;
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		LogUtil.e("mDrawerLayout.isDrawerOpen(id_linearlayout2)=" + mDrawerLayout.isDrawerOpen(id_linearlayout2));
		if (mDrawerLayout.isDrawerOpen(id_linearlayout2)) {
			mDrawerLayout.closeDrawers();
		} else {
			if(System.currentTimeMillis() - exitTime > 2000){
				exitTime = System.currentTimeMillis();
				ToastUtils.showLongToast(this, getResources().getString(R.string.exits));

			}else{
				finish();
			}
		}
	}
	/**
	 * 是否打开菜单滑动
	 * 
	 * @param isOpen
	 */
	private void isOpenMenu(boolean isOpen) {
		if (isOpen) {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); //打开手势滑动 
		} else {
			//关闭手势滑动 
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); 
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		OutMd.zhilUpdate(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onResume(this);
		JPushInterface.onResume(this);
		if(UsedUtil.VIP.equals("3"))
			login();
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		JPushInterface.onPause(this);
	}
	/**
	 * 关闭侧拉栏
	 * @param
	 */
	public void colseMenu(){
		mDrawerLayout.closeDrawers();
	}

	@Event(value = R.id.iv_game, type = View.OnClickListener.class)
	private void main_gameClick(View view) {
		Intent intent = new Intent(this, AppRdActivity.class);
		startActivity(intent);
	}
	@Event(value = R.id.iv_sousuo, type = View.OnClickListener.class)
	private void main_sousuoClick(View view) {
		Intent intent = new Intent(this,SearchActivity.class);
		//intent.putExtra("type","0");
		startActivity(intent);
	}
	@Event(value = R.id.main_leftLayout, type = View.OnClickListener.class)
	private void main_leftLayoutClick(View view) {
		mDrawerLayout.openDrawer(Gravity.START);

	}

	final static class HolderInfo {
		String title_name;
		String class_id;

		HolderInfo() {

		}
	}

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 101:
				String[] categoryList = (String[])msg.obj;
				initTabColumn(categoryList);
				initViewPage(categoryList);
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
							JSONObject jsonData = jsonObj.getJSONObject("data");
							String isvip = jsonData.getString("vip");
							if(isvip != null)
							UsedUtil.VIP = isvip;
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
				ToastUtils.showLongToast(MainActivity.this, getResources().getString(R.string.service_error));
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			OutMd.end(MainActivity.this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}
