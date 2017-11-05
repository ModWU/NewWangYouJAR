package cg.yunbee.cn.wangyoujar.pay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Xml;
import cg.yunbee.cn.wangyoujar.Yunbee;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.DYH5Activity;
import cg.yunbee.cn.wangyoujar.work.DianPayActivity;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import cn.yunbee.cn.wangyoujar.joggle.IPayCallBack;

public abstract class SDKPay {
	protected String mPropId;
	protected String mPropName;
	protected String mParam;
	protected String mPrice;
	protected  String mData;
	protected String mPayId;
	protected  IPayCallBack mPayCallBack;
	public  String mPropertyPayResult;
	protected Activity mActivity;
	
	public SDKPay() {
		TAGS.log("-----------------SDKPay->create---------------");;
	}
	
	public void setParam(Pay pay) {
		mPropId = pay.getPropId();
		mPropName = pay.getPropName();
		mParam = pay.getParam();
		mPrice = pay.getPrice();
		mData = pay.getData();
		mPayId = pay.getPayId();
		mPayCallBack = pay.getPayCallBack();
		mPropertyPayResult = pay.getPropertyPayResult();
		mActivity = pay.getActivity();
	}
	
	public String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
	
	public void payAbort() {
		payAbort(mData);
	}
	
	public String md5WeChat(String content) {
		return Util.getHash(content, "MD5");
	}
	
	public String getValueFromXML(String content, String nodeName) {
		String value = null;
		try {
			ByteArrayInputStream tInputStringStream = null;
			if (content != null && !content.trim().equals("")) {
				tInputStringStream = new ByteArrayInputStream(
						content.getBytes());
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(tInputStringStream, "UTF-8");
				int eventType = 0;
				eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG: {
						if (parser.getName().equals(nodeName)) {
							eventType = parser.next();
							value = parser.getText();
						}
						break;
					}
					}
					eventType = parser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
	public void payAbort(final String msg) {
		TAGS.log("--------payAbort--------");
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				mPayCallBack.onFailed(msg);
				mPropertyPayResult = "fail";
				Yunbee.isPaying = false;
				YunbeeVice.doPayFlag = false;
				try {
					DialogUtils.closeDialog(mActivity, PayManager.dialog);
					if(DianPayActivity.dianPayActivity != null)
						DianPayActivity.dianPayActivity.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void payCancel() {
		payCancel(mData);
	}
	
	
	public void payCancel(final String msg) {
		TAGS.log("--------payCancel--------");
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				DialogUtils.closeDialog(mActivity, PayManager.dialog);
				mPayCallBack.onCanceled(msg);
				Yunbee.isPaying = false;
				YunbeeVice.doPayFlag = false;
			}

		});
	}

	public void paySuccess() {
		paySuccess(mData);
	}
	
	
	public void paySuccess(final String msg) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
		TAGS.log("--------paySuccess--------");
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				try {
					if(DianPayActivity.dianPayActivity != null) {
						DianPayActivity.dianPayActivity.finish();
						DianPayActivity.dianPayActivity = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				mPayCallBack.onSuccess(msg);
				mPropertyPayResult = "success";
				Yunbee.isPaying = false;
				YunbeeVice.doPayFlag = false;
			}

		});

	}
	
	protected void startH5Page(String url, int page_see, boolean isPost) {
		TAGS.log("-----------------DYH5Pay->startH5Page-----------------");
		Util.replaceClassLoader_api17(getClass().getClassLoader(), mActivity);
		DYH5Activity.setSDKPay(this);
		Intent h5Intent = new Intent(mActivity, DYH5Activity.class);
		h5Intent.putExtra(DYH5Activity.WEBVIEW_URL, url);
		h5Intent.putExtra(DYH5Activity.PAGE_SEE, page_see);
		String method = isPost ? DYH5Activity.POST : DYH5Activity.GET;
		h5Intent.putExtra(DYH5Activity.WEBVIEW_METHOD, method);
		mActivity.startActivity(h5Intent);
	}
	
	public abstract SDKPay pay();
}
