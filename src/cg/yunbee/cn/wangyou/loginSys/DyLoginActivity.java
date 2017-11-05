package cg.yunbee.cn.wangyou.loginSys;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import cg.yunbee.cn.wangyou.loginSys.Utils.DialogListener;
import cg.yunbee.cn.wangyoujar.Yunbee;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;

public class DyLoginActivity extends Activity implements ILoginEvents {
	
	private TextView register, login, quickLogin;
	private TextView passwdStatus, showMore, changePasswd, forgetPasswd;
	private EditText et_passwd;
	private ViewGroup container;
	private LinearLayout cbGroupRmemeberPwd, cbGroupAutoLogin;
	
	private PopupWindow mPopupWindow;
	
	//自动提示文本框
	private AutoCompleteTextView autoTv;
	private DySpinnerItemAdpater usernameAdapter;
	private DyListViewAdapter listViewAdapter;
	private Toast toast;
	
	private Map<String, UserInfo> allUserMap;
	
	public static Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ResourceUtils.getLayoutId(this, "dy_base_layout_login"));
		Utils.setFinishOnTouchOutside(this, false);
		initViews();
		initData();
		setOnClickListeners();
		
		activity = this;
		
	}
	
	
	
	private final TextWatcher accountInputWatcher = new TextWatcher() {
		@Override  
	    public void beforeTextChanged(CharSequence s, int start, int count,  
	            int after) {  
	          
	    }  
	  
	    @Override  
	    public void onTextChanged(CharSequence s, int start, int before, int count) { 
	    	 if(allUserMap != null) {
		    	 UserInfo userInfo = allUserMap.get(s.toString());
		    	 passwordInputWatcher.setIsJust(false);
		    	 if(userInfo != null) {
		    		 if(userInfo.isHasPwd() && userInfo.isRememberPwd()) {
			    		 et_passwd.setText(userInfo.getPassword());
			    		 cbGroupRmemeberPwd.setSelected(true);
			    		 passwordInputWatcher.setIsJust(true);
			    		 autoTv.dismissDropDown();
			    		 if(userInfo.isAutoLogin()) {
			    			 cbGroupAutoLogin.setSelected(true);
			    		 }
			    		 Utils.setCursorToLast(et_passwd);
		    		 } else {
		    			 cbGroupRmemeberPwd.setSelected(false);
		    			 cbGroupAutoLogin.setSelected(false);
		    			 et_passwd.setText("");
		    		 }
		    	 } else {
		    		 cbGroupRmemeberPwd.setSelected(false);
	    			 cbGroupAutoLogin.setSelected(false);
		    		 et_passwd.setText("");
		    	 }
	    	 }
	    }  
	  
	    @Override  
	    public void afterTextChanged(Editable s) {  
	          
	    }  
	};
	
	private class PwdTextWatcherAdapter implements TextWatcher {
		
		protected boolean isJust;
		
		public void setIsJust(boolean isJust) {
			this.isJust = isJust;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
		
	}
	
	private final OnKeyListener pwdOnKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			 if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
	             if(passwordInputWatcher.isJust) {
	            	 passwordInputWatcher.setIsJust(false);
	            	 et_passwd.setText("");
	            	 return true;
	             }
	         } 
	         return false;
		}
		
	};
	
	
	private final PwdTextWatcherAdapter passwordInputWatcher = new PwdTextWatcherAdapter() {
		
		private String beforeTxt = "";
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			 beforeTxt = (s == null ? "" : s.toString());
		}
		
		
	    @Override  
	    public void onTextChanged(CharSequence s, int start, int before, int count) {  
	          if(allUserMap != null) {
	        	  if(isJust) {
	        		  isJust = false;
	        		  if(beforeTxt.length() > s.length()) {
	        			  et_passwd.setText("");
	        		  } else if(beforeTxt.length() == s.length()){
	        			  isJust = true;
	        		  } else {
	        			  String subAddStr = s.toString().substring(start, start + count);
	        			  et_passwd.setText(subAddStr);
	        			  Utils.setCursorToLast(et_passwd);
	        		  }
	        	  }
	          }
	    }  
	  
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && Utils.isOutOfBounds(this, event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	/*private boolean handleShowMore(TextView showMore2, MotionEvent event) {
		int[] location = new int[2];
		showMore.getLocationOnScreen(location);
		int showMoreX = location[0], showMoreY = location[1];
		int currentX = (int) event.getX();
		int currentY = (int) event.getY();
		
		container.getLocationOnScreen(location);
		
		int contentX = showMoreX - location[0];
		int contentY = showMoreY - location[1];
		
		Log.i("INFO", "currentX:" + currentX);
		Log.i("INFO", "currentY:" + currentY);
		
		
		if(new Rect(contentX, contentY, contentX + showMore.getWidth(), contentY + showMore.getHeight()).contains(currentX, currentY)) {
			Toast.makeText(this, "包含", 0).show();
			return true;
		}
		return false;
	}*/
	


	private void initData() {
		isDoThing = false;
		toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		List<UserInfo> allUser = LoginRecords.getInstance(this).getAllListUsers();
		allUserMap = LoginRecords.getInstance(this).convertToUserInfoMap(allUser);
		Collections.sort(allUser);
		int count = allUser.size();
		if(count > 0) {
			UserInfo lastLoginUser = allUser.get(0);
			autoTv.setText(lastLoginUser.getLoginName());
			Utils.setCursorToLast(autoTv);
			if(lastLoginUser.isHasPwd() && lastLoginUser.isRememberPwd()) {
				et_passwd.setText(lastLoginUser.getPassword());
				Utils.setCursorToLast(et_passwd);
				passwordInputWatcher.setIsJust(true);
				cbGroupRmemeberPwd.setSelected(true);
				if(lastLoginUser.isAutoLogin()) {
					//自动登录
					cbGroupAutoLogin.setSelected(true);
					TAGS.log("--自动登录--");
				} else {
					cbGroupAutoLogin.setSelected(false);
				}
				login.requestFocus();
			} else {
				et_passwd.requestFocus();
				cbGroupAutoLogin.setSelected(false);
				cbGroupRmemeberPwd.setSelected(false);
			}
		}
		List<UserName> allUserName = LoginRecords.getInstance(this).convertToLoginNameList(allUser);
		usernameAdapter = new DySpinnerItemAdpater(this, autoTv, this, allUserName);
		autoTv.setThreshold(1);
		autoTv.setAdapter(usernameAdapter);
	}
	
	
	private void showPopupWindow(View view) {
		
		 List<UserInfo> list = LoginRecords.getInstance(this).getAllListUsers();
		 if(list.isEmpty()) return;
		
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                ResourceUtils.getLayoutId(this, "dy_spiner_layout"), null);
        
        ListView listview = (ListView) contentView.findViewById(ResourceUtils.getId(this, "listview"));
        Collections.sort(list);
        listViewAdapter = new DyListViewAdapter(this, list, listview, this);
        listview.setAdapter(listViewAdapter);
        
        // 设置按钮的点击事件

        mPopupWindow = new PopupWindow(contentView,
        		view.getWidth(),  WindowManager.LayoutParams.WRAP_CONTENT, true);

        mPopupWindow.setTouchable(true);
        
        mPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				TAGS.log("DyLoginActivity-->onDismiss popupWindow");
				showMore.setBackgroundResource(ResourceUtils.getDrawableId(DyLoginActivity.this, "dy_more_down"));
			}
		});
        
        

        mPopupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.ic_dialog_info));
        // 设置好参数之后再show
        mPopupWindow.showAsDropDown(view);
        showMore.setBackgroundResource(ResourceUtils.getDrawableId(DyLoginActivity.this, "dy_more_up"));
    }

	private void setOnClickListeners() {
		final DyLoginListeners dyll = DyLoginListeners.newInstance(this);
		dyll.addListener(register, DyLoginListeners.FLAG_REGISTER);
		dyll.addListener(login, DyLoginListeners.FLAG_LOGIN);
		dyll.addListener(quickLogin, DyLoginListeners.FLAG_QUICK_LOGIN);
		dyll.addListener(passwdStatus, DyLoginListeners.FLAG_PWD_STATUS);
		dyll.addListener(showMore, DyLoginListeners.FLAG_SHOW_MORE);
		dyll.addListener(changePasswd, DyLoginListeners.FLAG_ALTER_PASSWORD);
		dyll.addListener(forgetPasswd, DyLoginListeners.FLAG_FORGET_PASSWORD);
		dyll.addListener(cbGroupRmemeberPwd, DyLoginListeners.FLAG_REMEMBER_PASSWORD);
		dyll.addListener(cbGroupAutoLogin, DyLoginListeners.FLAG_AUTO_LOGIN);
		
		autoTv.addTextChangedListener(accountInputWatcher);
		et_passwd.addTextChangedListener(passwordInputWatcher);
		et_passwd.setOnKeyListener(pwdOnKeyListener);
	}

	private void initViews() {
		container = (ViewGroup) findViewById(ResourceUtils.getId(this, "id_container"));
		cbGroupRmemeberPwd = (LinearLayout) findViewById(ResourceUtils.getId(this, "cbGroup_rememberPwd"));
		cbGroupAutoLogin = (LinearLayout) findViewById(ResourceUtils.getId(this, "cbGroup_autoLogin"));
		register = (TextView) findViewById(ResourceUtils.getId(this, "register"));
		login = (TextView) findViewById(ResourceUtils.getId(this, "loginBtn"));
		quickLogin = (TextView) findViewById(ResourceUtils.getId(this, "quickLogin"));
		passwdStatus = (TextView) findViewById(ResourceUtils.getId(this, "passwdStatus"));
		showMore = (TextView) findViewById(ResourceUtils.getId(this, "showMore"));
		changePasswd = (TextView) findViewById(ResourceUtils.getId(this, "changePasswd"));
		forgetPasswd = (TextView) findViewById(ResourceUtils.getId(this, "forgetPasswd"));
		et_passwd = (EditText) findViewById(ResourceUtils.getId(this, "et_passwd"));
		autoTv = (AutoCompleteTextView) findViewById(ResourceUtils.getId(this, "autocmt_account"));
		//默认选项
		cbGroupRmemeberPwd.setSelected(false);
		cbGroupAutoLogin.setSelected(false);
		
		forgetPasswd.setVisibility(View.GONE);
		cbGroupAutoLogin.setVisibility(View.GONE);
		
		et_passwd.setFocusable(true);
		et_passwd.setFocusableInTouchMode(true);
	}
	
	private UserInfo validEdit() {
		UserInfo userInfo = new UserInfo();
		String account = autoTv.getText().toString();
		String password = et_passwd.getText().toString();
		if(Utils.isEmpty(account)) {
			toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_account_empty"));
			toast.show();
			return null;
		}
		
		if(Utils.isEmpty(password)) {
			toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_pwd_empty"));
			toast.show();
			return null;
		}
		
		
		userInfo.setUserName(account);
		if(!UserInfo.VALID_OK.equals(userInfo.validForUserName(this))) {
			userInfo.setPhoneNumber(account);
			if(!UserInfo.VALID_OK.equals(userInfo.validForPhoneNumber(this))) {
				userInfo.setEmail(account);
				if(!UserInfo.VALID_OK.equals(userInfo.validForEmail(this))) {
					toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_account_all_cue"));
					toast.show();
					return null;
				}
			}
		}
		
		
		
		userInfo.setRememberPwd(cbGroupRmemeberPwd.isSelected());
		userInfo.setAutoLogin(cbGroupAutoLogin.isSelected());
		userInfo.setLoginName(account);
		userInfo.setUserName("");
		userInfo.setPhoneNumber("");
		userInfo.setEmail("");
		userInfo.setPassword(password);
		return userInfo;
		
	}
	
	private boolean isDoThing = false;

	@Override
	public void login() {
		Log.i("baba", "-----------login------------");
		if(isDoThing) 
			return;
		
		Utils.hideSoftInput(autoTv, this);
		
		UserInfo userInfo = validEdit();
		
		if(userInfo != null) {
			isDoThing = true;
			Intent loginAcIt = new Intent(this, DyLoadingActivity.class);
			
			UserInfo oldUserInfo = LoginRecords.getInstance(this).findUserInfo(userInfo.getLoginName());
			Log.i("baba", "oldUserInfo: " + oldUserInfo);
			if(oldUserInfo == null)
				loginAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_LOGIN);
			else
			{
				if(oldUserInfo.isNormalUser()) {
					loginAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_LOGIN);
				} else {
					loginAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_QUICK_LOGIN);
					userInfo.setNormalUser(false);
					userInfo.setDianId(oldUserInfo.getDianId());
				}
			}
			loginAcIt.putExtra(DyLoadingActivity.USERINFO, userInfo);
			startActivity(loginAcIt);
			finish();
		}
		
	}
	

	@Override
	public void register() {
		if(isDoThing) 
			return;
		isDoThing = true;
		Intent registerAcIt = new Intent(this, DyRegisterActivity.class);
		startActivity(registerAcIt);
		finish();
	}

	@Override
	public void quickLogin() {
		
		
		Utils.getDialog(this, new DialogListener() {
			
			@Override
			public void positive() {
				if(isDoThing) 
					return;
				isDoThing = true;
				Intent quickAcIt = new Intent(DyLoginActivity.this, DyLoadingActivity.class);
				quickAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_QUICK_REGISTER_LOGIN);
				startActivity(quickAcIt);
				finish();
			}
			
			@Override
			public void negative() {
				login();
			}
		}, "快速登录", "是否创建一个新账户？", "不创建", "创建").show();
		
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Yunbee.isLogin = false;
	}
	
	@Override
	protected void onDestroy() {
		DyLoginListeners.newInstance(this).clear();
		LoginRecords.getInstance(this).clear();
		if(activity == this)
			activity = null;
		super.onDestroy();
	}

	@Override
	public void pwdVisible() {
		if(passwdStatus.isSelected()) {
			Utils.setPwdVisible(et_passwd, false);
			Utils.setCursorToLast(et_passwd);
			passwdStatus.setBackgroundResource(ResourceUtils.getDrawableId(this, "dy_eye_close"));
			passwdStatus.setSelected(false);
		} else {
			Utils.setPwdVisible(et_passwd, true);
			Utils.setCursorToLast(et_passwd);
			passwdStatus.setBackgroundResource(ResourceUtils.getDrawableId(this, "dy_eye_open"));
			passwdStatus.setSelected(true);
		}
		
	
		
	}

	@Override
	public void showMore() {
		showPopupWindow(autoTv);
	}



	@Override
	public void alterPwd() {
		if(isDoThing) 
			return;
		isDoThing = true;
		Intent alterAcIt = new Intent(this, DyAlterActivity.class);
		startActivity(alterAcIt);
		finish();
	}



	@Override
	public void forgetPwd() {
		toast.setText(ResourceUtils.getStringId(this, "dy_alter_contact"));
		toast.show();
	}




	@Override
	public void autoLogin() {
		if(cbGroupAutoLogin.isSelected()) {
			cbGroupAutoLogin.setSelected(false);
		} else {
			cbGroupAutoLogin.setSelected(true);
			cbGroupRmemeberPwd.setSelected(true);
		}
	}

	@Override
	public void rememberPwd() {
		if(cbGroupRmemeberPwd.isSelected()) {
			cbGroupRmemeberPwd.setSelected(false);
			cbGroupAutoLogin.setSelected(false);
		} else
			cbGroupRmemeberPwd.setSelected(true);
	}

	@Override
	public void selectUser(UserInfo userInfo) {
		if(userInfo != null) {
			
			if(mPopupWindow != null && mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
			
			Log.i("INFO", "selectUser:" + userInfo.toString());
			if(userInfo.isHasPwd() && userInfo.isRememberPwd()) {
				et_passwd.setText(userInfo.getPassword());
				cbGroupRmemeberPwd.setSelected(true);
				passwordInputWatcher.setIsJust(true);
				if(userInfo.isAutoLogin()) {
					//自动登录
					cbGroupAutoLogin.setSelected(true);
				} else {
					cbGroupAutoLogin.setSelected(false);
				}
				autoTv.setText(userInfo.getLoginName());
				Utils.hideSoftInput(autoTv, this);
				Utils.setCursorToLast(et_passwd);
				Utils.setCursorToLast(autoTv);
				autoTv.clearFocus();
			} else {
				autoTv.setText(userInfo.getLoginName());
				et_passwd.setText("");
				et_passwd.setFocusable(true);
				et_passwd.setFocusableInTouchMode(true);
				et_passwd.requestFocus();
				cbGroupAutoLogin.setSelected(false);
				cbGroupRmemeberPwd.setSelected(false);
			}
			
			
		}
	}
	
	public static final int DELETE_USER_ACTV = 1;
	public static final int DELETE_USER_POPW = 2;

	@Override
	public void deleteRecord(UserInfo userInfo, int deleteType) {
		if(userInfo != null) {
			UserInfo userinfo = null;
			if(allUserMap != null)
				userinfo = allUserMap.remove(userInfo.getLoginName());
			if(deleteType == DELETE_USER_ACTV) {
				if(listViewAdapter != null)
					listViewAdapter.deleteUserInfo(userInfo);
			} else if(deleteType == DELETE_USER_POPW) {
				if(usernameAdapter != null)
					usernameAdapter.deleteUserInfo(userInfo);
				if(allUserMap.isEmpty())
					mPopupWindow.dismiss();
			}
			
			if(userinfo != null) {
				String loginName = userinfo.getLoginName();
				String userTxt = autoTv.getText().toString();
				if(loginName.equals(userTxt)) {
					et_passwd.setText("");
					autoTv.setText("");
					autoTv.requestFocus();
				}
			}
		}
	}
	
	
	

}
