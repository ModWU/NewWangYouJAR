package cg.yunbee.cn.wangyoujar.pay;

import java.io.File;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.Toast;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.QRCodeUtil;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.PayManager;
import cg.yunbee.cn.wangyoujar.work.QRCodepayActivity;
import dy.compatibility.work.Utils;

public class QRcodePay extends SDKPay {
	
	private String mScanUrl = "http://www.baidu.com/";
	

	public void setScanUrl(String mScanUrl) {
		this.mScanUrl = mScanUrl;
	}

	@Override
	public QRcodePay pay() {
		final String FINAL_URL = mScanUrl;
		final String FINAL_PAY_ID = mPayId;
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				DisplayMetrics metrics = mActivity.getResources().getDisplayMetrics();
				int size = (int) (metrics.density * 250 + 0.5f);
				
				TAGS.log("二维码大小: " + size + "px");
				TAGS.log("屏幕密度: " + metrics.density);
				
				
				String path = "";
				File qrRootFile = Utils.getCachePath(mActivity);
				if (qrRootFile == null)
					path = "./qrimg";
				else
					path = qrRootFile.getAbsolutePath() + "/qrimg";
				File tmpF = new File(path);
				if (!tmpF.exists()) {
					tmpF.mkdirs();
				}
				
				String qrfilepath = path + "/qrtmp.jpg";
				
				boolean isTF = QRCodeUtil.createQRImage(FINAL_URL, size, size, BitmapFactory.decodeResource(mActivity.getResources(), 0), qrfilepath);
				if(isTF) {
					Util.replaceClassLoader_api17(getClass().getClassLoader(), mActivity);
					Intent intent = new Intent(mActivity, QRCodepayActivity.class);
					intent.putExtra(QRCodepayActivity.PAY_ID, FINAL_PAY_ID);
					intent.putExtra(QRCodepayActivity.QR_PATH, qrfilepath);
					mActivity.startActivity(intent);
				} else {
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(mActivity, "创建二维码失败，请稍后再试！", Toast.LENGTH_SHORT).show();
						}
						
					});
				}
				DialogUtils.closeDialog(mActivity, PayManager.dialog);
			}
			
		}).start();
		
		return this;
	}

}
