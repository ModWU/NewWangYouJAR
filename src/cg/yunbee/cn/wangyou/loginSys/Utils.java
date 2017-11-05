package cg.yunbee.cn.wangyou.loginSys;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;

public class Utils {
	public static void setPwdVisible(EditText et, 
			boolean isVisible) {
		if(et != null) {
			if(isVisible)
				 et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			else 
				et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT); 
		}
	}
	
	public static void setCursorToLast(EditText et) {
		if(et != null) {
			CharSequence text = et.getText();
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());// 将光标移动到最后
			}
		}
	}
	
	public interface DialogListener {
		void positive();
		void negative();
	}
	
	
	
	public static void returnRoot(Activity activity) {
		Intent loginAtIt = new Intent(activity, DyLoginActivity.class);
		activity.startActivity(loginAtIt);
		activity.finish();
	}
	
	
	public static Dialog getDialog(Activity activity, final DialogListener listener, String title, String message, String negativeTxt, String positiveTxt) {
		
		OnClickListener positiveListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					listener.positive();
				}
			}
		};
		
		OnClickListener negativeListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null)
					listener.negative();
			}
		};
		
		return new AlertDialog.Builder(activity).setTitle(title).setMessage(message).setNegativeButton(negativeTxt, negativeListener).setPositiveButton(positiveTxt, positiveListener).create();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setFinishOnTouchOutside(Activity activity, boolean isFinished) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			activity.setFinishOnTouchOutside(isFinished);
		}
	}
	
	public static boolean isOutOfBounds(Activity activity, MotionEvent event) {
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		final int slop = ViewConfiguration.get(activity).getScaledWindowTouchSlop();
		final View decorView = activity.getWindow().getDecorView();
		return (x < -slop) || (y < -slop)|| (x > (decorView.getWidth() + slop))|| (y > (decorView.getHeight() + slop));
	}
	
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}
	
	public static boolean isHasSurface() {
		if(DyLoginActivity.activity != null && !DyLoginActivity.activity.isFinishing()) {
			return true;
		}
		
		if(DyAlterActivity.activity != null && !DyAlterActivity.activity.isFinishing()) {
			return true;
		}
		
		if(DyRegisterActivity.activity != null && !DyRegisterActivity.activity.isFinishing()) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 香港
	 * @param str
	 * @return
	 */
	public static boolean isChinaPhoneLegal(String str) {  
		if(str == null) {
			return false;
		}
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }  
  
    /** 
     * 香港手机号码8位数�?5|6|8|9�?�?+7位任意数 
     */  
    public static boolean isHKPhoneLegal(String str) {  
    	if(str == null) {
    		return false;
    	}
        String regExp = "^(5|6|8|9)\\d{7}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }  
    
    public static boolean isMobile(String str) {
    	return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }
    
    
    public static String getResourceStr(Context context, String strName) {
    	return context.getResources().getString(ResourceUtils.getStringId(context, strName));
    }
    
    
	@TargetApi(23)
	public static int getResourceColor(Context context, String strColorName) {
    	final int version = Build.VERSION.SDK_INT;
    	int colorId = ResourceUtils.getColorId(context, strColorName);
        if (version >= 23) {
            return context.getColor(colorId);
        } else {
            return context.getResources().getColor(colorId);
        }
    }
    
    public static void hideSoftInput(View view, Context context) {
    	InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
		
    
    public static void showSoftInput(View view, Context context) {
    	InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
    	imm.showSoftInput(view,InputMethodManager.SHOW_FORCED); 
    }
}
