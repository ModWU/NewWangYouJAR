package cg.yunbee.cn.wangyou.loginSys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;

public class DyRegisterActivity extends Activity {
	
	private TextView registerBackToLogin, register;
	
	private EditText et_register_passwd,  et_register_passwd_confirm;
	
	private EditText et_register_account, et_register_phone, et_register_email;
	
	private TextView register_passwd_eye_1, register_passwd_eye_2;
	
	private TextView titleTv;
	
	private Toast toast;
	
	public static Activity activity;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ResourceUtils.getLayoutId(this, "dy_base_layout_register"));
		Utils.setFinishOnTouchOutside(this, false);
		initData();
		activity = this;
	}
	
	@Override
	protected void onDestroy() {
		if(activity == this)
			activity = null;
		super.onDestroy();
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
		registerBackToLogin = (TextView) findViewById(ResourceUtils.getId(this, "registerBackToLogin"));
		register = (TextView) findViewById(ResourceUtils.getId(this, "register"));
		et_register_account = (EditText) findViewById(ResourceUtils.getId(this, "et_register_account"));
		et_register_phone = (EditText) findViewById(ResourceUtils.getId(this, "et_register_phone"));
		et_register_email = (EditText) findViewById(ResourceUtils.getId(this, "et_register_email"));
		et_register_passwd = (EditText) findViewById(ResourceUtils.getId(this, "et_register_passwd"));
		et_register_passwd_confirm = (EditText) findViewById(ResourceUtils.getId(this, "et_register_passwd_confirm"));
		register_passwd_eye_1 = (TextView) findViewById(ResourceUtils.getId(this, "register_passwd_eye_1"));
		register_passwd_eye_2 = (TextView) findViewById(ResourceUtils.getId(this, "register_passwd_eye_2"));
		
		toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserInfo userInfo = validEdit();
				//зЂВс
				if(userInfo != null)
					register(userInfo);
			}
			
		});
		
		registerBackToLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.returnRoot(DyRegisterActivity.this);
			}
		});
		
		
		register_passwd_eye_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(register_passwd_eye_1.isSelected()) {
					Utils.setPwdVisible(et_register_passwd, false);
					register_passwd_eye_1.setBackgroundResource(ResourceUtils.getDrawableId(DyRegisterActivity.this, "dy_eye_close"));
					register_passwd_eye_1.setSelected(false);
				} else {
					Utils.setPwdVisible(et_register_passwd, true);
					register_passwd_eye_1.setBackgroundResource(ResourceUtils.getDrawableId(DyRegisterActivity.this, "dy_eye_open"));
					register_passwd_eye_1.setSelected(true);
				}
				
				Utils.setCursorToLast(et_register_passwd);
				
			}
			
		});
		
		register_passwd_eye_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(register_passwd_eye_2.isSelected()) {
					Utils.setPwdVisible(et_register_passwd_confirm, false);
					Utils.setCursorToLast(et_register_passwd_confirm);
					register_passwd_eye_2.setBackgroundResource(ResourceUtils.getDrawableId(DyRegisterActivity.this, "dy_eye_close"));
					register_passwd_eye_2.setSelected(false);
				} else {
					Utils.setPwdVisible(et_register_passwd_confirm, true);
					Utils.setCursorToLast(et_register_passwd_confirm);
					register_passwd_eye_2.setBackgroundResource(ResourceUtils.getDrawableId(DyRegisterActivity.this, "dy_eye_open"));
					register_passwd_eye_2.setSelected(true);
				}
				
				Utils.setCursorToLast(et_register_passwd_confirm);
			}
			
		});
		
	}
	
	
	protected UserInfo validEdit() {
		String username = et_register_account.getText().toString();
		String password = et_register_passwd.getText().toString();
		String passwordConfirm = et_register_passwd_confirm.getText().toString();
		String phoneNumber = et_register_phone.getText().toString();
		String email = et_register_email.getText().toString();
		
		UserInfo userInfo = new UserInfo();
		userInfo.setUserName(username);
		userInfo.setPassword(password);
		userInfo.setPhoneNumber(phoneNumber);
		userInfo.setEmail(email);
		
		String uNameStr = userInfo.validForUserName(this);
		if(!UserInfo.VALID_OK.equals(uNameStr)) {
			toast.setText(uNameStr);
			toast.show();
			return null;
		}
		
		boolean isEmptyForPE = Utils.isEmpty(phoneNumber) && Utils.isEmpty(email);
		if(isEmptyForPE) {
			toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_pechoice_one"));
			toast.show();
			return null;
		} else {
			boolean isPhoneEmpty = Utils.isEmpty(phoneNumber);
			if(!isPhoneEmpty) {
				String uPhoneStr = userInfo.validForPhoneNumber(this);
				if(!UserInfo.VALID_OK.equals(uPhoneStr)) {
					toast.setText(uPhoneStr);
					toast.show();
					return null;
				}
			}
			
			boolean isEmailEmpty = Utils.isEmpty(email);
			if(!isEmailEmpty) {
				String uEmailStr = userInfo.validForEmail(this);
				if(!UserInfo.VALID_OK.equals(uEmailStr)) {
					toast.setText(uEmailStr);
					toast.show();
					return null;
				}
			}
		}
		
		
		
		String uPwdStr = userInfo.validForPassword(this);
		if(!UserInfo.VALID_OK.equals(uPwdStr)) {
			toast.setText(uPwdStr);
			toast.show();
			return null;
		} else {
			boolean isPwdConfirmEmpty = Utils.isEmpty(passwordConfirm);
			if(isPwdConfirmEmpty) {
				toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_conpwd_empty"));
				toast.show();
				return null;
			} else if(!password.equals(passwordConfirm)) {
				toast.setText(ResourceUtils.getStringId(this, "dy_rmreg_twopwd_consistent"));
				toast.show();
				return null;
			}
		}
		
		return userInfo;
	}

	private boolean isDoThing = false;
	
	private void register(UserInfo userInfo) {
		if(isDoThing) return;
		isDoThing = true;
		Intent loadingAcIt = new Intent(this, DyLoadingActivity.class);
		loadingAcIt.putExtra(DyLoadingActivity.TYPE, DyLoadingActivity.TYPE_REGISTER);
		loadingAcIt.putExtra(DyLoadingActivity.USERINFO, userInfo);
		startActivity(loadingAcIt);
		finish();
	}
	
	
	@Override
	public void onBackPressed() {
		Utils.returnRoot(this);
	}
}
