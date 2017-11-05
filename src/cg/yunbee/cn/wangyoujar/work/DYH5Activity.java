package cg.yunbee.cn.wangyoujar.work;


import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cg.yunbee.cn.wangyoujar.pay.SDKPay;
import cg.yunbee.cn.wangyoujar.pojo.TAGS;
import cg.yunbee.cn.wangyoujar.utils.ResourceUtils;
import cg.yunbee.cn.wangyoujar.utils.Util;
import cg.yunbee.cn.wangyoujar.work.NetworkChangeReceiver.IOnNetworkChangeListener;
import cn.yunbee.cn.wangyoujar.views.ProgressWebView;


public class DYH5Activity extends Activity {
	
	private ProgressWebView webview;
	
	private TextView webview_bar;
	
	private LinearLayout webViewLay;
	
	private NetworkChangeReceiver networkReceiver;
	
	private String webViewUrl = "https://www.baidu.com", webViewTitle = "典支付";
	
	public static final String WEBVIEW_URL = "dianyou_advert_webview_url";
	//public static final String WEBVIEW_TITLE = "dianyou_advert_webview_title";
	
	private static final String LAYOUT_NAME = "dian_payweb_activity";
	private static final String WIEW_BAR_RES_NAME = "dianyou_advert_webviewbar_style";
	
	public static final String WEBVIEW_METHOD = "METHOD";
	public static final String POST = "POST";
	public static final String GET = "GET";
	
	private String method = GET;
	
	private static SDKPay mSDKPay;
	
	public static final String PAGE_SEE = "page_see";
	
	public static final int PAGE_DYH5 = 0;
	public static final int PAGE_WFT_WEIXIN = 1;
	public static final int PAGE_JZSD_WEIXIN = 2;
	public static final int PAGE_DYH5_WEIXIN = 3;
	public static final int PAGE_DYH5_ALIPAY = 4;
	public static final int PAGE_OKPAY_WEIXIN = 5;
	public static final int PAGE_OKPAY_APLIPAY = 6;
	
	private int page_see = PAGE_DYH5;
	
	public static void setSDKPay(SDKPay sdkPay) {
		mSDKPay = sdkPay;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int layoutId = ResourceUtils.getLayoutId(this, LAYOUT_NAME);
		if(layoutId == 0) {
			Log.i("INFO", "Not find the resource id of layout \"" + LAYOUT_NAME + "\".");
			Toast.makeText(this, "webview的布局文件没有找到,请放入到您的工程中!", Toast.LENGTH_LONG).show();
			finish();
		} else {
			setContentView(layoutId);
			initViews();
			setData();
			setWebView();
		}
		
		networkReceiver = new NetworkChangeReceiver(listener);
		IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkReceiver, networkFilter);
	}
	
	

	private void setData() {
		Intent intent = getIntent();
		
		String url = intent.getStringExtra(WEBVIEW_URL);
		String methd = intent.getStringExtra(WEBVIEW_METHOD);
		//String title = intent.getStringExtra(WEBVIEW_TITLE);
		if(url != null)
			webViewUrl = url;
		if(methd != null)
			method = methd;
		
		
		page_see = intent.getIntExtra(PAGE_SEE, PAGE_DYH5);
		
		/*if(title != null)
			webViewTitle = title;*/
		
		
		webview_bar.setText(webViewTitle);
	}
	
	private void setWebView() {
		WebSettings webSettings = webview.getSettings();
		webSettings.setBuiltInZoomControls(false);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new JSObject(), "JsTest");
		webview.addJavascriptInterface(new JSObject(), "jsClose");
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1)
			webSettings.setDomStorageEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		/*webSettings.setBuiltInZoomControls(false);
		webSettings.setUseWideViewPort(true);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setAppCacheEnabled(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		//webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1)
			webSettings.setDomStorageEnabled(true);
		
		webview.requestFocus();  
		webview.setScrollBarStyle(0);
		webview.addJavascriptInterface(jsObject, "JsTest");*/
		
		webview.setWebViewClient(new WebViewClient() {
			
			
			@SuppressLint("NewApi")
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
				return doThingBeforeIntercept(view, request.getUrl().toString());
			}
			
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				return doThingBeforeIntercept(view, url);
			}
			
			private boolean isDoThingBeforeIntercept() {
				if(page_see == PAGE_JZSD_WEIXIN || page_see == PAGE_OKPAY_WEIXIN) {
					return true;
				}
				return false;
			}
			
			
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			private WebResourceResponse doThingBeforeIntercept(WebView view, String url) {
				if(isDoThingBeforeIntercept()) {
					boolean weixin = url.startsWith("https://wx.") || url.startsWith("http://wx.");
					if(weixin) {
						try { 
							Log.i("INFO", "doThingBeforeIntercept-->enter");
							URL mUrl=new URL(url); 
							HttpURLConnection connection= (HttpURLConnection) mUrl.openConnection(); 
							connection.setDoInput(true); 
							connection.setDoOutput(true); 
							connection.setUseCaches(false); 
							connection.setRequestMethod("GET"); 
							connection.setRequestProperty("Referer", getReferer()); 
							DataOutputStream os=new DataOutputStream(connection.getOutputStream()); 
							os.flush(); 
							return new WebResourceResponse("text/html", connection.getContentEncoding(), connection.getInputStream()); 
						} catch (Exception e) { 
							TAGS.log(Log.getStackTraceString(e));
						}
					}
				}
				return super.shouldInterceptRequest(view, url);	
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				boolean weixin = url.startsWith("weixin://wap/pay?");
				boolean alipay = url.startsWith("alipays://platformapi/startApp?");
				if(page_see == PAGE_JZSD_WEIXIN || page_see == PAGE_OKPAY_WEIXIN) {
					if(url.startsWith("http:") || url.startsWith("https:")) {
						loadRefererUrl(url);
				        return true;
					} else if(weixin) {
						 Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
					     DYH5Activity.this.startActivity(intent);
					     return true;
					}
				} else {
					if(weixin || alipay) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						DYH5Activity.this.startActivity(intent);
						return true;
					}
				}
				Log.i("INFO", "最下面: " + url);
				view.loadUrl(url);
				return true;
			}
			

		});
		
		webview.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String arg1, String arg2, String arg3, long arg4) {
				if (url != null && url.startsWith("http://"))
	                   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				
			}
		});
		/*webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });*/
		
		
		
		if(POST.equals(method)) {
			int index_ = webViewUrl.indexOf("?");
			String url = webViewUrl;
			String params = "";
			if(index_ > 0) {
				url = webViewUrl.substring(0, index_);
				params = webViewUrl.substring(index_ + 1, webViewUrl.length());
			}
			
			byte[] paramData = new byte[0];
			try {
				paramData = params.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				Log.i("INFO", "setWebView-->UnsupportedEncodingException: " + e.toString());
			}
			
			webview.postUrl(url, paramData);
		} else {
			if(page_see == PAGE_JZSD_WEIXIN || page_see == PAGE_OKPAY_WEIXIN) {
				loadRefererUrl(webViewUrl);
			} else {
				webview.loadUrl(webViewUrl);
			}
			
		}
		
	}
	
	private String getReferer() {
		if(page_see == PAGE_JZSD_WEIXIN) {
			return "http://www.toppsp.com";
		} else if(page_see == PAGE_OKPAY_WEIXIN) {
			return "http://pay.yunbee.cn";
		} else {
			return "";
		}
	}
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	private void loadRefererUrl(String url) {
		int api_level = Build.VERSION.SDK_INT;
		if(api_level >=  Build.VERSION_CODES.FROYO) {
			Log.i("INFO", "api大于8");
			Map<String, String> extraHeaders = new HashMap<String, String>();
			extraHeaders.put("Referer", getReferer());
			webview.loadUrl(url, extraHeaders);
		} else {
			Log.i("INFO", "api小于8");
			webview.loadUrl(url);
		}
	}
	

	private void initViews() {
		int webviewbarId = ResourceUtils.getId(this, "webview_bar");
		int webviewlayid = ResourceUtils.getId(this, "id_webview_lay");
		int webviewStyleId = ResourceUtils.getDrawableId(this, WIEW_BAR_RES_NAME);
		webview_bar = (TextView) findViewById(webviewbarId);
		
		webViewLay = (LinearLayout) findViewById(webviewlayid);
		webview = new ProgressWebView(getApplicationContext());
		LinearLayout.LayoutParams webview_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		webview.setLayoutParams(webview_lp);
		if(webviewStyleId != 0)
			webview.setBackgroundResource(webviewStyleId);
		else
			webview.setBackgroundColor(Color.WHITE);
		webViewLay.addView(webview);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (!isDirectBack() && webview.canGoBack()) {
					webview.goBack();
				} else {
					tryCancelPay();
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean isBack = false;
	
	class JSObject {
		@JavascriptInterface
		public void back() {
			isBack = true;
			if(mSDKPay != null) {
				finish();
				mSDKPay.paySuccess();
			}
		}
		
		@JavascriptInterface
		public void JsCallAndroid(String url) {
		}
	}
	
	
	private void tryCancelPay() {
		if(!isBack && mSDKPay != null) {
			mSDKPay.payCancel();
			mSDKPay = null;
		}
	}

	
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void performBack(View v) {
		if (!isDirectBack() && webview.canGoBack()) {
			webview.goBack();
			return;
		}
		
		tryCancelPay();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			super.onBackPressed();
		} else {
			finish();
		}
	}
	
	private boolean isDirectBack() {
		if(page_see == PAGE_JZSD_WEIXIN) {
			return true;
		}
		return false;
	}
	
	public void openLink(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(this.webViewUrl));
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Util.releaseAllWebViewCallback();
		if(webview != null) {
			webview.removeAllViews();
			webViewLay.removeView(webview);
			webview.destroy();
			webview.setVisibility(View.GONE);
			webview = null;
		}
		
		if(networkReceiver != null) {
			unregisterReceiver(networkReceiver);
			networkReceiver = null;
		}
	}
	
	private IOnNetworkChangeListener listener = new IOnNetworkChangeListener() {
		
		@Override
		public void openNetwork() {
			Log.i("INFO", "reload");
			if(webview != null)
				webview.reload();
		}

		@Override
		public void closeNetwork() {
			// TODO Auto-generated method stub
			
		}
	};

}

