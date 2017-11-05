package cg.yunbee.cn.wangyoujar.work;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cg.yunbee.cn.wangyoujar.Yunbee;
import cg.yunbee.cn.wangyoujar.YunbeeVice;
import cg.yunbee.cn.wangyoujar.feeInfo.PayWay;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.sdkhelper.SPayBuilder;
import cg.yunbee.cn.wangyoujar.utils.Util;

public class DianPayActivity extends Activity {
	/** Called when the activity is first created. */
	public static Activity dianPayActivity;
	public static String propertyId;
	
	private List<PayWay> payWayList = new ArrayList<PayWay>();

	private boolean isHGreaterW = true;
	private boolean chosen = true;
	
	private Toast toast = null;
	
	public static int getResourceId(Context paramContext, String resourceType,
			String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				resourceType, paramContext.getPackageName());
	}
	
	

	private void initData() {
		TAGS.log("-------------------DianPayActivity->initData----------------------------");
		for (int i = 0; i < payWayList.size(); i++) {
			PayWay payWay = payWayList.get(i);
			TAGS.log("name: " + payWay.getName());
			SPayBuilder sPayBuidler = SPayBuilder.build(this, payWay.getName());
			payWay.setImgId(sPayBuidler.getIconId());
			payWay.setPayName(sPayBuidler.getPayName());
			payWay.setPayDetails(sPayBuidler.getPayDetail());
			payWay.setDiscountDetails(getDiscountDetails(payWay.getDiscount()));
		}
	}
	//0.01
	private static String getDiscountDetails(float discount) {
		
		if(discount == 0 || discount == 1) {
			return null;
		}
		
		float result_discount =  (float)(Math.round(discount * 100))/10;
		int int_discount = (int)result_discount;
		if(int_discount == result_discount) {
			return int_discount + "折";
		}
		
		return result_discount + "折";
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		TAGS.log("--------------DianPayActivity-onActivityResult----------------");
		if(YunbeeVice.payManager != null)
			YunbeeVice.payManager.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	/* *//**
	 * 屏幕旋转时调用此方法
	 */
	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * super.onConfigurationChanged(newConfig);
	 * //newConfig.orientation获得当前屏幕状态是横向或者竖向
	 * //Configuration.ORIENTATION_PORTRAIT 表示竖向
	 * //Configuration.ORIENTATION_LANDSCAPE 表示横屏
	 * if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
	 * Toast.makeText(DianPayActivity.this, "现在是竖屏", Toast.LENGTH_SHORT).show();
	 * } if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
	 * Toast.makeText(DianPayActivity.this, "现在是横屏", Toast.LENGTH_SHORT).show();
	 * } }
	 */

	private int screenHeight, screenWidth;

	private ViewGroup description_container1, description_container2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		

		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		screenWidth = getResources().getDisplayMetrics().widthPixels;

		try {
			
			Log.i("chao", "PayManager.getPayWayList() size:" + YunbeeVice.payManager.getPayWayList().size());
			for(PayWay pw : YunbeeVice.payManager.getPayWayList()) {
				Log.i("chao", "pay name:" + pw.getName());
			}
			
			dianPayActivity = this;
			payWayList.addAll(YunbeeVice.payManager.getPayWayList());
			Collections.sort(payWayList, new Comparator<PayWay>() {
				@Override
				public int compare(PayWay lhs, PayWay rhs) {
					return lhs.compareTo(rhs);
				}
			});
			initData();

			int layoutId = getResourceId(DianPayActivity.this, "layout",
					"dian_pay_activity");
			TAGS.log("DianPayActivity.this:"
					+ DianPayActivity.this);
			TAGS.log("layoutId" + layoutId);
			setContentView(layoutId);
			description_container1 = (ViewGroup) findViewById(getResourceId(
					DianPayActivity.this, "id", "id_description_container"));

			description_container2 = (ViewGroup) findViewById(getResourceId(
					DianPayActivity.this, "id", "id_description_container2"));
			/*if (screenHeight < screenWidth) {
				isHGreaterW = false;
				description_container1.setVisibility(View.GONE);
				description_container2.setVisibility(View.VISIBLE);
				TextView propTV = (TextView) findViewById(getResourceId(
						DianPayActivity.this, "id", "id_item_name2"));

				int fenint = Integer.valueOf(PayManager
						.getPropertyPriceByPropertyId(String
								.valueOf(YunbeeVice.payManager.propId)));
				float yuanFloat = (float) (fenint / 100.0);
				String fee = String.valueOf(yuanFloat);

				propTV.setText(PayManager.getPropertyNameByPropertyId(String
						.valueOf(YunbeeVice.payManager.propId)));
				TextView propPrice = (TextView) findViewById(getResourceId(
						DianPayActivity.this, "id", "id_item_price2"));
				propPrice.setText(fee + "元");// id_item_needPay
				TextView needPrice = (TextView) findViewById(getResourceId(
						DianPayActivity.this, "id", "id_item_needPay2"));
				needPrice.setText(fee);
			} else {*/
				isHGreaterW = true;
				description_container1.setVisibility(View.VISIBLE);
				description_container2.setVisibility(View.GONE);
				TextView propTV = (TextView) findViewById(getResourceId(
						DianPayActivity.this, "id", "id_item_name"));

				int fenint = PayManager.mPayItem.getPrice();
				float yuanFloat = (float) (fenint / 100.0);
				String fee = String.valueOf(yuanFloat);

				propTV.setText(PayManager.mPayItem.getPropName());

				TextView propPrice = (TextView) findViewById(getResourceId(
						DianPayActivity.this, "id", "id_item_price"));
				propPrice.setText(fee + "元");

				TextView needPrice = (TextView) findViewById(getResourceId(
						DianPayActivity.this, "id", "id_item_needPay"));
				needPrice.setText(fee);
			//}

			int listViewId = getResourceId(DianPayActivity.this, "id",
					"dian_pay_listview");
			ListView listView = (ListView) findViewById(listViewId);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					try {
						if (Yunbee.isPaying && isPayRunning) {
							Util.showToastAtMainThread(toast, "已经在处理中，请稍后......");
							return;
						}
						
						if(YunbeeVice.payManager == null) {
							Yunbee.isPaying = false;
							YunbeeVice.doPayFlag = false;
							Util.showToastAtMainThread(toast, "支付出错，请重新支付！");
							finish();
							return;
						}
						
						YunbeeVice.payManager.payWithUploadParam(DianPayActivity.this, payWayList.get(arg2).getName());
						isPayRunning = true;
						chosen = true;
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
					chosen = true;
					isPayRunning = false;
				}

			});
			MyAdapter adapter = new MyAdapter(this);
			listView.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static volatile boolean isPayRunning = false;

	@Override
	protected void onStop() {
		isPayRunning = false;
		super.onStop();
	}
	
	


	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return payWayList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		// ****************************************final方法
		// 注意原本getView方法中的int position变量是非final的，现在改为final
		// public View getView(final int position, View convertView,
		// ViewGroup parent) {
		// ViewHolder holder = null;
		// if (convertView == null) {
		// holder = new ViewHolder();
		// // 可以理解为从vlist获取view 之后把view返回给ListView
		//
		// convertView = mInflater.inflate(R.layout.vlist, null);
		// holder.imageView = (ImageView) findViewById(R.id.sItemIcon);
		// holder.viewBtn = (Button) convertView
		// .findViewById(R.id.view_btn);
		// convertView.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }
		//
		// holder.imageView.setImageResource(imageIdArray[position]);
		// holder.viewBtn.setText(btnNameArray[position]);
		// holder.viewBtn.setTag(position);
		// // 给Button添加单击事件 添加Button之后ListView将失去焦点 需要的直接把Button的焦点去掉
		// holder.viewBtn.setOnClickListener(new View.OnClickListener() {
		//
		// public void onClick(View v) {
		// showInfo(position);
		// }
		// });
		//
		// // holder.viewBtn.setOnClickListener(MyListener(position));
		//
		// return convertView;
		// }
		// }
		// ****************************************第二种方法，高手一般都用此种方法,具体原因，我还不清楚,有待研究

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			
			if (convertView == null) {
				holder = new ViewHolder();

				// 可以理解为从vlist获取view 之后把view返回给ListView
				
				convertView = mInflater.inflate(
						getResourceId(DianPayActivity.this, "layout",
								"dian_pay_vlist"), null);
				holder.imageView = (ImageView) convertView
						.findViewById(getResourceId(DianPayActivity.this, "id",
								"id_payway_itemIcon"));
				holder.discountTv = (TextView) convertView
						.findViewById(getResourceId(DianPayActivity.this, "id",
								"id_payway_discount"));
				holder.noGkParent = (ViewGroup) convertView
						.findViewById(getResourceId(DianPayActivity.this, "id",
								"id_notHasGk_lagout"));
				holder.hasGkParent = (ViewGroup) convertView
						.findViewById(getResourceId(DianPayActivity.this, "id",
								"id_hasGk_lagout"));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.imageView.setImageResource(payWayList.get(position).getImgId());

			// 给Button添加单击事件 添加Button之后ListView将失去焦点 需要的直接把Button的焦点去掉
			// holder.viewBtn.setOnClickListener(myListener);
			// holder.viewBtn.setText(btnName);
			String payDetails = payWayList.get(position).getPayDetails();
			if(payDetails == null || payDetails.equals("")) {
				holder.noGkParent.setVisibility(View.VISIBLE);
				holder.hasGkParent.setVisibility(View.GONE);
				
				TextView payText = (TextView) holder.noGkParent.getChildAt(0);
				payText.setText(payWayList.get(position).getPayName());
			} else {
				holder.noGkParent.setVisibility(View.GONE);
				holder.hasGkParent.setVisibility(View.VISIBLE);
				
				TextView payText = (TextView) holder.hasGkParent.getChildAt(0);
				payText.setText(payWayList.get(position).getPayName());
				
				TextView gKText = (TextView) holder.hasGkParent.getChildAt(1);
				gKText.setText(payDetails);
			}
			

			// discount[position] = 8 + "";
			String discountDetails = payWayList.get(position).getDiscountDetails();

			if (payWayList.get(position).getDiscountDetails() != null) {
				holder.discountTv.setText(discountDetails);
				holder.discountTv.setVisibility(View.VISIBLE);

			} else {
				holder.discountTv.setVisibility(View.GONE);
			}

			return convertView;
		}
	}
	
	

	@Override
	protected void onStart() {
		super.onStart();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			try {
				dianPayActivity = null;
				YunbeeVice.payManager.payCancel();
				DianPayActivity.this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		payWayList.clear();
		YunbeeVice.doPayFlag = false;
		if(this == dianPayActivity)
			dianPayActivity = null;
		super.onDestroy();
	}

	// 提取出来方便点
	public final class ViewHolder {
		public ImageView imageView;
		public TextView discountTv;
		public ViewGroup noGkParent;
		public ViewGroup hasGkParent;
	}

}