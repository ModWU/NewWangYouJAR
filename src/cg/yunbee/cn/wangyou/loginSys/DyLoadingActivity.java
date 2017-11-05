package cg.yunbee.cn.wangyou.loginSys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cg.yunbee.cn.wangyou.loginSys.UserAuthorize.IAuthorizeListener;
import cg.yunbee.cn.wangyoujar.Yunbee;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;
import cn.yunbee.cn.wangyoujar.joggle.ILoginListener;

public class DyLoadingActivity extends Activity {
	
	public static final String USERINFO = "dy_login_userinfo";
	public static final String TYPE = "dy_loading_type";
	
	public static final int TYPE_LOGIN = 0;
	public static final int TYPE_QUICK_REGISTER_LOGIN = 1;
	public static final int TYPE_QUICK_LOGIN = 2;
	public static final int TYPE_REGISTER = 3;
	public static final int TYPE_ALTER_PASSWORD = 4;
	public static final int TYPE_LOGOUT = 5;
	
	private ImageView loadingImg;
	
	private TextView lodingText;
	
	private UserInfo userInfo;
	
	static ILoginListener loginListener;
	
	private Toast toast;
	
	public static void setLoginListener(ILoginListener loginListener) {
		DyLoadingActivity.loginListener = loginListener;
	}
	
	public static void callLoginListener(Activity activity, boolean isSuccess) {
		if(loginListener != null) {
			UserToken userToken = LoginRecords.getInstance(activity).getUserToken();
			if(isSuccess)
				loginListener.loginSuccess(UserAuthorize.CODE_LOGIN_ALWAY_KEEP, userToken.getUserId(), userToken.getLoginName());
			else
				loginListener.loginFail(UserAuthorize.CODE_LOGIN_NORMAL_RETURN_FAIL, "µÇÂ¼Ê§°Ü");
		}
	}
	
	public static ILoginListener getLoginListener() {
		return loginListener;
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(ResourceUtils.getLayoutId(this, "dy_loading_dialog"));
		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		Utils.setFinishOnTouchOutside(this, false);
		lodingText = (TextView) findViewById(ResourceUtils.getId(this, "lodingText"));
		loadingImg = (ImageView) findViewById(ResourceUtils.getId(this, "lodingImg"));
		getData();
		runLoading();
	}
	
	@Override
	public void onBackPressed() {
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && Utils.isOutOfBounds(this, event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void login() {
		lodingText.setText(getString(ResourceUtils.getStringId(this, "dy_text_titile_loding")));
		UserAuthorize.build(this, userInfo).loginByNormal(new IAuthorizeListener() {
			
			@Override
			public void success(UserInfo userInfo, int code, String info) {
				
				TAGS.log("login success");
				TAGS.log("login userInfo:" + userInfo.toAllString());
				TAGS.log("login code:" + code);
				TAGS.log("login info:" + info);
				
				UserInfo oldUserInfo = LoginRecords.getInstance(DyLoadingActivity.this).findUserInfo(userInfo.getLoginName());
				
				TAGS.log("login oldUserInfo?" + oldUserInfo);
				
				if(oldUserInfo != null) {
					String oldLoginName = oldUserInfo.getLoginName();
					oldUserInfo.setPassword(userInfo.getPassword());
					oldUserInfo.setLoginName(userInfo.getLoginName());
					oldUserInfo.setAutoLogin(userInfo.isAutoLogin());
					oldUserInfo.setRememberPwd(userInfo.isRememberPwd());
					oldUserInfo.setLastLoginTime(System.currentTimeMillis());
					oldUserInfo.setDianId(userInfo.getDianId());
					oldUserInfo.setToken(userInfo.getToken());
					LoginRecords.getInstance(DyLoadingActivity.this).updateUserInfo(oldLoginName, oldUserInfo);
				} else {
					oldUserInfo = userInfo.copy();
					oldUserInfo.setLastLoginTime(System.currentTimeMillis());
					LoginRecords.getInstance(DyLoadingActivity.this).addUserInfo(oldUserInfo);
				}
				
				LoginRecords.getInstance(DyLoadingActivity.this)
				.saveUserToken(new UserToken(oldUserInfo.getLoginName(), oldUserInfo.getDianId(), oldUserInfo.getToken()));
				
				bindDianIdAndUid(oldUserInfo.copy());
			}
			
			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				TAGS.log("login fail");
				TAGS.log("login userInfo:" + userInfo);
				TAGS.log("login code:" + code);
				TAGS.log("login errorInfo:" + errorInfo);
				
				toast.setText(errorInfo);
				toast.show();
				toast.setText(errorInfo);
				toast.show();
				UserToken userToken = LoginRecords.getInstance(DyLoadingActivity.this).getUserToken();
				if(userToken == null) {
					startRootLogin();
				} else 
					finish();
			}
		});
	}
	
	
	private void bindDianIdAndUid(UserInfo userInfo) {
		lodingText.setText(getString(ResourceUtils.getStringId(this, "dy_login_bind_data")));
		Log.i("INFO", "..finaluserInfo..:" + userInfo);
		UserAuthorize.bindUidAndDianId(this, new IAuthorizeListener() {
			
			@Override
			public void success(UserInfo userInfo, int code, String info) {
				TAGS.log("bindDianIdAndUid success");
				TAGS.log("bindDianIdAndUid userInfo:" + userInfo);
				TAGS.log("bindDianIdAndUid code:" + code);
				TAGS.log("bindDianIdAndUid info:" + info);
				if(loginListener != null)
					loginListener.loginSuccess(code, userInfo.getDianId(), userInfo.getLoginName());
				else {
					toast.setText("µÇÂ¼³É¹¦!");
					toast.show();
				}
				Yunbee.isLogin = false;
				finish();
			}
			
			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				
				TAGS.log("bindDianIdAndUid fail");
				TAGS.log("bindDianIdAndUid userInfo:" + userInfo);
				TAGS.log("bindDianIdAndUid code:" + code);
				TAGS.log("bindDianIdAndUid errorInfo:" + errorInfo);
				LoginRecords.getInstance(DyLoadingActivity.this).clearUserToken();
				LoginRecords.getInstance(DyLoadingActivity.this).deleteUserInfo(userInfo.getLoginName());
				if(loginListener != null)
					loginListener.loginFail(code, errorInfo);
				else {
					toast.setText("µÇÂ¼Ê§°Ü!");
					toast.show();
				}
				Yunbee.isLogin = false;
				finish();
			}
		}, userInfo);
		
	}

	private void register() {
		lodingText.setText(getString(ResourceUtils.getStringId(this, "dy_text_titile_registing")));
		UserAuthorize.build(this, userInfo).registerByNormal(new IAuthorizeListener() {
			
			@Override
			public void success(UserInfo userInfo, int code, String info) {
				TAGS.log("register success");
				TAGS.log("register userInfo:" + userInfo.toAllString());
				TAGS.log("register code:" + code);
				TAGS.log("register info:" + info);
				toast.setText(info);
				toast.show();
				DyLoadingActivity.this.userInfo = userInfo.copy();
				
				LoginRecords.getInstance(DyLoadingActivity.this).addUserInfo(userInfo.copy());
				
				login();
			}
			
			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				TAGS.log("register fail");
				TAGS.log("register userInfo:" + userInfo);
				TAGS.log("register code:" + code);
				TAGS.log("register errorInfo:" + errorInfo);
				toast.setText(errorInfo);
				toast.show();
				startRootLogin();
			}
		});
	}

	protected void bind() {
		UserAuthorize.registerBind(new IAuthorizeListener() {
			
			@Override
			public void success(UserInfo userInfo, int code, String info) {
				TAGS.log("bind success");
				TAGS.log("bind userInfo:" + userInfo.toAllString());
				TAGS.log("bind code:" + code);
				TAGS.log("bind info:" + info);
				DyLoadingActivity.this.userInfo = userInfo;
				login();
			}
			
			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				TAGS.log("bind fail");
				TAGS.log("bind userInfo:" + userInfo);
				TAGS.log("bind code:" + code);
				TAGS.log("bind errorInfo:" + errorInfo);
				toast.setText(errorInfo);
				toast.show();
				startRootLogin();
			}
		}, userInfo);
	}

	private void getData() {
		Intent it = getIntent();
		
		int loadingType = it.getIntExtra(TYPE, TYPE_LOGIN);
		userInfo = it.getParcelableExtra(USERINFO);
		if(loadingType == TYPE_LOGIN) {
			login();
		} else if(loadingType == TYPE_QUICK_REGISTER_LOGIN) {
			quickRegisterAndLogin();
		} else if(loadingType == TYPE_QUICK_LOGIN) {
			quickLogin(userInfo);
		} else if(loadingType == TYPE_REGISTER){
			register();
		} else if(loadingType == TYPE_LOGOUT) {
			logout();
		} else {
			alterPwd();
		}
		
	}

	private void quickRegisterAndLogin() {
		lodingText.setText(getString(ResourceUtils.getStringId(this, "dy_text_titile_loding")));
		UserAuthorize.registerByTourist(new IAuthorizeListener() {
			
			@Override
			public void success(UserInfo userInfo, int code, String info) {
				TAGS.log("quickRegisterAndLogin success userInfo:" + userInfo.toAllString());
				TAGS.log("quickRegisterAndLogin success dianId:" + userInfo.getDianId());
				
				DyLoadingActivity.this.userInfo = userInfo.copy();
				
				LoginRecords.getInstance(DyLoadingActivity.this).addUserInfo(userInfo.copy());
				
				quickLogin(userInfo);
			}
			
			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				TAGS.log("quickRegister fail");
				TAGS.log("quickRegister userInfo:" + userInfo);
				TAGS.log("quickRegister code:" + code);
				TAGS.log("quickRegister errorInfo:" + errorInfo);
				if(loginListener != null)
					loginListener.loginFail(code, errorInfo);
				toast.setText(errorInfo);
				toast.show();
				startRootLogin();
			}
		});
	}
	
	private void quickLogin(UserInfo userInfo) {
		UserAuthorize.loginByTourist(this, new IAuthorizeListener() {
			
			@Override
			public void success(UserInfo userInfo, int code, String info) {
				TAGS.log("quickLogin success");
				TAGS.log("quickLogin userInfo:" + userInfo.toAllString());
				TAGS.log("quickLogin code:" + code);
				TAGS.log("quickLogin info:" + info);
				
				UserInfo oldUserInfo = LoginRecords.getInstance(DyLoadingActivity.this).findUserInfo(userInfo.getLoginName());
				
				if(oldUserInfo != null) {
					String oldLoginName = oldUserInfo.getLoginName();
					oldUserInfo.setPassword(userInfo.getPassword());
					oldUserInfo.setLoginName(userInfo.getLoginName());
					oldUserInfo.setAutoLogin(userInfo.isAutoLogin());
					oldUserInfo.setRememberPwd(userInfo.isRememberPwd());
					oldUserInfo.setLastLoginTime(System.currentTimeMillis());
					oldUserInfo.setDianId(userInfo.getDianId());
					oldUserInfo.setToken(userInfo.getToken());
					LoginRecords.getInstance(DyLoadingActivity.this).updateUserInfo(oldLoginName, oldUserInfo);
				} else {
					oldUserInfo = userInfo.copy();
					oldUserInfo.setLastLoginTime(System.currentTimeMillis());
					LoginRecords.getInstance(DyLoadingActivity.this).addUserInfo(oldUserInfo);
				}
				
				LoginRecords.getInstance(DyLoadingActivity.this)
				.saveUserToken(new UserToken(oldUserInfo.getLoginName(), oldUserInfo.getDianId(), oldUserInfo.getToken()));
				
				bindDianIdAndUid(oldUserInfo);
			}
			
			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				TAGS.log("quickLogin fail");
				TAGS.log("quickLogin userInfo:" + userInfo);
				TAGS.log("quickLogin code:" + code);
				TAGS.log("quickLogin errorInfo:" + errorInfo);
				if(loginListener != null)
					loginListener.loginFail(code, errorInfo);
				toast.setText(errorInfo);
				toast.show();
				UserToken userToken = LoginRecords.getInstance(DyLoadingActivity.this).getUserToken();
				if(userToken == null) {
					startRootLogin();
				} else 
					finish();
			}
		}, userInfo);
	}
	
	
	private void logout() {
		lodingText.setText(getString(ResourceUtils.getStringId(this, "dy_logout_loading_txt")));
		UserToken userToken = LoginRecords.getInstance(this).getUserToken();
		UserInfo tmpUserInfo = new UserInfo();
		tmpUserInfo.setLoginName(userToken.getLoginName());
		tmpUserInfo.setDianId(userToken.getUserId());
		tmpUserInfo.setToken(userToken.getUserToken());
		UserAuthorize.logout(this, new IAuthorizeListener() {

			@Override
			public void success(UserInfo userInfo, int code, String info) {
				TAGS.log("doLogout success");
				TAGS.log("doLogout userInfo:" + userInfo.toAllString());
				TAGS.log("doLogout code:" + code);
				TAGS.log("doLogout info:" + info);
				LoginRecords.getInstance(DyLoadingActivity.this).clearUserToken();
				LogoutProvider.logoutSuccess(userInfo.getDianId(), userInfo.getLoginName());
				LogoutProvider.isDoThing = false;
				finish();
			}

			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				TAGS.log("doLogout fail");
				TAGS.log("doLogout userInfo:" + userInfo);
				TAGS.log("doLogout code:" + code);
				TAGS.log("doLogout errorInfo:" + errorInfo);
				String loginName = (userInfo == null ? "" : userInfo.getLoginName());
				LogoutProvider.logoutFail(loginName, code, errorInfo);
				LogoutProvider.isDoThing = false;
				finish();
			}
			
		}, tmpUserInfo);
	}

	private void alterPwd() {
		lodingText.setText(getString(ResourceUtils.getStringId(this, "dy_alter_loading")));
		Intent it = getIntent();
		UserAuthorize.build(this, userInfo).alterPassword(new IAuthorizeListener() {
			
			@Override
			public void success(UserInfo userInfo, int code, String info) {
				TAGS.log("alterPwd success");
				TAGS.log("alterPwd userInfo:" + userInfo.toAllString());
				TAGS.log("alterPwd code:" + code);
				TAGS.log("alterPwd info:" + info);
				//LoginRecords.getInstance(DyLoadingActivity.this).clearPwd(userInfo.getLoginName());
				UserInfo oldUserInfo = LoginRecords.getInstance(DyLoadingActivity.this).findUserInfo(userInfo.getLoginName());
				if(oldUserInfo != null) {
					TAGS.log("alterPwd oldUserInfo: not null");
					oldUserInfo.setPassword(userInfo.getPassword());
					LoginRecords.getInstance(DyLoadingActivity.this).updateUserInfo(userInfo.getLoginName(), oldUserInfo);
				} else {
					TAGS.log("alterPwd oldUserInfo: is null");
					userInfo.setUserName(userInfo.getLoginName());
					userInfo.setRememberPwd(true);
					LoginRecords.getInstance(DyLoadingActivity.this).addUserInfo(userInfo);
				}
				
				toast.setText(info);
				toast.show();
				startRootLogin();
			}
			
			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				TAGS.log("alterPwd fail");
				TAGS.log("alterPwd userInfo:" + userInfo);
				TAGS.log("alterPwd code:" + code);
				TAGS.log("alterPwd errorInfo:" + errorInfo);
				toast.setText(errorInfo);
				toast.show();
				startRootLogin();
			}
		}, it.getStringExtra(DyAlterActivity.NEW_PASSWORD));
	}
	
	private void startRootLogin() {
		Intent it = new Intent(this, DyLoginActivity.class);
		startActivity(it);
		finish();
	}

	private void runLoading() {
		loadingImg.startAnimation(AnimationUtils.loadAnimation(this, ResourceUtils.getAnimationId(this, "dy_loading_animation")));
	}
}
