package cg.yunbee.cn.wangyoujar;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import cg.yunbee.cn.wangyou.loginSys.DyLoadingActivity;
import cg.yunbee.cn.wangyou.loginSys.DyLoginActivity;
import cg.yunbee.cn.wangyou.loginSys.LoginRecords;
import cg.yunbee.cn.wangyou.loginSys.UserInfo;
import cg.yunbee.cn.wangyou.loginSys.Utils;
import cg.yunbee.cn.wangyoujar.feeInfo.InitFeeInfo;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkConfig.SecretData;
import cg.yunbee.cn.wangyoujar.sdkpay.Sdk_Init;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils.IDialogValueSet;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.AppInfoManager;
import cg.yunbee.cn.wangyoujar.work.DeviceInfoManager;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import cg.yunbee.cn.wangyoujar.work.ServerCommunicationManager;
import cn.yunbee.cn.wangyoujar.joggle.IInitListener;

public class YunbeeTask extends AsyncTask<String, Void, Boolean> {
	
	private static final String JAR_VERSION = "w3.0";
	private boolean isInitFinished;
	private boolean isInitSuccess;
	private Activity activity;
	public Dialog initDialog;
	
	public static final int STATE_NO = 0;
	public static final int STATE_PAY = 1;
	public static final int STATE_LOGIN = 2;
	private int currentState = STATE_NO;
	
	private PayManager payManager;
	private Toast toast;
	
	private static IInitListener mInitListener;
	
	public static void setIInitListener(IInitListener initListener) {
		mInitListener = initListener;
		
		Log.i("chao", "---------------------setIInitListener--------------------------");
		Log.i("chao", "mInitListener: " + mInitListener);
	}
	
	public YunbeeTask(Activity activity) {
		this.activity = activity;
		toast = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
				
	}
	
	public boolean isFnished() {
		return isInitFinished;
	}
	
	public boolean isSuccess() {
		
		return isInitSuccess;
	}
	
	
	public void pay(Activity activity, PayManager payManager) {
		TAGS.log("-------------------pay-------------------");
		this.activity = activity;
		this.payManager = payManager;
		currentState = STATE_PAY;
		//��ʼ����ɾ�ֱ�ӽ���֧������
		if(isInitFinished) {
			doPay();
		}
	}
	
	public void login(Activity activity) {
		TAGS.log("-------------------login-------------------");
		this.activity = activity;
		currentState = STATE_LOGIN;
		if(isInitFinished)
			doLogin(false, false);
	}
	
	public void payLogin(Activity activity) {
		TAGS.log("-------------------payLogin-------------------");
		TAGS.log("isInitFinished: " + isInitFinished);
		this.activity = activity;
		currentState = STATE_LOGIN;
		if(isInitFinished)
			doLogin(true, true);
	}
	

	@Override
	protected void onPreExecute() {
		showDialog("���ڳ�ʼ��", true);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		TAGS.log("-------------------YunbeeTask->doInBackground-------------------");
		synchronized (this) {
			YunbeeVice.gameJSONInfo = ServerCommunicationManager.getGameJsonInfo(activity);
			
			if(Util.isEmpty(YunbeeVice.gameName)) 
				YunbeeVice.gameName = YunbeeVice.gameJSONInfo.gameName;
			
			initData();
			
			if(isCancelled()) {
				return false;
			}
			
			isInitSuccess = initBase();
			
			if(isInitSuccess)
				isInitSuccess = initSDK();
			
			return isInitSuccess;
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		TAGS.log("-----------------------------YunbeeTaskInit->onPostExecute-----------------------------------");
		TAGS.log("currentState: " + currentState);
		isInitFinished = result;
		if(currentState == STATE_PAY) {
			doPay();
		} else if(currentState == STATE_LOGIN) {
			doLogin(false, false);
		} else {
			DialogUtils.closeDialog(activity, initDialog);
			initDialog = null;
			if(!result)
				initFail();
			else
				initSuccess();
		}
	}
	
	//��ʼ�����֮����õķ���
	private void doPay() {
		TAGS.log("-------------------doPay-------------------");
		TAGS.log("doPay->YunbeeVice.doPayFlag:" + YunbeeVice.doPayFlag);
		if(isInitSuccess) {
			new PayTask(payManager).execute();
		} else {
			initFail();
		}
	}
	
	private void initFail() {
		TAGS.log("-----------------------------initFail-----------------------------------");
		if(mInitListener == null) {
			toast.setText("��ʼ��ʧ�ܣ�");
			toast.show();
		} else {
			mInitListener.initFail();
		}
	}
	
	private void initSuccess() {
		TAGS.log("---------------------------initSuccess------------------------------------");
		TAGS.log("mInitListener: " + mInitListener);
		if(mInitListener != null) {
			mInitListener.initSuccess();
		}
	}
	
	//��ʼ�����֮����õķ���
	private void doLogin(boolean isPop, boolean isPay) {
		TAGS.log("---------------------------doLogin------------------------------------");
		TAGS.log("doLogin->isInitSuccess:" + isInitSuccess);
		if(isInitSuccess) {
			new LoginTask(activity, isPop, isPay).execute();
		} else
			initFail();
	}
	
	
	
	class PayTask extends AsyncTask<String, Void, Boolean> {
		
		private PayManager payManager;

		private PayTask(PayManager payManager) {
			this.payManager = payManager;
		}
		
		@Override
		protected void onPreExecute() {
			if(payManager != null)
				payManager.setToast(toast);
			showDialog("֧����,���Ժ�", false);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			TAGS.log("-------------------PayTask->doInBackground-------------------");
			synchronized (YunbeeTask.this) {
				if(payManager != null) {
					payManager.run();
				}
				return true;
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			DialogUtils.closeDialog(activity, initDialog);
			initDialog = null;
		}
		
	}
	
	class LoginTask extends AsyncTask<String, Void, Boolean> {
		
		private Activity activity;
		private boolean isPop;
		private boolean isPay;
		
		private LoginTask(Activity activity, boolean isPop, boolean isPay) {
			this.activity = activity;
			this.isPop = isPop;
			this.isPay = isPay;
		}
		
		@Override
		protected void onPreExecute() {
			showDialog("���Ժ�", false);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			TAGS.log("-------------------LoginTask->doInBackground-------------------");
			synchronized (YunbeeTask.this) {
				popLogin(activity, isPop, isPay);
				return true;
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			DialogUtils.closeDialog(activity, initDialog);
			initDialog = null;
		}
		
		
		
		
		private boolean checkAutoLogin(Activity activity) {
			List<UserInfo> allUser = LoginRecords.getInstance(activity).getAllListUsers();
			if(allUser != null && allUser.size() > 0) {
				Collections.sort(allUser);
				UserInfo lastUser = allUser.get(0);
				if(lastUser.isAutoLogin() && lastUser.isHasPwd()) {
					Intent loginAcIt = new Intent(activity, DyLoadingActivity.class);
					if(lastUser.isNormalUser()) {
						loginAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_LOGIN);
					} else {
						loginAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_QUICK_LOGIN);
					}
					loginAcIt.putExtra(DyLoadingActivity.USERINFO, lastUser);
					activity.startActivity(loginAcIt);
					return true;
				}
			}
			return false;
		}
		
		
		
		
		private void popLogin(Activity activity, boolean isPop, boolean isPay) {
			TAGS.log("-------------------popLogin-------------------");
			if(Yunbee.isLogin) {
				TAGS.log("��½�У����Ժ�����!");
				return;
			}
			
			if(activity == null || (Utils.isHasSurface())) return;
			
			Yunbee.isLogin = true;
			TAGS.log("isPop:" + isPop);
			final Activity finalActivity = activity;
			final boolean finalIsPop = isPop;
			final boolean finalIsPay = isPay;
			if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
				Util.replaceClassLoader_api17(getClass().getClassLoader(), activity);
				if(finalIsPop) {
					if(finalIsPay) {
						toast.setText("���¼��Ϸ");
						toast.show();
					}
					activity.startActivity(new Intent(activity, DyLoginActivity.class));
				} else {
					if(!checkAutoLogin(activity)) {
						activity.startActivity(new Intent(activity, DyLoginActivity.class));
					}
				}
				
			} else {
				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						Util.replaceClassLoader_api17(YunbeeTask.this.getClass().getClassLoader(), finalActivity);
						if(finalIsPop) {
							if(finalIsPay) {
								toast.setText("���¼��Ϸ");
								toast.show();
							}
							finalActivity.startActivity(new Intent(finalActivity, DyLoginActivity.class));
						} else {
							if(!checkAutoLogin(finalActivity)) {
								finalActivity.startActivity(new Intent(finalActivity, DyLoginActivity.class));
							}
						}
						
					}
					
				});
			}
		}
		
	}
	
	
	private void showDialog(String msg, boolean isCanBack) {
		if(activity.isFinishing()) return;
		if(initDialog == null) {
			initDialog = DialogUtils.getSimpleRotateDialog_dotRun(activity, true, 12, msg, dy.compatibility.work.Utils.getResourceId(activity,
					"drawable", "dianyou_gray_rotate_icon2"));
			((DialogUtils.IDialogValueSet)initDialog).setIsCanBack(isCanBack);
		} else {
			DialogUtils.IDialogValueSet valueDialog = (IDialogValueSet) initDialog;
			valueDialog.setTextValue(msg);
			valueDialog.setIsCanBack(isCanBack);
		}
		if(!activity.isFinishing())
			initDialog.show();
	}
	
	
	
	
	
	protected boolean initBase() {
		/*SecretData secretData = SecretData.getInstance();
		if(secretData.isSDKEmpty()) {
			TAGS.log("����json�ļ���û�������κ�sdkͨ��, ��ʼ��ʧ��!");
			return false;
		}*/
		
		boolean isSuccess = ServerCommunicationManager.initBase(activity);
		
		return isSuccess;
		
	}
	
	
	private void initData() {
		TAGS.log("��Ϸ����: " + YunbeeVice.gameJSONInfo.gameName);
		
		TAGS.log("��ʼִ��APK��init����");
		/*
		 * ��¼jar���汾
		 */
		SecretData.getInstance().setJarVersion(JAR_VERSION);
		/*
		 * ��ʼ��app��Ϣ
		 */
		TAGS.log("��ʼ��AppInfo...");
		AppInfoManager.initData(activity);
		TAGS.log("AppInfo��ʼ�����");
		/*
		 * ��ʼ���豸��Ϣ
		 */
		TAGS.log("��ʼ��DeviceInfo...");
		DeviceInfoManager.initDeviceInfo(activity);
		TAGS.log("DeviceInfo��ʼ�����");
		
		
		InitFeeInfo.getInstance().setListSdk(SecretData.getInstance().getSdkConfig());
	}
	

	
	public boolean initSDK() {
		/*SecretData secretData = SecretData.getInstance();
		if(secretData.isSDKEmpty()) {
			TAGS.log("����json�ļ���û�������κ�sdkͨ��, ��ʼ��ʧ��!");
			initFail();
			return false;
		}*/
		
		//����û����µ����˳�ʼ���ӿڣ���ʱ��������´�������������initSdk()�����ĵڶ�������Ϊfalse���������ڽ��������֧��ʱ��������ذ�ť�ٴλص����ǵĽ����л���������ԭ���Ǽ�������ʧ��ɵģ�Ŀǰԭ���в���ȷ��
		//��������sdk�Ƿ������
		//checkSdk3Update();
		Sdk_Init.initSdk(activity, false);
		return true;
	}

	
}
