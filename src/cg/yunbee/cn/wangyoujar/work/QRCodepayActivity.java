package cg.yunbee.cn.wangyoujar.work;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;
import cn.yunbee.cn.wangyoujar.joggle.IQueryListener;

public class QRCodepayActivity extends Activity {
	public static final String QR_PATH = "QRIMG_PATH";
	public static final String PAY_ID = "PAY_ID";
	
	private ImageView qrImg, descImg;
	private TextView propNameTv;
	private TextView propPriceTv;
	
	private ViewGroup parentLay1, parentLay2;
	
	
	private String qrfilepath;
	private String payId = "";
	
	
	private int mScreenW, mScreenH;
	
	private boolean isHeightBig = false;
	
	private volatile boolean isCancel[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ResourceUtils.getLayoutId(this, "dian_pay_qrcode_activity"));
		TAGS.log("--------------QRCodepayActivity-->onCreate------------");
		obtainScreenInfo();
		initViews();
		isCancel = new boolean[1];
		obtainData();
		TAGS.log("payId: " + payId);
		TAGS.log("qrfilepath: " + qrfilepath);
		showQRScanCode();
	}
	
	
	private void obtainScreenInfo() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mScreenW = metrics.widthPixels;
		mScreenH = metrics.heightPixels;
		if (mScreenH >= mScreenW) 
			isHeightBig = true;
		else 
			isHeightBig = false;
		
		TAGS.log("isHeightBig: " + isHeightBig);
	}


	private void showQRScanCode() {
		qrImg.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(qrfilepath)));
		descImg.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), ResourceUtils.getDrawableId(QRCodepayActivity.this, "dyqrdesctxt"))));
		if(YunbeeVice.payManager != null) {
			YunbeeVice.payManager.pc_query(QRCodepayActivity.this, new IQueryListener() {
				
				@Override
				public void onQuerySuccess() {
					if(!QRCodepayActivity.this.isFinishing())
						QRCodepayActivity.this.finish();
					if(YunbeeVice.payManager != null)
						YunbeeVice.payManager.paySuccess();
				}
				
				@Override
				public void onQueryFail() {
					if(!QRCodepayActivity.this.isFinishing()) {
						Toast.makeText(QRCodepayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
						QRCodepayActivity.this.finish();
					}
					if(YunbeeVice.payManager != null)
						YunbeeVice.payManager.payAbort();
				}

				@Override
				public void onQueryCancel() {
					if (!QRCodepayActivity.this.isFinishing()) {
						//最好会调一次，让cp查询一次客户端
						final PayManager finalPM = YunbeeVice.payManager;
						new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

							@Override
							public void run() {
								if(finalPM != null)
									finalPM.awakeCallback();
							}
								
						}, 2000);
							
						Toast.makeText(QRCodepayActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
						finish();
					}
				}

			}, isCancel);
		}
	}


	private void initViews() {
		parentLay1  = (ViewGroup) findViewById(ResourceUtils.getId(this, "qrscanLay"));
		parentLay2 = (ViewGroup) findViewById(ResourceUtils.getId(this, "qrscanLay2"));
		if(isHeightBig) {
			parentLay1.setVisibility(View.VISIBLE);
			parentLay2.setVisibility(View.GONE);
			qrImg = (ImageView) findViewById(ResourceUtils.getId(this, "idqrscanImg"));
			descImg = (ImageView) findViewById(ResourceUtils.getId(this, "descTxt"));
		} else {
			parentLay1.setVisibility(View.GONE);
			parentLay2.setVisibility(View.VISIBLE);
			qrImg = (ImageView) findViewById(ResourceUtils.getId(this, "idqrscanImg2"));
			descImg = (ImageView) findViewById(ResourceUtils.getId(this, "descTxt2"));
		}
		
		
		propNameTv = (TextView) findViewById(ResourceUtils.getId(this, "propNameValue"));
		propPriceTv = (TextView) findViewById(ResourceUtils.getId(this, "propPriceValue"));
	}


	
	public void onBack(View view) {
		onBackPressed();
	}


	private void obtainData() {
		Intent it = getIntent();
		payId = it.getStringExtra(PAY_ID);
		qrfilepath = it.getStringExtra(QR_PATH);
		
		float yuanFloat = (float) (PayManager.mPayItem.getPrice() / 100.0);
		String fee = String.valueOf(yuanFloat);
		propPriceTv.setText(fee + "元");
		propNameTv.setText(PayManager.mPayItem.getPropName());
		
	}

		
	@Override
	public void onBackPressed() {
		isCancel[0] = true;
		super.onBackPressed();
	}
	
}
	
