package cg.yunbee.cn.wangyou.loginSys;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;

public class DyLogoutActivity extends Activity implements OnClickListener{
	
	private TextView dialogCancel, dialogConfirm;
	
	private int dialogCancelId, dialogConfirmId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ResourceUtils.getLayoutId(this, "dy_base_layout_logout"));
		Utils.setFinishOnTouchOutside(this, false);
		
		dialogCancelId = ResourceUtils.getId(this, "dialog_cancel");
		dialogConfirmId = ResourceUtils.getId(this, "dialog_confirm");
		dialogCancel = (TextView) findViewById(dialogCancelId);
		dialogConfirm = (TextView) findViewById(dialogConfirmId);
		dialogCancel.setOnClickListener(this);
		dialogConfirm.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		if(v.getId() == dialogConfirmId) {
			confirm();
		} else {
			cancel();
		}
	}
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		LogoutProvider.isDoThing = false;
	}
	
	
	private void cancel() {
		finish();
		LogoutProvider.isDoThing = false;
	}
	
	private void confirm() {
		LogoutProvider.doLogout(this);
		finish();
		LogoutProvider.isDoThing = false;
	}


}
