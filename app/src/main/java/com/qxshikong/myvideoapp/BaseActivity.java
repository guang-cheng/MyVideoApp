package com.qxshikong.myvideoapp;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import org.exp.in.OutMd;

import cn.jpush.android.api.JPushInterface;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		OutMd.zhilUpdate(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	 protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		JPushInterface.onResume(this);
		//OutMd.zhilUpdate(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		JPushInterface.onPause(this);
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

}
