package cg.yunbee.cn.wangyoujar;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cn.yunbee.cn.wangyoujar.joggle.IInitListener;
import cn.yunbee.cn.wangyoujar.joggle.ILoginListener;
import cn.yunbee.cn.wangyoujar.joggle.ILogoutListener;
import cn.yunbee.cn.wangyoujar.joggle.IPayCallBack;
import cn.yunbee.cn.wangyoujar.joggle.IQueryListener;
import cn.yunbee.cn.wangyoujar.joggle.IYunbee;

public class Yunbee implements IYunbee {
	
	public static boolean isPaying = false;
	
	public static boolean isLogin = false;

	private Toast toast;
	
	private static volatile Yunbee INSTANCE;
	
	public static final String LOCAL_VERSION = "2";
	
	public YunbeeVice yunbeeVice;
	
	private Activity activity;
	
	private Yunbee(Activity activity) {
		yunbeeVice  = new YunbeeVice(activity);
	}
	
	public static Yunbee getInstance(Activity activity) {
		if(INSTANCE == null) {
			synchronized (Yunbee.class) {
				if(INSTANCE == null) {
					INSTANCE = new Yunbee(activity);
				}
			}
		}
		
		return INSTANCE;
	}
	
	/**
	 * 初始化接口
	 * 
	 * @param activity
	 *            游戏的Activity
	 */
	public synchronized void yunbeeInit(Activity activity) {
		this.activity = activity;
		TAGS.log("-------------------典游新的网游sdk初始化开始啰version(15:06):" + LOCAL_VERSION + "---------------------");
		TAGS.log("init Thread: " + Thread.currentThread().toString() + "--->Thread id:" + Thread.currentThread().getId());
		isPaying = false;
		isLogin = false;
		yunbeeVice.yunbeeInit(activity);
	}
	
	
	/**
	 * 网游支付接口
	 * 
	 * @param activity
	 *            游戏的Activity
	 * @param propId
	 *            道具的唯一编号
	 * @param param
	 *            自定义参数，支付完成之后会原样post到cp指定地址，多个参数之间用&连接，类似a=1&b=2
	 * @param payCallBack
	 *            支付回调
	 */
	@Override
	public synchronized void oGpay(Activity activity, String propId, String propName, String propPrice, String cpprivate,
			IPayCallBack payCallBack) {
		if (!isPaying && yunbeeVice != null) {
			yunbeeVice.oGpay(activity, propId, propName, propPrice, cpprivate, payCallBack);
			toast = null;
		} else {
			if(toast == null) {
				toast = Util.getToast(activity);
			}
			if(yunbeeVice == null) {
				Util.showToastAtMainThread(toast, "未初始化!");
			} else {
				Util.showToastAtMainThread(toast, "正在支付中, 请稍后...");
			}
		}
	}
	
	/**
	 * 是否开启debug调试功能,标记为: yunbee_processing
	 */
	public synchronized void setDebug(boolean isDebug) {
		yunbeeVice.setDebug(isDebug);
	}
	
	/**
	 * 重新设置游戏名称
	 */
	public synchronized void setGameName(String gameName) {
		yunbeeVice.setGameName(gameName);
	}
	
	/**
	 * 当支付成功之后可调用该方法 --单机使用: 用于不查询
	 * @return
	 */
	public synchronized void pc_query(Activity activity, IQueryListener listener) {
		yunbeeVice.pc_query(activity, listener);
	}
	
	
	

	@Override
	public String getVersionCode() {
		return LOCAL_VERSION;
	}


	@Override
	public synchronized void exit(Activity activity) {
		Log.i("INFO", "exit: initActivity:" + this.activity);
		Log.i("INFO", "exit: exitActivity:" + activity);
		if(this.activity == activity) {
			yunbeeVice.exit(activity);
			isPaying = false;
			isLogin = false;
			toast = null;
			this.activity = null;
		}
	}

	/**
	 * 当支付成功之后可调用该方法--单机使用: 用于不查询
	 * @return
	 */
	@Override
	public synchronized void pc_no_query() {
		yunbeeVice.pc_no_query();
	}


	@Override
	public synchronized void doLogin(Activity activity) {
		yunbeeVice.doLogin(activity);
	}

	
	
	@Override
	public boolean isAutoLogin() {
		return yunbeeVice.isAutoLogin();
	}

	@Override
	public synchronized void setAutoLogin(boolean isAutoLogin) {
		yunbeeVice.setAutoLogin(isAutoLogin);
	}

	@Override
	public synchronized void doLogout(Activity activity, boolean isPopOutWindow) {
		yunbeeVice.doLogout(activity, isPopOutWindow);
	}


	@Override
	public synchronized void setLoginListener(ILoginListener listener) {
		yunbeeVice.setLoginListener(listener);
	}

	@Override
	public synchronized void setLogoutListener(ILogoutListener listener) {
		yunbeeVice.setLogoutListener(listener);
	}

	@Override
	public synchronized void setInitListener(IInitListener listener) {
		yunbeeVice.setInitListener(listener);
	}

}
