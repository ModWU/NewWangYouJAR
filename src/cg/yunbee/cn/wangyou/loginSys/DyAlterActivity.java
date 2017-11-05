package cg.yunbee.cn.wangyou.loginSys;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cg.yunbee.cn.wangyou.loginSys.UserAuthorize.IAuthorizeListener;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;

public class DyAlterActivity extends Activity {
	
	private TextView alterBackToLogin, alter;
	
	private EditText et_alter_account, et_alter_passwd,  et_alter_passwd_new;
	
	private TextView alter_passwd_eye_1, alter_passwd_eye_2;
	
	private Toast toast;
	
	public static final String NEW_PASSWORD = "newpwd";
	
	private String newpwd = "";
	
	
	public static Activity activity;
	
	
	@Override
	protected void onDestroy() {
		if(activity == this)
			activity = null;
		super.onDestroy();
	}
	//验证码
	private EditText et_vef_code;
	private Button btn_vef_resp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ResourceUtils.getLayoutId(this, "dy_base_layout_alter"));
		Utils.setFinishOnTouchOutside(this, false);
		initData();
		activity = this;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && Utils.isOutOfBounds(this, event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void initData() {
		isDoThing = false;
		alterBackToLogin = (TextView) findViewById(ResourceUtils.getId(this, "alterBackToLogin"));
		alter = (TextView) findViewById(ResourceUtils.getId(this, "alter"));
		et_alter_account = (EditText) findViewById(ResourceUtils.getId(this, "et_alter_account"));
		et_alter_passwd = (EditText) findViewById(ResourceUtils.getId(this, "et_alter_passwd"));
		et_alter_passwd_new = (EditText) findViewById(ResourceUtils.getId(this, "et_alter_passwd_new"));
		alter_passwd_eye_1 = (TextView) findViewById(ResourceUtils.getId(this, "alter_passwd_eye_1"));
		alter_passwd_eye_2 = (TextView) findViewById(ResourceUtils.getId(this, "alter_passwd_eye_2"));
		et_vef_code = (EditText) findViewById(ResourceUtils.getId(this, "id_vef_code"));
		btn_vef_resp = (Button) findViewById(ResourceUtils.getId(this, "id_vef_resp"));
		
		toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		
		alter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final UserInfo finalUserInfo = validEdit();
				
				if(et_vef_code == null) {
					if(finalUserInfo  != null) {
						alter(finalUserInfo);
					}
				} else {
					if(finalUserInfo != null) {
						String verityCode = vefityRefCode();
						if(verityCode != null)
							UserAuthorize.confirmVertifyCode(DyAlterActivity.this, new IAuthorizeListener() {
								
								@Override
								public void success(UserInfo userInfo, int code, String info) {
									alter(finalUserInfo);
								}
								
								@Override
								public void fail(UserInfo userInfo, int code, String errorInfo) {
									toast.setText("验证码错误，请稍后再试!");
									toast.show();
								}
							}, finalUserInfo.getPhoneNumber(), verityCode);
					}
				}
				
			}
			
		});
		
		alterBackToLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.returnRoot(DyAlterActivity.this);
			}
		});
		
		
		alter_passwd_eye_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(alter_passwd_eye_1.isSelected()) {
					Utils.setPwdVisible(et_alter_passwd, false);
					Utils.setCursorToLast(et_alter_passwd);
					alter_passwd_eye_1.setBackgroundResource(ResourceUtils.getDrawableId(DyAlterActivity.this, "dy_eye_close"));
					alter_passwd_eye_1.setSelected(false);
				} else {
					Utils.setPwdVisible(et_alter_passwd, true);
					Utils.setCursorToLast(et_alter_passwd);
					alter_passwd_eye_1.setBackgroundResource(ResourceUtils.getDrawableId(DyAlterActivity.this, "dy_eye_open"));
					alter_passwd_eye_1.setSelected(true);
				}
				
				
			}
			
		});
		
		
		
		alter_passwd_eye_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(alter_passwd_eye_2.isSelected()) {
					Utils.setPwdVisible(et_alter_passwd_new, false);
					Utils.setCursorToLast(et_alter_passwd_new);
					alter_passwd_eye_2.setBackgroundResource(ResourceUtils.getDrawableId(DyAlterActivity.this, "dy_eye_close"));
					alter_passwd_eye_2.setSelected(false);
				} else {
					Utils.setPwdVisible(et_alter_passwd_new, true);
					Utils.setCursorToLast(et_alter_passwd_new);
					alter_passwd_eye_2.setBackgroundResource(ResourceUtils.getDrawableId(DyAlterActivity.this, "dy_eye_open"));
					alter_passwd_eye_2.setSelected(true);
				}
			}
			
		});
		
		
		
		if(btn_vef_resp != null) {
			btn_vef_resp.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					Toast.makeText(DyAlterActivity.this, "点击获取验证码", 0).show();
					obtainVefCode();
				}
				
			});
		}
		
	}
	
	protected void obtainVefCode() {
		String account = et_alter_account.getText().toString();
		boolean isPhoneNumber = UserInfo.validForPhoneNumber(account, this).equals(UserInfo.VALID_OK);
		boolean isEmail = UserInfo.validForEmail(account, this).equals(UserInfo.VALID_OK);
		boolean isVefSuccess = isPhoneNumber || isEmail;
		if(isVefSuccess) {
			btn_vef_resp.setEnabled(false);
			btn_vef_resp.setBackgroundColor(Color.parseColor("#77555556"));
			if(isPhoneNumber) {
				obtainVefCodeByPhone(account);
			} else {
				//obtainVefCodeByEmail();
			}
			
			
		} else {
			toast.setText("请输入正确的手机号或邮箱");
			toast.show();
		}
	}
	/*final Timer timer = new Timer();
    timer.schedule(new TimerTask() {

   	private int currentSecond = 60;
   	 
		@Override
		public void run() {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					if (currentSecond <= 0) {
						timer.cancel();
						et_phone_number.setEnabled(true);
                       tv_vef_resp.setEnabled(true);
                       tv_vef_resp.setBackgroundColor(-14890607);
                       tv_vef_resp.setTextColor(-1);
                       tv_vef_resp.setText("重新获取");
					} else {
						tv_vef_resp.setText((currentSecond--) + "s之后可重新获取");
					}
				}
				
			});
			
		}
   	 
    }, 0, 1000);*/
	
	
	private void startTimerOnVerCode() {
		
	}

	private void obtainVefCodeByPhone(String phoneNumber) {
		UserAuthorize.getVertifyCodeByPhone(this, new IAuthorizeListener() {
			
			@Override
			public void success(UserInfo userInfo, int code, String info) {
				final Timer timer = new Timer();
			    timer.schedule(new TimerTask() {

			   	private int currentSecond = 60;
			   	 
					@Override
					public void run() {
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								if (currentSecond <= 0) {
									timer.cancel();
									et_alter_account.setEnabled(true);
									btn_vef_resp.setEnabled(true);
									btn_vef_resp.setBackgroundColor(Color.parseColor("#E3366F"));
									btn_vef_resp.setTextColor(-1);
									btn_vef_resp.setText("重新获取");
								} else {
									btn_vef_resp.setText((currentSecond--) + "s之后可重新获取");
								}
							}
							
						});
						
					}
			   	 
			    }, 0, 1000);
			}
			
			@Override
			public void fail(UserInfo userInfo, int code, String errorInfo) {
				btn_vef_resp.setEnabled(true);
				btn_vef_resp.setBackgroundColor(Color.parseColor("#ff1cc991"));
				toast.setText(errorInfo);
				toast.show();
			}
		}, phoneNumber);
	}
	
	
	private String vefityRefCode() {
	       Editable editable = et_vef_code.getText();
	       if((TextUtils.isEmpty(editable)) || (editable.toString().trim().equals(""))) {
	           toast.setText("验证码不能为空");
	           toast.show();
	           return null;
	       }
	       return editable.toString();
	}
	
	
	@Override
	public void onBackPressed() {
		Utils.returnRoot(this);
	}
	
	
	private UserInfo validEdit() {
		String account = et_alter_account.getText().toString();
		String oldPwd = et_alter_passwd.getText().toString();
		String newPwd = et_alter_passwd_new.getText().toString();
		
		UserInfo userInfo = new UserInfo();
		if(Utils.isEmpty(account)) {
			toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_account_empty"));
			toast.show();
			return null;
		}
		
		
		userInfo.setPhoneNumber(account);
		if(!UserInfo.VALID_OK.equals(userInfo.validForPhoneNumber(this))) {
			userInfo.setEmail(account);
			if(!UserInfo.VALID_OK.equals(userInfo.validForEmail(this))) {
				userInfo.setUserName(account);
				if(!UserInfo.VALID_OK.equals(userInfo.validForUserName(this))) {
					toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_account_cue"));
					toast.show();
					return null;
				}
			}
		}
		
		
		if(Utils.isEmpty(oldPwd)) {
			toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_oldpwd_empty"));
			toast.show();
			return null;
		}
		
		if(Utils.isEmpty(newPwd)) {
			toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_newpwd_empty"));
			toast.show();
			return null;
		}
		
		if(oldPwd.equals(newPwd)) {
			toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_oldnewpwd_consistent"));
			toast.show();
			return null;
		}
		this.newpwd = newPwd;
		userInfo.setLoginName(account);
		userInfo.setUserName("");
		userInfo.setPhoneNumber("");
		userInfo.setEmail("");
		userInfo.setPassword(oldPwd);
		return userInfo;
	}
	
	
	private boolean isDoThing = false;

	private void alter(UserInfo userInfo) {
		if(isDoThing) 
			return;
		isDoThing = true;
		Intent loadingAcIt = new Intent(this, DyLoadingActivity.class);
		loadingAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_ALTER_PASSWORD);
		loadingAcIt.putExtra(DyLoadingActivity.USERINFO, userInfo);
		loadingAcIt.putExtra(NEW_PASSWORD, newpwd);
		startActivity(loadingAcIt);
		finish();
	}
}
