package com.qxshikong.myvideoapp.util;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.qxshikong.myvideoapp.R;

public class LoadDialog extends Dialog {

    private Context context = null;

    private TextView tv_msg;

    public LoadDialog(Context context) {
        super(context);
        this.context = context;

    }

    public LoadDialog(Context context, boolean cancelable,
                      OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public LoadDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        // 加载自己定义的布局
        View view = LayoutInflater.from(context)
                .inflate(R.layout.widget_progressdialog, null);

        ImageView img_loading = (ImageView) view.findViewById(R.id.loadingImageView);
      //  ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        tv_msg = (TextView) view.findViewById(R.id.id_tv_loadingmsg);
        // 加载XML文件中定义的动画
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils
                .loadAnimation(context, R.anim.anim_progress);
        // 开始动画
        img_loading.setAnimation(rotateAnimation);
        // 为Dialoge设置自己定义的布局
        setContentView(view);
        // 为close的那个文件添加事件
        /*img_close.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dismiss();

            }
        });*/

    }

    public void setMsg(String msg) {

        if (null != tv_msg) {
            tv_msg.setText(msg);
        }
    }

    public void setMsg(int resId) {

        if (null != tv_msg) {
            tv_msg.setText(context.getString(resId));
        }
    }

}
