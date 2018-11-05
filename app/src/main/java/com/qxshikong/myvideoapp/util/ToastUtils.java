package com.qxshikong.myvideoapp.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ToastUtils {

	public static void showShortToast(Context ctx, String msg) {
		// Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		/*ImageView icon = new ImageView(ctx);
		icon.setImageResource(R.drawable.yes_icon);
		toastView.addView(icon, 0);*/
		toast.show();
	}

	public static void showYesShortToast(Context ctx, String msg) {
		// Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
		Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		/*ImageView icon = new ImageView(ctx);
		icon.setImageResource(R.drawable.yes_icon);
		toastView.addView(icon, 0);*/
		toast.show();
	}

	public static void showLongToast(Context ctx, String msg) {
		Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		/*ImageView icon = new ImageView(ctx);
		icon.setImageResource(R.drawable.yes_icon);
		toastView.addView(icon, 0);*/
		toast.show();
	}
}
