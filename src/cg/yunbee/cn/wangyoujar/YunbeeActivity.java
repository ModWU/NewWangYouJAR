package cg.yunbee.cn.wangyoujar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class YunbeeActivity extends Activity {
	
	private Activity realActivity;
	
	private Resources resources;
	private Handler handler = new Handler(Looper.getMainLooper());
	
	public static final boolean isBack = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			resources = realActivity.getResources();
			int id = resources.getIdentifier("yunbee_startup", "layout",
					realActivity.getPackageName());
			View mView = LayoutInflater.from(realActivity).inflate(id, null);
			realActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
			realActivity.setContentView(mView);
			Log.i("INFO", "SINGLE_INSTANCE..: start intent");
			
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Log.i("INFO", "SINGLE_INSTANCE: start intent");
					Intent intent = new Intent(realActivity,
							ChannelActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					realActivity.startActivity(intent);
					realActivity.finish();
				}
			}, 1000);
				
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("INFO", "YunbeeActivity->proxy“Ï≥££∫" + e.toString());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			Log.i("INFO", "YunbeeActivity->proxy“Ï≥£2£∫" + e.toString());
		} catch (Error e) {
			e.printStackTrace();
			Log.i("INFO", "YunbeeActivity->proxy“Ï≥£3£∫" + e.toString());
		}
	}
	
	public YunbeeActivity() {
	}
	
	public void setRealActivity(Activity activity) {
		realActivity = activity;
	}
	
	
	/*------------------------*/
	@Override
	public void attachBaseContext(Context newBase) {
	}
	
	@Override
	public void onBackPressed() {
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	}
	
	@Override
	public void onAttachedToWindow() {
	}
	
	@Override
	public void onNewIntent(Intent intent) {
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
	public void onContentChanged() {
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return false;
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return false;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	}
	
	@Override
	public void onDestroy() {
	}

}
