package com.qxshikong.myvideoapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qxshikong.myvideoapp.AppRdActivity;
import com.qxshikong.myvideoapp.HistorActivity;
import com.qxshikong.myvideoapp.MainActivity;
import com.qxshikong.myvideoapp.R;
import com.qxshikong.myvideoapp.util.LogUtil;
import com.qxshikong.myvideoapp.view.CircleImageView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * menu fragment ，主要是用于显示menu菜单
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @since Mar 12, 2013
 * @version 1.0.0
 */
public class MenuFragment extends Fragment{
   
	private MainActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	
    	View view = inflater.inflate(R.layout.frg_menu, null);
    	x.view().inject(this,view);
    	activity = (MainActivity)this.getActivity();
    	LogUtil.e("tag", "MenuFragment-onCreateView");
    	return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
       /* setRetainInstance(true);
        //set the preference xml to the content view
        addPreferencesFromResource(R.xml.menu);*/
       
    }
    
    @Event(value = R.id.rl_menu_home, type = View.OnClickListener.class)
      private void rl_menu_homeClick(View view){
       // LogUtil.e("tag", "mregister_btnClick");
        activity.colseMenu();

    }
    @Event(value = R.id.rl_menu_collection, type = View.OnClickListener.class)
    private void rl_menu_collectionClick(View view){
        LogUtil.e("tag", "mregister_btnClick");
        Intent intent = new Intent(activity, HistorActivity.class);
        intent.putExtra("type","0");
        intent.putExtra("title","Collection");
        startActivity(intent);
    }
    @Event(value = R.id.rl_menu_history, type = View.OnClickListener.class)
    private void rl_menu_historyClick(View view){

        Intent intent = new Intent(activity, HistorActivity.class);
        intent.putExtra("type","1");
        intent.putExtra("title","History");
        startActivity(intent);

    }
    @Event(value = R.id.rl_menu_apprecommend, type = View.OnClickListener.class)
    private void rl_menu_apprecommendClick(View view){
        LogUtil.e("tag", "mregister_btnClick");
        Intent intent = new Intent(activity, AppRdActivity.class);
        startActivity(intent);
    }


}
