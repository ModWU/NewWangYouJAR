package cg.yunbee.cn.wangyoujar.work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import cg.yunbee.cn.wangyou.loginSys.LoginRecords;
import cg.yunbee.cn.wangyou.loginSys.UserToken;
import cg.yunbee.cn.wangyoujar.Yunbee;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.feeInfo.InitFeeInfo;
import cg.yunbee.cn.wangyoujar.feeInfo.PayItem;
import cg.yunbee.cn.wangyoujar.feeInfo.PayWay;
import cg.yunbee.cn.wangyoujar.pay.Pay;
import cg.yunbee.cn.wangyoujar.pay.PayBuilder;
import cg.yunbee.cn.wangyoujar.pay.PayType;
import cg.yunbee.cn.wangyoujar.pay.QRcodePay;
import cg.yunbee.cn.wangyoujar.pay.SDKPay;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkhelper.SPayBuilder;
import cg.yunbee.cn.wangyoujar.utils.DialogUtils;
import cg.yunbee.cn.wangyoujar.utils.HttpUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cn.yunbee.cn.wangyoujar.joggle.IPayCallBack;
import cn.yunbee.cn.wangyoujar.joggle.IQueryListener;
import dy.compatibility.work.Contant;


public class PayManager implements Runnable {
	public Activity mActivity;
	/**
	 * cp传入的参数
	 */
	public  String param;
	public  IPayCallBack payCallBack;
	/**
	 * 所有可用的支付方式
	 */
	public Map<String, PayWay> payWayMap = new HashMap<String, PayWay>();
	/**
	 * 典游每次支付的订单号
	 */
	public  String data;
	
	/**
	 * 每次支付的缓冲区
	 */
	public final static PayItem mPayItem = new PayItem();
	
	private SDKPay mSDKPay;
	
	public static Dialog dialog;
	
	/**
	 * 支付
	 */
	private Pay pay;
	
	private YunbeeVice yunbeeVice;
	
	private Toast toast;
	
	public void setToast(Toast toast) {
		this.toast = toast;
	}
	
	public static final int UPLOAD_CHARGE_INFO_SUCCESS = 0;
	public static final int UPLOAD_PAY_PARAM_SUCCESS = 2;
	public static final int UPLOAD_PAY_PARAM_FAIL = 3;	
	public static final int UPLOAD_PAY_NO_PROPID = 4;
	public static final int UPLOAD_CHARGE_INFO_FAIL = -1;
	public static final int UPLOAD_PAY_TUNNEL_ERROR = -2;
	
	void payWithUploadParam(Activity activity, String payType) {
		final String tmpPayType = payType;
		//final Activity payActivity = DianPayActivity.dianPayActivity == null ? activity : DianPayActivity.dianPayActivity;
		/*Activity payActivity = DianPayActivity.dianPayActivity == null ? activity : DianPayActivity.dianPayActivity;
		Dialog dialog = DialogUtils.getSimpleRotateDialog(payActivity, false, 12, "请稍后", Utils.getResourceId(payActivity,
				"drawable", "dianyou_gray_rotate_icon"));*/
		final Activity payActivity = activity;
		this.mActivity = payActivity;
		pay.setActivity(activity);
		
		int icon_id = SPayBuilder.build(payActivity, tmpPayType).getIconId();
				
		dialog = DialogUtils.getDianyouPayDialog(payActivity, icon_id);
		DialogUtils.showDialog(payActivity, dialog);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				TAGS.log("------------------------------uploadPayParam------------------------");
				String uri = Contant.HOST + "/Pay/ClientParam.ashx";
				String dianid = "", token = "";
				UserToken userToken = LoginRecords.getInstance(payActivity).getUserToken();
				if(userToken != null) {
					dianid = userToken.getUserId();
					token = userToken.getUserToken();
				}
				InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
				Map<String, String> uploadParams = new HashMap<String, String>();
				uploadParams.put("id", initFeeInfo.getId());
				uploadParams.put("param", param);
				uploadParams.put("pay_id", mPayItem.getPay_id());
				uploadParams.put("explain", YunbeeVice.gameName);
				uploadParams.put("dianid", dianid);
				uploadParams.put("token", token);
				TAGS.log("uploadPayParam->params:" + uploadParams.toString());
				
				String result = HttpUtils.sendPostUTF8(uri, uploadParams);
				TAGS.log("uploadPayParam->result:" + result);
				try {
					JSONObject resultObj = new JSONObject(result);
					int code = resultObj.getInt("code");
					if(code == 0) {
						//成功
						Bundle data2 = new Bundle();
						data = mPayItem.getPay_id();/*Util.md5(param + System.currentTimeMillis())
								.substring(1, 11);*/
						pay.setData(data);
						TAGS.log("订单号为: " + data);
						data2.putString("payType", tmpPayType);
						Message msg = new Message();
						
						boolean isSuccess = true;
						if ("QRCodepay".equals(tmpPayType)) 
							isSuccess = obtainQRCodeInfo(initFeeInfo.getId(), dianid, token);
						
						if(isSuccess) {
							msg.setData(data2);
							msg.what = UPLOAD_PAY_PARAM_SUCCESS;
							yunbeeVice.handler.sendMessage(msg);
							return;
						}
						
					} else if(code == -10) {
						ServerCommunicationManager.return_code_10(mActivity, dialog);
						return;
					}
				} catch (Exception e) {
					TAGS.log("uploadPayParam->ex2:" + e.toString());
				}
				
				yunbeeVice.handler.sendEmptyMessage(UPLOAD_PAY_PARAM_FAIL);
			}
			
		}).start();
	}

	//获取二维码地址信息
	private boolean obtainQRCodeInfo(String id, String dianid, String token) {
		TAGS.log("--------------obtainQRCodeInfo----------------");
		String uri = Contant.HOST + "/Pay/GetWeixinScan.ashx";
		Map<String, String> uploadParams = new HashMap<String, String>();
		uploadParams.put("id", id);
		uploadParams.put("pay_id", mPayItem.getPay_id());
		uploadParams.put("dianid", dianid);
		uploadParams.put("token", token);
		String result = HttpUtils.sendPostUTF8(uri, uploadParams);
		try {
			JSONObject resultObj = new JSONObject(result);
			int code = resultObj.getInt("code");
			if(code == 0) {
				//成功
				JSONObject dataObj = resultObj.getJSONObject("data");
				String scanUrl = dataObj.getString("scanurl");
				mPayItem.setScanUrl(scanUrl);
				return true;
			} else if(code == -10) {
				ServerCommunicationManager.return_code_10(mActivity, dialog);
			}
		} catch (Exception e) {
			TAGS.log("obtainQRCodeInfo->ex2:" + e.toString());
		}
		return false;
	}

	public void clean() {
		TAGS.log("PayManager->clean->mActivity: " + mActivity);
		TAGS.log("PayManager->clean->dialog: " + dialog);
		DialogUtils.closeDialog(mActivity, dialog);
		payWayMap.clear();
		mActivity = null;
		param = null;
		payCallBack = null;
		data = null;
		mPayItem.clear();
		dialog = null;
	}

	public PayManager(Activity activity, YunbeeVice yunbeeVice, String propId, String propName, String propPrice, String param,
			IPayCallBack payCallBack) {
		super();
		this.mActivity = activity;
		this.yunbeeVice = yunbeeVice;
		this.param = param;
		this.payCallBack = payCallBack;
		
		mPayItem.setProp_id(propId);
		mPayItem.setPropName(propName);
		mPayItem.setPrice((int)(double)Double.valueOf(propPrice));
		pay = new Pay(activity, propId, propName, propPrice, param, payCallBack);
		TAGS.log("-----------------------------------------------");
		TAGS.log("PayManager构造函数->propId为: " + mPayItem.getProp_id());
		TAGS.log("PayManager构造函数->propName为: " + mPayItem.getPropName());
		TAGS.log("PayManager构造函数->propPrice为: " + mPayItem.getPrice());
	}
	
	private boolean validUid(String uid) {
		if(Util.isEmpty(uid))
			uid = ServerCommunicationManager.getUidFromPhone(mActivity);
			
		if(Util.isEmpty(uid)) {
			//再次获取uid
			uid = ServerCommunicationManager.initUploadMobilePhoneInfo(mActivity);
			if(!Util.isEmpty(uid)) {
				boolean isNeedLogin = InitFeeInfo.getInstance().isOAuthState();
				if(isNeedLogin) {
					if(Yunbee.getInstance(mActivity).yunbeeVice != null)
						Yunbee.getInstance(mActivity).yunbeeVice.payLogin(mActivity);
					return false;
				} else {
					boolean isSuccess = ServerCommunicationManager.initVefifyId(mActivity, isNeedLogin, uid);
					return isSuccess;
				}
			} else {
				TAGS.log("payManager-->没有获取到uid！初始化失败，请检查网络或重新初始化!");
				Util.showToastAtMainThread(toast, "初始化失败，请检查网络或重新初始化!");
				return false;
			}
		}
		if(uid == null) 
			uid = "";
		InitFeeInfo.getInstance().setId(uid);
		return true;
	}

	@Override
	public  void run() {
		
			String uri = Contant.HOST + "/Pay/GetPayWay.ashx";
				
			InitFeeInfo initFeeInfo = InitFeeInfo.getInstance();
			String uid = initFeeInfo.getId();
			if(!validUid(uid)) {
				if(yunbeeVice != null)
					yunbeeVice.handler.sendEmptyMessage(-1);
				return;
			} else uid = initFeeInfo.getId();
			
			/*if(!SecretData.getInstance().isHasPropId(mPayItem.getProp_id())) {
				//关闭对话框
				if(yunbeeVice != null)
					yunbeeVice.handler.sendEmptyMessage(UPLOAD_PAY_NO_PROPID);
				return;
			}*/
			
			String dianid = "", token = "";
			UserToken userToken = LoginRecords.getInstance(mActivity).getUserToken();
			if(userToken != null) {
				dianid = userToken.getUserId();
				token = userToken.getUserToken();
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", initFeeInfo.getId());
			params.put("prop_id", mPayItem.getProp_id() + "");
			float price = (float) (Math.floor(Float.valueOf(mPayItem.getPrice()))/100);
			params.put("price",  price + "");
			params.put("propName", mPayItem.getPropName());
			params.put("dianid", dianid);
			params.put("token", token);
			
				
			TAGS.log("PayManager->GetPayWay->params: " + params.toString());
			
			String result = HttpUtils.sendPostUTF8(uri, params);
				
			TAGS.log("PayManager->GetPayWay->result: " + result);
				
			LinkedList<PayWay> allPayWay = new LinkedList<PayWay>();
				
			try {
				JSONObject rstJsb = new JSONObject(result);
				int code = rstJsb.getInt("code");
				if(code == 0) {
					String pay_id = rstJsb.optString("pay_id");
					String priceType = rstJsb.optString("priceType", "1");
					String _price = rstJsb.optString("price");
					String _propName = rstJsb.optString("propName");
					String aibeiId = rstJsb.optString("aiBeiId", "");
					
					int penny = (int) (Double.valueOf(_price) * 100);
					
					mPayItem.setPrice(penny);
					mPayItem.setPropName(_propName);
					mPayItem.setPay_id(pay_id);
					mPayItem.setAibei_id(aibeiId);
					//固定价格
					if(priceType.equals("1")) {
						TAGS.log("PayManager->普通价格");
					//传入型价格
					} else {
						TAGS.log("PayManager->传入型价格");
					}
					
					TAGS.log("PayManager->propId: " + mPayItem.getProp_id());
					TAGS.log("PayManager->aibeiId: " + mPayItem.getAibei_id());
					TAGS.log("PayManager->propName: " + mPayItem.getPropName());
					TAGS.log("PayManager->propPrice: " + mPayItem.getPrice());
					TAGS.log("PayManager->pay_id: " + pay_id);
					
					
					
					JSONArray paywayArray = rstJsb.optJSONArray("pay_way");
					final int count = paywayArray.length();
					if(count > 0) {
						for(int i = 0; i < count; i++) {
							PayWay payWay = new PayWay();
							JSONObject perWay = paywayArray.getJSONObject(i);
							String name = perWay.getString("name");
							String discount = perWay.getString("discount");
							String order = perWay.getString("order");
							payWay.setName(name);
							payWay.setDiscount(Float.valueOf(discount));
							payWay.setOrder(Integer.valueOf(order));
							allPayWay.addLast(payWay);
						}
					}
				} else if(code == -10) {
					//关闭对话框
					ServerCommunicationManager.return_code_10(mActivity, dialog);
					return;
				} else {
					String message = rstJsb.getString("message");
					Util.showToastAtMainThread(toast, message);
					return;
				}
			} catch (Exception e) {
				TAGS.log("PayManager-->run->ex2:" + e.toString());
				Util.showToastAtMainThread(toast, "支付异常或网络错误");
				return;
			}
				
			//TAGS.log("sdkNames:" + initFeeInfo.getListSdkName());
			//List<String> allLocalSdkName = new ArrayList<String>(initFeeInfo.getListSdkName());
			//特殊的情况
			/*if(allLocalSdkName.contains("yhxf")) {
				allLocalSdkName.remove("yhxf");
				allLocalSdkName.add("yhxf_weixin");
				allLocalSdkName.add("yhxf_alipay");
			}
			
			if(allLocalSdkName.contains("wft")) {
				allLocalSdkName.remove("wft");
				allLocalSdkName.add("wft_weixin");
				allLocalSdkName.add("wft_alipay");
				allLocalSdkName.add("wft_qq");
			}*/
				
			for(int i = allPayWay.size() - 1; i >= 0; i--) {
				PayWay payWay = allPayWay.get(i);
				/*if(!allLocalSdkName.contains(payWay.getName())) {
					allPayWay.remove(i);
					continue;
				}*/
					
				if(payWay.getName().equals("aibei")) {
					if(mPayItem.getPrice() < 100) {
						allPayWay.remove(i);
					}
				}
					
			}
				
			payWayMap.clear();
				
			for(PayWay pw : allPayWay) {
				payWayMap.put(pw.getName(), pw);
			}
			
			//杰莘宏业暂时没用
			payWayMap.remove("jxhy");
			
			//关闭对话框
			if(payWayMap.isEmpty()) {
				// 下发的通道本地都没配置，也不能支付
				TAGS.log("下发的通道本地都没配置，不能支付");
				payAbort();
				Util.showToastAtMainThread(toast, "没有可用的支付方式!");
			} else {
				//有付费通道
				//传参数信息
				TAGS.log("----------------run propId:" + mPayItem.getProp_id());
				pay.setPropName(mPayItem.getPropName());
				uploadChargeInfo();
			}
		
		
		
	}
	
	

	public List<PayWay> getPayWayList() {
		return new ArrayList<PayWay>(payWayMap.values());
	}


	private void uploadChargeInfo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (param == null || param.equals("")) {
					param = "null";
				}
				/*String res = ServerCommunicationManager.uploadChargeInfo(param,
						orderId);
				if ("success".equals(res)) {
					handler.sendEmptyMessage(0);
				} else {
					handler.sendEmptyMessage(-1);
				}*/
				yunbeeVice.handler.sendEmptyMessage(0);
				
			}

		}).start();
	}

	private void startDianPay() {
		TAGS.log("上传充值信息成功，启动点支付");
		DianPayActivity.propertyId = String.valueOf(mPayItem.getProp_id());
		Intent intent = new Intent(mActivity, DianPayActivity.class);
		TAGS.log("替换外部activity加载器");
		//这里必须加这句话(低版本时，本类加载器和参数activity的类加载器没有联系)
		Util.replaceClassLoader_api17(getClass().getClassLoader(), mActivity);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivity.startActivity(intent);
	}
	
	private void startTransPay(String sdkName) {
		TAGS.log("--------------startTransPay-----------------");
		TAGS.log("sdkName: " + sdkName);
		Intent intent = new Intent(mActivity, TranspayProxyActivity.class);
		intent.putExtra(TranspayProxyActivity.ENTER, true);
		intent.putExtra(TranspayProxyActivity.SDK_NAME, sdkName);
		Util.replaceClassLoader_api17(getClass().getClassLoader(), mActivity);
		mActivity.startActivity(intent);
	}

	private void chooseTypeToPay(String payType) {
		TAGS.log("-----------------chooseTypeToPay-----------------");
		TAGS.log("上传参数信息成功，开始支付。。。");
		resetPayFlag();
		TAGS.log("支付价格: " + mPayItem.getPrice() + "分");
		double price_r = payWayMap.get(payType).getDiscount() * mPayItem.getPrice();
		TAGS.log("price_r: " + price_r + "分");
		double price_d = Math.floor(price_r);
		TAGS.log("price_d: " + price_d + "分");
		if(price_d < 1) {
			TAGS.log("打折后的实际价格小于0.01元，该道具按0.01元计算");
			price_d = 1;
		}
		String realPrice = String.valueOf((int)price_d);
		TAGS.log("打折后的实际支付价格: " + realPrice + "分");
		pay.setPrice(realPrice);
		
		mPayItem.setPayType(payType);
		
		//设置支付参数
		pay.setPayId(mPayItem.getPay_id());
		pay.setPayType(payType);
		
		mSDKPay = PayBuilder.build(pay).buildPay();
		handleSpecPay(payType);
		mSDKPay.pay();
		
	}

	private void handleSpecPay(String payType) {
		if(mSDKPay != null && PayType.TYPE_QRCODE.equals(payType)) {
			QRcodePay qrcodePay = (QRcodePay) mSDKPay;
			qrcodePay.setScanUrl(mPayItem.getScanUrl());
		}
		
	}

	private void resetPayFlag() {
		if(payWayMap.size() <= 1) {
			Yunbee.isPaying = false;
			YunbeeVice.doPayFlag = false;
		}
		
	}
	
	

	public void payAbort() {
		if(mSDKPay != null)
			mSDKPay.payAbort(data);
	}

	public  void payCancel() {
		if(mSDKPay != null)
			mSDKPay.payCancel(data);
	}

	public  void paySuccess() {
		if(mSDKPay != null)
			mSDKPay.paySuccess(data);
	}
	
	public void awakeCallback() {
		if(payCallBack != null)
			payCallBack.onSuccess(data);
	}

	public static String getUID(Context context) {
		String uid = InitFeeInfo.getInstance().getId();
		TAGS.log("游戏获取的uid:" + uid);
		return uid;
	}
	
	
	private boolean isHasTranspaySdk(String sdkName) {
		if(sdkName == null) return false;
		if(sdkName.contains("wft")) {
			return true;
		}
		return false;
	}
	

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case UPLOAD_CHARGE_INFO_SUCCESS: {
			if(payWayMap.size() == 1) {
				Set<String> nameKey = payWayMap.keySet();
				String sdkName = nameKey.iterator().next();
				if(isHasTranspaySdk(sdkName)) {
					startTransPay(sdkName);
				} else {
					payWithUploadParam(mActivity, sdkName);
				}
			} else if(payWayMap.size() > 1) {
				startDianPay();
			} else {
				showToast(mActivity, "没有通道可用");
			}
			break;
		}
		case UPLOAD_CHARGE_INFO_FAIL: {
			payAbort();
			break;
		}
		case UPLOAD_PAY_PARAM_SUCCESS: {
			Bundle data = msg.getData();
			String payType = data.getString("payType");
			chooseTypeToPay(payType);
			break;
		}
		
		case UPLOAD_PAY_PARAM_FAIL: {
			payAbort();
			TAGS.log("上传参数信息失败，参数{id, param, explain}");
			showToast(mActivity, "上传参数错误，支付失败");
			break;
		}
		
		case UPLOAD_PAY_NO_PROPID: {
			payAbort();
			TAGS.log("DYConfig文件中没有配置这样的道具id: " + mPayItem.getProp_id());
			showToast(mActivity, "没有配置这样的道具id");
			break;
		}
		
		case UPLOAD_PAY_TUNNEL_ERROR: {
			payAbort();
			TAGS.log("通道错误(服务器上支付类型的名字配错了?)");
			showToast(mActivity, "没有通道可用");
			break;
		}
		
		}
	}
	
	public void showToast(Context context, String msg) {
		if(toast != null) {
			toast.setText(msg);
			toast.show();
		} else {
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}
	
	public static String decodeInfo(String str) {
		
		List<Integer> interList = new ArrayList<Integer>();
		
		while(str.length() > 0) {
			int t = str.charAt(0) << 16;
			int t2 = str.charAt(1) << 8;
			int t3 = str.charAt(2);
			
			int value = t | t2 | t3;
			
			interList.add(value);
			
			str = str.substring(3);
		}
		
		int size = interList.size();
		
		int[] shifts = new int[size];
		int[] randoms = new int[size];
		Random random = new Random(size);
		for(int i = 0; i < size; i++) {
			shifts[i] = Math.abs(random.nextInt(16));
			randoms[i] = Math.abs(random.nextInt(Integer.MAX_VALUE));
		}
		
		int high_1 = 0;
		
		char[] buffer = new char[size];
		
		for(int i = size - 1; i >= 0; i--) {
			int d = interList.get(i);
			d ^= ~0 & (0xff << 4) * -1;
			d &= (1 << 31) - 1;
			d ^= randoms[i];
			d = (i == str.length() - 1) ? d : d ^ high_1;
			d >>= shifts[i];
			char c = (char) d;
			buffer[i] = c;
			high_1 = d;
		}
		return new String(buffer, 0 , size);
	}

	public void pc_query(Activity activity, IQueryListener listener, boolean isCancel[]) {
		ServerCommunicationManager.addQuery(activity, listener, mPayItem.getPay_id(), 0 , 0, isCancel);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		TAGS.log("-------------------onActivityResult-----------------------");
		String payType = mPayItem.getPayType();
		
		if(isHasTranspaySdk(payType)) {
			//威富通
			if(data == null || pay == null) return;
			
			String respCode = data.getExtras().getString("resultCode");
			TAGS.log("respCode: " + respCode);
			if(!TextUtils.isEmpty(respCode)) {
				TAGS.log("not empty");
				if("success".equalsIgnoreCase(respCode)) {
					paySuccess();
				} else if("notpay".equalsIgnoreCase(respCode)) {
					payCancel();
				} else {
					payAbort();
				}
			} else {
				TAGS.log("isEmpty");
				payAbort();
			}
			
		}
	}
	

}
