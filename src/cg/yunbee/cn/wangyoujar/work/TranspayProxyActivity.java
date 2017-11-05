package cg.yunbee.cn.wangyoujar.work;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import cg.yunbee.cn.wangyoujar.Yunbee;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;

public class TranspayProxyActivity extends Activity {
	
	public static final String ENTER = "dy_traspay_enter";
	public static final String SDK_NAME = "dy_traspay_sdkname";
	private String sdkName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAGS.log("TranspayProxyActivity-->onCreate：" + this.toString());
		Intent it = getIntent();
		boolean isEnter = it.getBooleanExtra(ENTER, false);
		TAGS.log("TranspayProxyActivity-->isEnter:" + isEnter);
		if(isEnter) {
			if(YunbeeVice.payManager != null) {
				sdkName = it.getStringExtra(SDK_NAME);
				YunbeeVice.payManager.payWithUploadParam(this, sdkName);
			} else {
				Toast.makeText(this, "支付出错，请重新支付！", Toast.LENGTH_SHORT).show();
				Yunbee.isPaying = false;
				YunbeeVice.doPayFlag = false;
				finish();
			}
		}
	}
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TAGS.log("--------------TranspayProxyActivity-onActivityResult----------------");
		TAGS.log("sdkName: " + sdkName);
		if(YunbeeVice.payManager != null)
			YunbeeVice.payManager.onActivityResult(requestCode, resultCode, data);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		TAGS.log("TranspayProxyActivity-->onDestroy");
		YunbeeVice.doPayFlag = false;
		super.onDestroy();
	}
}
