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
 * 还有更好的做法：让CP不创建packagename.wxapi包，典游来自动生成这个包以及提前编译好的class类。然后用加载器加载进来。
 * @author Administrator
 *
 */
public class DYPayEntryActivity extends Activity {
	
	private Object proxyObj;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("INFO", "DYPayEntryActivity->onCreate");
		super.onCreate(savedInstanceState);
		
		try {
			Log.i("INFO", "proxyObj:" + proxyObj);
			
			Method m = proxyObj.getClass().getMethod("onCreate", new Class<?>[]{Bundle.class});
			m.invoke(proxyObj, new Object[]{savedInstanceState});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity->" + Log.getStackTraceString(e));
			Log.i("INFO", "DYPayEntryActivity->WXPayEntryActivity-onCreate->ex:" + e.toString());
		}
	}
	
	@Override
	public void attachBaseContext(Context newBase) {
		Log.i("INFO", "DYPayEntryActivity->attachBaseContext");
		super.attachBaseContext(newBase);
		try {
			proxyObj = YunbeeUtils.getInstance(this).getWXPayEntryActivity();
			Method setActivity = proxyObj.getClass().getMethod("setActivity", new Class<?>[]{Activity.class});
			setActivity.invoke(proxyObj, this);
			Method m = proxyObj.getClass().getMethod("attachBaseContext", new Class<?>[]{Context.class});
			m.invoke(proxyObj, new Object[]{newBase});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-attachBaseContext->ex:" + e.toString());
		}
	}

	@Override
	public void onAttachedToWindow() {
		Log.i("INFO", "DYPayEntryActivity->onAttachedToWindow");
		super.onAttachedToWindow();
		try {
			Method m = proxyObj.getClass().getMethod("onAttachedToWindow", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onAttachedToWindow->ex:" + e.toString());
		}
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i("INFO", "DYPayEntryActivity->onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
		try {
			Method m = proxyObj.getClass().getMethod("onRestoreInstanceState", new Class<?>[]{Bundle.class});
			m.invoke(proxyObj, new Object[]{savedInstanceState});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onRestoreInstanceState->ex:" + e.toString());
		}
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		try {
			Method m = proxyObj.getClass().getMethod("onNewIntent", new Class<?>[]{Intent.class});
			m.invoke(proxyObj, new Object[]{intent});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onNewIntent->ex:" + e.toString());
		}
	}
	
	@Override
	public void onStart() {
		Log.i("INFO", "DYPayEntryActivity->onStart");
		super.onStart();
		try {
			Method m = proxyObj.getClass().getMethod("onStart", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onStart->ex:" + e.toString());
		}
	}
	
	@Override
	public void onStop() {
		Log.i("INFO", "DYPayEntryActivity->onStop");
		super.onStop();
		try {
			Method m = proxyObj.getClass().getMethod("onStop", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onStop->ex:" + e.toString());
		}
	}
	
	@Override
	public void onRestart() {
		Log.i("INFO", "DYPayEntryActivity->onRestart");
		super.onRestart();
		try {
			Method m = proxyObj.getClass().getMethod("onRestart", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onRestart->ex:" + e.toString());
		}
	}
	
	@Override
	public void onResume() {
		Log.i("INFO", "DYPayEntryActivity->onResume");
		super.onResume();
		try {
			Method m = proxyObj.getClass().getMethod("onResume", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onResume->ex:" + e.toString());
		}
	}
	
	@Override
	public void onBackPressed() {
		try {
			Field field = proxyObj.getClass().getField("isBack");
			boolean isBack = field.getBoolean(proxyObj.getClass());
			if(isBack) {
				super.onBackPressed();
			}
			
			Method m = proxyObj.getClass().getMethod("onBackPressed", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onBackPressed->ex:" + e.toString());
		}
	}
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		try {
			Method m = proxyObj.getClass().getMethod("onContentChanged", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onContentChanged->ex:" + e.toString());
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.i("INFO", "DYPayEntryActivity->onPrepareOptionsMenu");
		boolean isReturn = false;
		try {
			Method m = proxyObj.getClass().getMethod("onPrepareOptionsMenu", new Class<?>[]{Menu.class});
			isReturn = (Boolean) m.invoke(proxyObj, new Object[]{menu});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onPrepareOptionsMenu->ex:" + e.toString());
		}
		return isReturn ? true : super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i("INFO", "DYPayEntryActivity->onMenuItemSelected:" + featureId);
		boolean isReturn = false;
		try {
			Method m = proxyObj.getClass().getMethod("onMenuItemSelected", new Class<?>[]{int.class, MenuItem.class});
			isReturn = (Boolean) m.invoke(proxyObj, new Object[]{featureId, item});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onMenuItemSelected->ex:" + e.toString());
		}
		return isReturn ? true : super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		Log.i("INFO", "DYPayEntryActivity->onMenuOpened:" + featureId);
		boolean isReturn = false;
		try {
			Method m = proxyObj.getClass().getMethod("onMenuOpened", new Class<?>[]{int.class, Menu.class});
			isReturn = (Boolean) m.invoke(proxyObj, new Object[]{featureId, menu});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onMenuOpened->ex:" + e.toString());
		}
		return isReturn ? true : super.onMenuOpened(featureId, menu);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("INFO", "DYPayEntryActivity->onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
		try {
			Method m = proxyObj.getClass().getMethod("onConfigurationChanged", new Class<?>[]{Configuration.class});
			m.invoke(proxyObj, new Object[]{newConfig});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onMenuOpened->ex:" + e.toString());
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.i("INFO", "DYPayEntryActivity->onWindowFocusChanged");
		super.onWindowFocusChanged(hasFocus);
		try {
			Method m = proxyObj.getClass().getMethod("onWindowFocusChanged", new Class<?>[]{boolean.class});
			m.invoke(proxyObj, new Object[]{hasFocus});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onWindowFocusChanged->ex:" + e.toString());
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			Method m = proxyObj.getClass().getMethod("onDestroy", new Class<?>[]{});
			m.invoke(proxyObj, new Object[]{});
		} catch (Exception e) {
			Log.i("INFO", "DYPayEntryActivity-onDestroy->ex:" + e.toString());
		}
	}

	/*@Override
	public void setActivity(Activity arg0) {
	}*/


	/*private IWXAPI api;
	
	private Class<?> pmClass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int layoutId = getLayoutId(WXPayEntryActivity.this,
				"dian_pay_wechat_result");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View mView = LayoutInflater.from(this).inflate(layoutId, null);
		setContentView(mView);

		pmClass = YunbeeUtils.getInstance(this).getPayManagerClass();
		api = WXAPIFactory.createWXAPI(this, getWeiXinAppid());
		api.handleIntent(getIntent(), this);
	}

	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout",
				paramContext.getPackageName());
	}

	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id",
				paramContext.getPackageName());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	public void onReq(BaseReq req) {
	}

	public void onResp(BaseResp resp) {
		int textViewId = getId(WXPayEntryActivity.this,
				"wechat_pay_result_display");
		TextView tv = (TextView) findViewById(textViewId);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			switch (resp.errCode) {
			case 0:// success
				tv.setText("购买成功");
				try {
					Method method = pmClass.getMethod("paySuccess", new Class<?>[]{});
					method.invoke(pmClass, new Object[]{});
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case -1:// failed
				tv.setText("支付失败：code" + resp.errCode);
				try {
					Method method = pmClass.getMethod("payAbort", new Class<?>[]{});
					method.invoke(pmClass, new Object[]{});
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case -2:// cancel
				tv.setText("支付取消");
				try {
					Method method = pmClass.getMethod("payCancel", new Class<?>[]{});
					method.invoke(pmClass, new Object[]{});
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
		
		finish();
	}
	
	
	public String getWeiXinAppid() {
		String content = loadFileFromAssets(this, "weixinConfig.json");
		Log.i("chao", "getWeiXinAppid:" + content);
		try {
			JSONObject jsObj = new JSONObject(content);
			String appid = jsObj.getString("appid");
			Log.i("chao", "appid:" + appid);
			return appid;
		} catch(Exception e) {
		}
		return "";
	}
	
	synchronized static String loadFileFromAssets(Context context,
			String fileName) {
		StringBuffer sbContent = new StringBuffer();
		InputStreamReader inputReader = null;
		BufferedReader bufReader = null;
		try {
			String myCharset = System.getProperty("file.encoding");
			inputReader = new InputStreamReader(context.getResources()
					.getAssets().open(fileName), "GBK");
			bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				sbContent.append(line);
		} catch (Exception e) {
		} finally {
			try {
				if (bufReader != null)
					bufReader.close();
				if (inputReader != null)
					inputReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 去掉不可见字符
		String content = sbContent.toString();
		content = content.replaceAll("\\s", "");
		return content;
	}	
	*/
}
