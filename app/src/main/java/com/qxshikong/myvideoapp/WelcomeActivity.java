package com.qxshikong.myvideoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import org.exp.in.OutMd;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import cn.jpush.android.api.JPushInterface;


@ContentView(R.layout.act_welcome)
public class WelcomeActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		x.view().inject(this);

		new android.os.Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
				finish();
			}
		}, 1500);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
