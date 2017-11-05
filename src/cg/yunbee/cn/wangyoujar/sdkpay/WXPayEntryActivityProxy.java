package cg.yunbee.cn.wangyoujar.sdkpay;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import dy.compatibility.work.SDKOptProducer;
//onCreate->onAttachFragment->onContentChanged->(onRestart)->onStart->onRestoreInstanceState->onPostCreate->onResume->onPostResume->onAttachToWindow->onCreateOptionsMenue->onPrepareOptionsMenue
public class WXPayEntryActivityProxy extends Activity {
	
	private SDKOptProducer sdkOptProducer;
	private Object proxyObj;
	private Activity realActivity;
	
	public static final boolean isBack = true;
	
	
	public WXPayEntryActivityProxy() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try {
			Method m = proxyObj.getClass().getMethod("onCreate", new Class<?>[]{Bundle.class});
			m.invoke(proxyObj, new Object[]{savedInstanceState});
		} catch(Exception e) {
			Log.i("INFO", "WXPayEntryActivityProxy->" + Log.getStackTraceString(e));
			Log.i("INFO", "WXPayEntryActivityProxy->e...onCreate->ex:" + e.toString());
		}
		
		Log.i("INFO", "WXPayEntryActivityProxy onCreate finished!");
	}
	
	

	public void setActivity(Activity activity) {
		this.realActivity = activity;
	}
	
	@Override
	public void onDestroy() {
		try {
			Method m = proxyObj.getClass().getMethod("onDestroy", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch(Exception e) {
			Log.i("INFO", "WXPayEntryActivityProxy->e...onDestroy->ex:" + e.toString());
		}
		realActivity = null;
	}
	
	@Override
	public void onStart() {
		
	}
	
	@Override
	public void onStop() {
		
	}
	
	@Override
	public void onRestart() {
		
	}
	
	@Override
	public void onResume() {
		
	}
	
	
	@Override
	public void onBackPressed() {
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return false;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return false;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
	}
	//View附加到窗体时:onCreate->onStart->onResume->onAttachedToWindow
	@Override
	public void onAttachedToWindow() {
	}
	
	@Override
	public void attachBaseContext(Context newBase) {
		try {
			sdkOptProducer = SDKOptProducer.newInstance(this);
			proxyObj = Class.forName("dy.compatibility.ImplSdk.WeixinSDKImpl$WXPayEntryActivityProxy", true, sdkOptProducer.getWeixinSDK().getClass().getClassLoader()).newInstance();
			Method setActivity = proxyObj.getClass().getMethod("setActivity", new Class<?>[]{Activity.class});
			setActivity.invoke(proxyObj, realActivity);
		} catch (Exception e) {
			Log.i("INFO", "WXPayEntryActivityProxy--->" + Log.getStackTraceString(e));
		}
	}
	
	@Override
	public void onContentChanged() {
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	}

	@Override
	public void onNewIntent(Intent intent) {
		try {
			Method m = proxyObj.getClass().getMethod("onNewIntent", new Class<?>[]{Intent.class});
			m.invoke(proxyObj, new Object[]{intent});
		} catch(Exception e) {
			Log.i("INFO", "DY->WXPayEntryActivityProxy->e...onNewIntent->ex:" + e.toString());
		}
	}

	
}
