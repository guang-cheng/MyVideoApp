package com.qxshikong.myvideoapp.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.exp.in.IMobPay;
import org.exp.in.OutMd;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.x;

import android.content.Context;
import android.widget.Toast;

import com.qxshikong.myvideoapp.R;
import com.umeng.analytics.MobclickAgent;

public class MobPay implements IMobPay,Serializable{
	@Override
	public void Success(Context context)
	{
		 // Log.i("pay", "pay success");
		 Toast.makeText(context,"pay suc" ,
			     Toast.LENGTH_SHORT).show();
		 //ResourceUtil.getStringId(context, "bill_success")
		payTongji(context, true);

		httpPost(101,context);
		 // Log.i("test","CPaymentObserver suc jid:");

	}
	
	@Override
    public void fail(Context context,int errorcode)
    {

		//httpPost(101,context);

		payTongji(context,false);
		 Toast.makeText(context, "pay err:" +" code="+errorcode,
			     Toast.LENGTH_SHORT).show();
    		 //context.getResources().getString(ResourceUtil.getStringId(context, "bill_failure"))
    }

	private void httpPost(final int other,final Context context) {
		RequestParams params = new RequestParams(HttpUtil.VIP_Buy_URL);

		params.addBodyParameter("u_imsi", UsedUtil.getIMSI(context));
		params.addBodyParameter("vipType", "2");
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
						UsedUtil.VIP = "1";
						ToastUtils.showLongToast(context, context.getResources().getString(R.string.pay_success));
						//Toast.makeText(x.app(), "ArrayList", Toast.LENGTH_LONG).show();
					} else {
						ToastUtils.showLongToast(context, context.getResources().getString(R.string.pay_faile));
						//mHandler.sendMessage(mHandler.obtainMessage(110));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {

				ToastUtils.showLongToast(context, context.getResources().getString(R.string.service_error));
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


	private void payTongji(Context context,boolean isSuccess){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("imsi", UsedUtil.getIMSI(context));
		//map.put("quantity", "3");
		if(isSuccess)
		map.put("pay", "success");
		else
			map.put("pay", "faile");
		String traffic1 = OutMd.GetChargingPointInfo(context, "J005");
		if(traffic1 != null)
		map.put("accout", traffic1);
		MobclickAgent.onEvent(context, "purchase", map);
	}
}
