package cg.yunbee.cn.wangyoujar.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.yunbee.cn.wangyoujar.views.CornerDrawable;
import cn.yunbee.cn.wangyoujar.views.DotsRunView;
import cn.yunbee.cn.wangyoujar.views.DotsRunView2;
import cn.yunbee.cn.wangyoujar.views.RefreshRotateView;

public class DialogUtils {
	
	interface IGetDialog {
		TopDialog getDialog();
	}
	
	interface SafeMethod {
		void safeMethod();
	}
	
	public interface IDialogValueSet {
		void setIsCanBack(boolean isCanBack);
		void setBackColor(int color);
		void setBackBitmap(Bitmap bmp);
		void setTextValue(String text);
		void clear();
	}
	
	static class TopDialog extends Dialog implements IDialogValueSet {
		
		private boolean isCanBack = false;
		
		private ViewGroup realParent;
		
		private int width, height;
		
		private ViewGroup rootView;
		
		private TextView textView;
		
		private float roundDegree;
		
		private OnDismissListener listener;
		

		public TopDialog(Context context) {
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setCanceledOnTouchOutside(false);
			
			Window window = getWindow();
			rootView = (ViewGroup) window.getDecorView();
			rootView.setBackgroundResource(0);
		}
		
		public void setWidth(int width) {
			this.width = width;
		}
		
		public void setHeight(int height) {
			this.height = height;
		}
		
		public void setLayout(ViewGroup realParent) {
			rootView.removeAllViews();
			this.realParent = realParent;
			rootView.addView(realParent);
		}

		@Override
		public void setIsCanBack(boolean isCanBack) {
			this.isCanBack = isCanBack;
		}
		
		@Override
		public void onBackPressed() {
			if(isCanBack) {
				super.onBackPressed();
			}
		}
		
		@Override
		public void setOnDismissListener(OnDismissListener listener) {
			super.setOnDismissListener(listener);
			this.listener = listener;
		}
		
		private CornerDrawable handlerDrawable() {
			Drawable drawable = realParent.getBackground();
			CornerDrawable cornerDrawable = null;
			if(drawable instanceof CornerDrawable) {
				cornerDrawable = (CornerDrawable) drawable;
			}
			return cornerDrawable;
		}

		@Override
		public void setBackColor(int color) {
			if(realParent != null) {
				
				CornerDrawable cornerDrawable = handlerDrawable();
				if(cornerDrawable == null) {
					cornerDrawable = new CornerDrawable(color, width, height);
				}
				
				cornerDrawable.setRoundDegree(roundDegree);
				
				final Drawable finalDrawable = cornerDrawable;
				
				setSafeValue(new SafeMethod() {
					
					@Override
					public void safeMethod() {
						realParent.setBackgroundDrawable(finalDrawable);
						
					}
				});
			}
			
		}

		@Override
		public void setBackBitmap(Bitmap bmp) {
			if(realParent != null) {
				
				CornerDrawable cornerDrawable = handlerDrawable();
				if(cornerDrawable == null) {
					cornerDrawable = new CornerDrawable(bmp, width, height);
				}
				
				cornerDrawable.setRoundDegree(roundDegree);
				
				final Drawable finalDrawable = cornerDrawable;
				
				
				setSafeValue(new SafeMethod() {
					
					@Override
					public void safeMethod() {
						realParent.setBackgroundDrawable(finalDrawable);
					}
				});
			}
		}
		
		public void setTextView(TextView textView) {
			this.textView = textView;
		}
		
		@Override
		public void setTextValue(String text) {
			if(textView != null && text != null) {
				final String tmpText = text;
				setSafeValue(new SafeMethod() {
					@Override
					public void safeMethod() {
						ViewGroup.LayoutParams lp = textView.getLayoutParams();
						if(lp instanceof MarginLayoutParams) {
							MarginLayoutParams margin_lp = (MarginLayoutParams) lp;
							String oldText = textView.getText().toString();
							if(oldText == null) oldText = "";
							int oldWidth = (int) (textView.getPaint().measureText(oldText) + 0.5f);
							
							int newWidth = (int) (textView.getPaint().measureText(tmpText) + 0.5f);
							
							int offsetValue = (oldWidth - newWidth)/2;
							
							margin_lp.leftMargin += offsetValue;
							
							textView.setLayoutParams(margin_lp);
						}
						
						textView.setText(tmpText);
					}
				});
			}
		}

		public void setRoundDegree(float roundDegree) {
			this.roundDegree = roundDegree;
		}

		@Override
		public void clear() {
			if(listener != null)
				listener.onDismiss(this);
		}
		
	}
	
	public static void setSafeValue(final SafeMethod method) {
		
		if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
			method.safeMethod();
		} else {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					method.safeMethod();
				}
				
			});
		}
	}
	
	
	private static TopDialog buildSimpleRotateDialog(Activity activity, boolean isClockwise, float roundDegree, String title, int dotCount, int imgId) {
		
		TopDialog dialog = new TopDialog(activity);
		
		DisplayMetrics outMetrics = activity.getResources().getDisplayMetrics();
		float mDensity = outMetrics.density;
		int txtSize = 16;
		
		int topMargin = (int) (18 * mDensity);
		
		int leftMargin = (int) (18 * mDensity);
		
		int imgSize = (int) (36 * mDensity);
		
		TextView tv = new TextView(activity);
		tv.setTextColor(Color.WHITE);
		tv.setId(0X00867);
		RelativeLayout.LayoutParams txtLL = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		tv.setTextSize(txtSize);
		tv.setText(title);
		
		
		final DotsRunView2 dotTxt = new DotsRunView2(activity);
		dotTxt.setTextColor(Color.WHITE);
		int txtHeight = (int) (tv.getPaint().descent() - tv.getPaint().ascent() + 0.5f);
		int txtWidth = 0;
		
		if(dotCount > 0) {
			dotTxt.setTextSize(txtSize);
			dotTxt.setDotCount(dotCount);
			txtWidth = (int) (tv.getPaint().measureText(title) + dotTxt.measureContentTxt() + 0.5f);
		} else {
			txtWidth = (int) (tv.getPaint().measureText(title) + 0.5f);
		}
		
		int dialogWidth = Math.max(leftMargin * 2 + txtWidth, leftMargin * 2 + imgSize);
		int dialogHeight = topMargin * 3 + txtHeight + imgSize;
		
		if(dialogWidth > dialogHeight) {
			topMargin += ((dialogWidth - dialogHeight)/3.0f + 0.5f);
			dialogHeight = topMargin * 3 + txtHeight + imgSize;
		}
		int dialogSize = Math.max(dialogWidth, dialogHeight);
		
		LinearLayout parentContent = new LinearLayout(activity);
		parentContent.setOrientation(LinearLayout.VERTICAL);
		parentContent.setLayoutParams(new ViewGroup.LayoutParams(dialogSize, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		dialog.setLayout(parentContent);
		dialog.setWidth(dialogSize);
		dialog.setHeight(dialogSize);
		dialog.setRoundDegree(12);
		dialog.setBackColor(Color.parseColor("#88000000"));
		dialog.setLayout(parentContent);
		dialog.setTextView(tv);
		
		RelativeLayout titleParent = new RelativeLayout(activity);
		LinearLayout.LayoutParams title_p_ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		title_p_ll.topMargin = topMargin;
		titleParent.setLayoutParams(title_p_ll);
		
		if(dotCount > 0) {
			RelativeLayout.LayoutParams dotTxtLL = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			dotTxtLL.addRule(RelativeLayout.RIGHT_OF, 0X00867);
			dotTxt.setLayoutParams(dotTxtLL);
			titleParent.addView(dotTxt);
			
			txtLL.leftMargin = (int) ((dialogSize - tv.getPaint().measureText(title) - dotTxt.measureContentTxt())/2);
			
			dotTxt.startRun(1);
		} else {
			txtLL.addRule(RelativeLayout.CENTER_IN_PARENT);
		}
		
		
		tv.setLayoutParams(txtLL);	
		titleParent.addView(tv);
		
		parentContent.addView(titleParent);
		
		LinearLayout imgParent = new LinearLayout(activity);
		imgParent.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams imgParent_ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
		imgParent_ll.weight = 1.0f;
		imgParent.setLayoutParams(imgParent_ll);
		
		final RefreshRotateView rotateView = new RefreshRotateView(activity);
		LinearLayout.LayoutParams imgLL = new LinearLayout.LayoutParams(imgSize, imgSize);
		rotateView.setLayoutParams(imgLL);
		rotateView.setScaleType(ScaleType.FIT_XY);
		rotateView.setImageResource(imgId);
		rotateView.startRefresh(isClockwise);
		imgParent.addView(rotateView);
		
		parentContent.addView(imgParent);
		
		
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				rotateView.stopRefresh();
				dotTxt.stopRun();
			}
		});
		
		
		Log.i("INFO", "buildSimpleRotateDialog-->end:" + imgId);
		
		return dialog;
	}
	
	
	private static TopDialog getDialog(IGetDialog listener) {
		if(listener == null) {
			return null;
		}
		final TopDialog[] dialog = new TopDialog[1];
		final boolean[] flags = new boolean[1];
		final IGetDialog finalListener = listener;
		if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					synchronized (dialog) {
						dialog[0] = finalListener.getDialog();
						dialog.notify();
						flags[0] = true;
					}
					
				}
				
			});
			
			if(!flags[0]) {
				synchronized (dialog) {
					if(!flags[0]) {
						try {
							dialog.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}
			
		} else {
			dialog[0] = finalListener.getDialog();
		}
		
		
		return dialog[0];
	}
	
	public static Dialog getSimpleRotateDialog(Activity activity, boolean isClockwise, float degree, String title, int imgId) {
		
		if(activity == null || activity.isFinishing()) return null;
		
		final Activity finalActivity = activity;
		final String finalTitle = title;
		final int finalImgId = imgId;
		final float finalDegree = degree;
		final boolean finalIsClockwise = isClockwise;
		
		IGetDialog getDialogListener = new IGetDialog() {
			@Override
			public TopDialog getDialog() {
				return buildSimpleRotateDialog(finalActivity, finalIsClockwise, finalDegree, finalTitle, -1, finalImgId);
			}
		};
		
		return getDialog(getDialogListener);
	}
	
	public static Dialog getSimpleRotateDialog_dotRun(Activity activity, boolean isClockwise, float degree, String title, int imgId) {
		
		if(activity == null || activity.isFinishing()) return null;
		
		final Activity finalActivity = activity;
		final String finalTitle = title;
		final int finalImgId = imgId;
		final float finalDegree = degree;
		final boolean finalIsClockwise = isClockwise;
		
		IGetDialog getDialogListener = new IGetDialog() {
			@Override
			public TopDialog getDialog() {
				return buildSimpleRotateDialog(finalActivity, finalIsClockwise, finalDegree, finalTitle, 4, finalImgId);
			}
		};
		
		return getDialog(getDialogListener);
	}
	
	
	private static TopDialog buildDianyouPayDialog(Activity activity, int imgId) {
		TopDialog dialog = new TopDialog(activity);
		DisplayMetrics outMetrics = activity.getResources().getDisplayMetrics();
		float yDensity = outMetrics.ydpi / 160f;
		int tmMargin = (int) (18 * yDensity);
		Bitmap srcBmp = BitmapFactory.decodeResource(activity.getResources(), imgId);
		
		int dialogSize = (int) (srcBmp.getHeight() + DotsRunView.DEFAULT_DOT_SIZE * yDensity + 3 * tmMargin + 0.5f);
		
		LinearLayout parentContent = new LinearLayout(activity);
		parentContent.setOrientation(LinearLayout.VERTICAL);
		parentContent.setGravity(Gravity.CENTER);
		ViewGroup.LayoutParams parent_ll = new ViewGroup.LayoutParams(dialogSize, dialogSize);
		parentContent.setLayoutParams(parent_ll);
		
		
		dialog.setLayout(parentContent);
		dialog.setWidth(dialogSize);
		dialog.setHeight(dialogSize);
		dialog.setRoundDegree(12);
		dialog.setBackColor(Color.parseColor("#88000000"));
		
		dialog.setLayout(parentContent);
		
		
		ImageView iconIV = new ImageView(activity);
		LinearLayout.LayoutParams iconIv_ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		iconIv_ll.topMargin = tmMargin;
		iconIV.setLayoutParams(iconIv_ll);
		iconIV.setImageResource(imgId);
		
		parentContent.addView(iconIV);
		
		final DotsRunView dotsRunView = new DotsRunView(activity);
		LinearLayout.LayoutParams dot_parent_ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		dot_parent_ll.topMargin = tmMargin;
		dot_parent_ll.bottomMargin = tmMargin;
		dotsRunView.setLayoutParams(dot_parent_ll);
		dotsRunView.setUnSelectedColor(Color.GRAY);
		dotsRunView.setSelectedColor(Color.WHITE);
		dotsRunView.setDotMargin(8);
		dotsRunView.startRun(1);
		
		parentContent.addView(dotsRunView);
		
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				dotsRunView.stopRun();
			}
		});
		
		return dialog;
	}
	
	public static TopDialog getDianyouPayDialog(Activity activity, int imgId) {
		
		if(activity == null || activity.isFinishing()) return null;
		
		final Activity finalActivity = activity;
		final int finalImgId = imgId;
		IGetDialog getDialogListener = new IGetDialog() {
			@Override
			public TopDialog getDialog() {
				return buildDianyouPayDialog(finalActivity, finalImgId);
			}
		};
		
		return getDialog(getDialogListener);
		
	}
	
	public static void showDialog(Activity activity, Dialog dialog) {
		if(dialog == null || dialog.isShowing() || activity.isFinishing()) return;
		final Dialog finalDialog = dialog;
		if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
			Log.i("INFO", "showDialog1-->end");
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					finalDialog.show();
					Log.i("INFO", "showDialog2-->end");
				}
				
			});
		} else {
			Log.i("INFO", "showDialog3-->end");
			finalDialog.show();
		}
	}
	
	public static void closeDialog(Activity activity, Dialog dialog) {
		if(dialog == null || activity == null) return;
		final Dialog finalDialog = dialog;
		final Activity finalAct = activity;
		if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					if(finalDialog.isShowing() && !finalAct.isFinishing()) {
						finalDialog.dismiss();
						Log.i("INFO", "showDialog-->close");
					} else {
						if(finalDialog instanceof DialogUtils.IDialogValueSet) {
							DialogUtils.IDialogValueSet tmpDialog = (IDialogValueSet) finalDialog;
							tmpDialog.clear();
						}
					}
				}
				
			});
		} else {
			Log.i("INFO", "finalAct:" + finalAct);
			Log.i("INFO", "finalDialog:" + finalDialog);
			if(finalDialog.isShowing() &&  !finalAct.isFinishing())
				finalDialog.dismiss();
			else {
				if(finalDialog instanceof DialogUtils.IDialogValueSet) {
					DialogUtils.IDialogValueSet tmpDialog = (IDialogValueSet) finalDialog;
					tmpDialog.clear();
				}
			}
				
		}
	}
}
