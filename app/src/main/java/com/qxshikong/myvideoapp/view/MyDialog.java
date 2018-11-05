package com.qxshikong.myvideoapp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MyDialog extends Dialog
{
	private Context context;
	public MyDialog(Context context) 
	{
		super(context);
		this.context=context;
	}
	public MyDialog(Context context, int theme)
	{
		super(context, theme);
		this.context=context;
	}
	public MyDialog(Context context, View layout, int style)
	{
		super(context, style);
		setContentView(layout);
		Window window=getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		window.addFlags(params.FLAG_DIM_BEHIND);
		params.gravity = Gravity.CENTER;
		params.dimAmount=0.6f;
		window.setAttributes(params);
	}
}
