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
	 * ��ʼ���ӿ�
	 * 
	 * @param activity
	 *            ��Ϸ��Activity
	 */
	public synchronized void yunbeeInit(Activity activity) {
		this.activity = activity;
		TAGS.log("-------------------�����µ�����sdk��ʼ����ʼ��version(15:06):" + LOCAL_VERSION + "---------------------");
		TAGS.log("init Thread: " + Thread.currentThread().toString() + "--->Thread id:" + Thread.currentThread().getId());
		isPaying = false;
		isLogin = false;
		yunbeeVice.yunbeeInit(activity);
	}
	
	
	/**
	 * ����֧���ӿ�
	 * 
	 * @param activity
	 *            ��Ϸ��Activity
	 * @param propId
	 *            ���ߵ�Ψһ���
	 * @param param
	 *            �Զ��������֧�����֮���ԭ��post��cpָ����ַ���������֮����&���ӣ�����a=1&b=2
	 * @param payCallBack
	 *            ֧���ص�
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
				Util.showToastAtMainThread(toast, "δ��ʼ��!");
			} else {
				Util.showToastAtMainThread(toast, "����֧����, ���Ժ�...");
			}
		}
	}
	
	/**
	 * �Ƿ���debug���Թ���,���Ϊ: yunbee_processing
	 */
	public synchronized void setDebug(boolean isDebug) {
		yunbeeVice.setDebug(isDebug);
	}
	
	/**
	 * ����������Ϸ����
	 */
	public synchronized void setGameName(String gameName) {
		yunbeeVice.setGameName(gameName);
	}
	
	/**
	 * ��֧���ɹ�֮��ɵ��ø÷��� --����ʹ��: ���ڲ���ѯ
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
	 * ��֧���ɹ�֮��ɵ��ø÷���--����ʹ��: ���ڲ���ѯ
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
