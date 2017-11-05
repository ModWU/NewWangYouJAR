package cn.yunbee.cn.wangyoujar.activities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import cn.yunbee.cn.wangyoujar.joggleUtils.YunbeeUtils;

/**
 * 应用启动时第一个Activity,此类尽量不要修改，因为无法热更新！
 * 
 * @author Administrator
 * 
 */
public class YunbeeActivity extends Activity {
	
	private Activity proxyActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			Method onCreateMethod = proxyActivity.getClass().getMethod("onCreate", new Class<?>[]{Bundle.class});
			onCreateMethod.invoke(proxyActivity, new Object[]{savedInstanceState});
		} catch(Exception e) {
			Log.i("INFO", "real->YunbeeActivity ex:" + e.toString());
		}
	}
	
	@Override
	public void onBackPressed() {
		try {
			Field field = proxyActivity.getClass().getField("isBack");
			boolean isBack = (Boolean) field.get(proxyActivity.getClass());
			if(isBack) {
				super.onBackPressed();
			}
			proxyActivity.onBackPressed();
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onBackPressed->ex:" + e.toString());
		}
	}
	
	@Override
	public void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
		try {
			proxyActivity = YunbeeUtils.getInstance(this).getYunbeeActivity();
			Method setRealActivityMethod = proxyActivity.getClass().getDeclaredMethod("setRealActivity", new Class<?>[]{Activity.class});
			setRealActivityMethod.invoke(proxyActivity, new Object[]{this});
			
			Method method = proxyActivity.getClass().getMethod("attachBaseContext", new Class<?>[]{Context.class});
			method.invoke(proxyActivity, new Object[]{newBase});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-attachBaseContext->ex:" + e.toString());
		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		try {
			proxyActivity.onAttachedToWindow();
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onAttachedToWindow->ex:" + e.toString());
		}
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		try {
			Method method = proxyActivity.getClass().getMethod("onRestoreInstanceState", new Class<?>[]{Bundle.class});
			method.invoke(proxyActivity, new Object[]{savedInstanceState});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onRestoreInstanceState->ex:" + e.toString());
		}
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		try {
			Method method = proxyActivity.getClass().getMethod("onNewIntent", new Class<?>[]{Intent.class});
			method.invoke(proxyActivity, new Object[]{intent});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onNewIntent->ex:" + e.toString());
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		try {
			Method method = proxyActivity.getClass().getMethod("onStart", new Class<?>[]{});
			method.invoke(proxyActivity, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onStart->ex:" + e.toString());
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		try {
			Method method = proxyActivity.getClass().getMethod("onStop", new Class<?>[]{});
			method.invoke(proxyActivity, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onStop->ex:" + e.toString());
		}
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		try {
			Method method = proxyActivity.getClass().getMethod("onRestart", new Class<?>[]{});
			method.invoke(proxyActivity, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onRestart->ex:" + e.toString());
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		try {
			Method method = proxyActivity.getClass().getMethod("onResume", new Class<?>[]{});
			method.invoke(proxyActivity, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onResume->ex:" + e.toString());
		}
	}
	
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		try {
			Method method = proxyActivity.getClass().getMethod("onContentChanged", new Class<?>[]{});
			method.invoke(proxyActivity, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onContentChanged->ex:" + e.toString());
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean flag = false;
		try {
			flag = proxyActivity.onPrepareOptionsMenu(menu);
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onPrepareOptionsMenu->ex:" + e.toString());
		}
		return flag ? true : super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean flag = false;
		try {
			flag = proxyActivity.onMenuItemSelected(featureId, item);
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onMenuItemSelected->ex:" + e.toString());
		}
		return flag ? true : super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		boolean flag = false;
		try {
			flag = proxyActivity.onMenuOpened(featureId, menu);
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onMenuOpened->ex:" + e.toString());
		}
		return flag ? true : super.onMenuOpened(featureId, menu);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		try {
			proxyActivity.onConfigurationChanged(newConfig);
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onBackPressed->ex:" + e.toString());
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		try {
			proxyActivity.onWindowFocusChanged(hasFocus);
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onBackPressed->ex:" + e.toString());
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			Method method = proxyActivity.getClass().getMethod("onDestroy", new Class<?>[]{});
			method.invoke(proxyActivity, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "real->YunbeeActivity-onDestroy->ex:" + e.toString());
		}
	}

}
