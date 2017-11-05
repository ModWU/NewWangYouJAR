package cn.yunbee.cn.wangyoujar.joggleUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import cn.yunbee.cn.wangyoujar.joggle.ICheckUpdate;
import cn.yunbee.cn.wangyoujar.joggle.IInitListener;
import cn.yunbee.cn.wangyoujar.joggle.ILoginListener;
import cn.yunbee.cn.wangyoujar.joggle.ILogoutListener;
import cn.yunbee.cn.wangyoujar.joggle.IPayCallBack;
import cn.yunbee.cn.wangyoujar.joggle.IQueryListener;
import cn.yunbee.cn.wangyoujar.joggle.IUpdate;
import cn.yunbee.cn.wangyoujar.joggle.IYunbee;
import dalvik.system.DexClassLoader;

/**
 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
 * <strong>网游接口类</strong></br></br>
 * 建议：接入时将接口getInstance(activity)和yunbeeInit(activity)同时放到子线程中，防止主线程在此过程中花费过多时间。
 * @author Administrator-2017/1/6
 */
public class YunbeeUtils {
	
	private DexClassLoader yunbeeClassLoader;
	
	private String realJarDir;
	private String realSoDir;
	private String realOutDexDir;
	
	//下载的路径，最好在sd下
	private String downloadJarDir;
	private String downloadSoDir;
	
	private IYunbee iYunbee;
	
	private IUpdate iUpdate;
	
	private static final String FILE_DYWANGYOU = "dywangyou";
	
	private static final String ASSETS_DIR = "dy_wangyou_dir";
	
	private static final String SHARE_FILENAME_LOCAL_JAR = "dy_preshare_local_jar";
	
	public static final String COMPT_DYWANGYOU = "compt_type_dywangyou";
	
	static final String REAL_CACHE_DIR = "real_cache_dir";
	
	static final String DOWNLOAD_CACHE_DIR = "download_cache_dir";
	
	
	static final String LIBS_IMPL_NAME = "dywangyouImpl.jar";
	static final String LIBS_SUPPORT_NAME = "supportLibs.jar";
	
	private volatile static YunbeeUtils SINGLE_INSTANCE;
	
	//记录参数，防止替换了加载器之后，重新加载类，需要重新设置这些设置过的参数
	private EntryInfo mEntryInfo = new EntryInfo();
	
	private ClassLoader mParentClassLoader;
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br> 
	 * <font color="red"><code>这个方法第一次调用会花费一定的时间，最好第一次放在子线程中调用.除非cp方调用了典游的开屏界面，这样的话这个方法会被典游先调用。</code></font>
	 * @param activity - 正在交互的activity
	 * @return - YunbeeUtils
	 */
	public static YunbeeUtils getInstance(Activity activity) {
		if(SINGLE_INSTANCE == null) {
			synchronized (YunbeeUtils.class) {
				if(SINGLE_INSTANCE == null)
					SINGLE_INSTANCE = new YunbeeUtils(activity);
			}
		}
		Log.i("INFO", "SINGLE_INSTANCE:" + SINGLE_INSTANCE);
		return SINGLE_INSTANCE;
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <font color="red"><code>在getInstance(Activity)方法调用过一次之后，此方法才可以调用，不然就会抛出异常。</code></font>
	 * @return - YunbeeUtils
	 */
	public static YunbeeUtils getInstance() {
		return SINGLE_INSTANCE;
	}
	
	private YunbeeUtils(Activity activity) {
		mParentClassLoader = Thread.currentThread().getContextClassLoader();
		initYunbeeFilePath(activity);
		final Activity tmpActivity = activity;;
		copyAssetsFiles(tmpActivity);
		checkJarAndComfirm(tmpActivity);
		buildClassLoader(tmpActivity);
		iYunbee = findYunBeeInstance(tmpActivity);
	}
	
	public Object getWXPayEntryActivity() {
		try {
			return Class.forName("cg.yunbee.cn.wangyoujar.sdkpay.WXPayEntryActivityProxy", true, yunbeeClassLoader).newInstance();
		} catch(Exception e) {
			Log.i("INFO", "YunbeeUtils-->getWXPayEntryActivity ex:" + e.toString());
		}
		return null;
	}
	
	private boolean checkJarAndComfirm(Activity activity) {
		SharedPreferencesUtils spfUtils = SharedPreferencesUtils.getInstance(activity, SHARE_FILENAME_LOCAL_JAR);
		boolean isExitsJar = Utils.checkJarExitsInPhone(activity);
		if(!isExitsJar) {
			Log.i("INFO", "YunbeeUtils-->yunbeeInit-->手机里的jar被删除了，重新copyAssetsFiles");
			//先copy->手机中的assets,再下载
			spfUtils.saveBoolean("assets_dywangyou_copy_success", false).commit();
			copyAssetsFiles(activity);
			reloadClassinfo(activity);
		}
		return isExitsJar;
	}
	
	
	private void reloadClassinfo(Activity activity) {
		buildClassLoader(activity);
		iYunbee = findYunBeeInstance(activity);
		Utils.replaceClassLoader(yunbeeClassLoader, activity);
		resetParamData();
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <font color="red"><code>初始化接口，正式初始化典游网游sdk，可以放到子线程中调用</code></font>
	 * @param activity - 正在交互的activity
	 * @return - void
	 */
	public void yunbeeInit(Activity activity) {
		Log.i("baba2", "----yunbeeInit----什么都没做，只初始化");
		Log.i("baba2", "yunbeeInit-->currentThread: " + Thread.currentThread().toString());
		Log.i("baba2", "兼容7.0版本");
		final Activity finalActivity = activity;
		
		mParentClassLoader = Thread.currentThread().getContextClassLoader();
		//checkJarAndComfirm(activity);
		iUpdate = findUpdateInstance(activity);
		Utils.replaceClassLoader(yunbeeClassLoader, finalActivity);
		//在这里检查服务器是否需要下载dynewwangyou.jar
				
		//checkWangyouSdkUpdate(finalActivity);//此方法现在不用
				
		iUpdate.tryAddSdkLibs();
				
		iUpdate.checkUpdate(new ICheckUpdate() {
			
			@Override
			public void checkFinished(boolean isUpdate) {
				Log.i("INFO", "------------checkFinished-文件已经更新------------");
				Log.i("INFO","isUpdate: " + isUpdate);
				if(isUpdate) {
					//清除虚拟机,防止底层c代码将缓存加载器和新创建的加载器进行比较造成崩溃
					finalActivity.finish();
					System.exit(0);
					reloadClassinfo(finalActivity);
				}
				init(finalActivity);
			}
		});
		
	}
	
	private void clearData(Activity activity) {
		//Utils.clearClassLoader(activity);
	}

	
	private void init(final Activity activity) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				iYunbee.yunbeeInit(activity);
			}
			
		});
	}
	

	private void initYunbeeFilePath(Context context) {
		realJarDir = Utils.getCachePath(context).getAbsolutePath() + File.separator + REAL_CACHE_DIR + File.separator + "jar" + File.separator + FILE_DYWANGYOU;
		realOutDexDir = context.getCacheDir().getAbsolutePath() + File.separator + REAL_CACHE_DIR + File.separator + "dex" + File.separator + FILE_DYWANGYOU;
		realSoDir = context.getCacheDir().getAbsolutePath() + File.separator + REAL_CACHE_DIR + File.separator + "so" + File.separator + FILE_DYWANGYOU;
		
		downloadJarDir = Utils.getCachePath(context).getAbsolutePath() + File.separator + DOWNLOAD_CACHE_DIR + File.separator + "jar" + File.separator + FILE_DYWANGYOU;
		downloadSoDir = Utils.getCachePath(context).getAbsolutePath() + File.separator + DOWNLOAD_CACHE_DIR + File.separator + "so" + File.separator + FILE_DYWANGYOU;
		Utils.createFileDir(realJarDir);
		Utils.createFileDir(realOutDexDir);
		Utils.createFileDir(realSoDir);
		
		Utils.createFileDir(downloadJarDir);
		Utils.createFileDir(downloadSoDir);
	}

	private void copyAssetsFiles(Context context) {
		SharedPreferencesUtils spfUtils = SharedPreferencesUtils.getInstance(context, SHARE_FILENAME_LOCAL_JAR);
		
		//只copy一遍，切记！
		boolean isCoppySuccess = spfUtils.getBoolean("assets_dywangyou_copy_success", false);
		
		if(!isCoppySuccess) {
			AssetManager am = context.getAssets();
			isCoppySuccess = Utils.private_copyAssets(am, ASSETS_DIR + File.separator + "jar", realJarDir, false);
			//外部文件
			if(isCoppySuccess) {
				//保存手机中已存在的jar路径。
				Utils.saveLocalJarFlagsToPhone(context, realJarDir);
			}
			isCoppySuccess &= Utils.private_copyAssets(am, ASSETS_DIR + File.separator + "so", realSoDir, false);
			spfUtils.saveBoolean("assets_dywangyou_copy_success", isCoppySuccess).commit();
		}
		
		Log.i("INFO", "YunbeeUtils-->isCoppySuccess:" + isCoppySuccess);
		
	}
	
	private void resetParamData() {
		if(iYunbee != null) {
			iYunbee.setAutoLogin(mEntryInfo.isAutoLogin);
			iYunbee.setDebug(mEntryInfo.isDebug);
			iYunbee.setGameName(mEntryInfo.gameName);
			iYunbee.setInitListener(mEntryInfo.initListener);
			iYunbee.setLoginListener(mEntryInfo.loginListener);
			iYunbee.setLogoutListener(mEntryInfo.logoutListener);
		}
	}
	
	

	//我们自己的网游jar没有so文件，所以很简单
	private void buildClassLoader(Activity context) {
		List<String> allJars = Utils.getPluginsFromPhone(context);
		String load_paths = "";
		if(allJars == null || allJars.size() <= 0) {
			load_paths = realJarDir + File.separator + LIBS_IMPL_NAME
						 + File.pathSeparator + realJarDir + File.separator + LIBS_SUPPORT_NAME;
		} else {
			load_paths = Utils.parsePath(realJarDir, allJars);
		}
		
		if(!new File(realOutDexDir).exists() || !new File(realSoDir).exists()) {
			boolean isCreate = Utils.createFileDir(realOutDexDir);
			Log.i("baba2", "buildClassLoader-->isCreate: " + isCreate);
			isCreate &= Utils.createFileDir(realSoDir);
			if(!isCreate) return;
		}
		
		boolean api_lt7_0 = Build.VERSION.SDK_INT < Build.VERSION_CODES.N;//兼容7.0版本
		
		/*String nativeLibraryDir = getNativeLibraryDir(context);
		
		String allLibraryDir = (nativeLibraryDir == null || api_lt7_0 ? realSoDir : nativeLibraryDir + File.pathSeparator + realSoDir)*/;
		
		/*Log.i("baba2", "buildClassLoader-->api_lt7_0: " + api_lt7_0);
		
		Log.i("baba2", "buildClassLoader-->allLibraryDir: " + allLibraryDir);*/
		
		String nativeLibraryDir = getNativeLibraryDir(context);
		
		String allLibraryDir = (nativeLibraryDir == null || api_lt7_0 ? realSoDir : nativeLibraryDir + File.pathSeparator + realSoDir);
		
		ClassLoader parentClassLoader = api_lt7_0 || (mParentClassLoader == null) ? getClass().getClassLoader() : mParentClassLoader;
		
		yunbeeClassLoader = new DexClassLoader(load_paths, realOutDexDir, allLibraryDir, parentClassLoader);//realSoDir
		
		Log.i("baba2", "buildClassLoader-->api_lt7_0: " + api_lt7_0);
		
		Log.i("baba2", "buildClassLoader-->allLibraryDir: " + allLibraryDir);
		
		Log.i("baba2", "buildClassLoader-->currentThread: " + Thread.currentThread().toString());
		
		Log.i("baba2", "buildClassLoader-->api_lt7_0: " + api_lt7_0);
		
		Log.i("baba2", "buildClassLoader-->mParentClassLoader: " + mParentClassLoader);
		
		Log.i("baba2", "buildClassLoader-->parentClassLoader: " + parentClassLoader);
		
		Log.i("baba2", "buildClassLoader-->yunbeeClassLoader: " + yunbeeClassLoader);
		
	}
	
	
	private String getNativeLibraryDir(Activity activity) {
		ActivityInfo ai = null;
		try {
			ai = activity.getPackageManager().getActivityInfo(activity.getIntent().getComponent(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(ai != null) {
			return ai.applicationInfo.nativeLibraryDir;
		}
		return null;
	}

	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <strong>此方法为支付的统一接口。<br>
	 * <ul type="square">
	 * 		<li>典游将支付的价格分成传入型价格和固定价格(普通价格)</li>
	 * 		<li>一切以计费文件为准，不管是什么类型的价格，计费文件信息都是准确的(后台会自动区分价格的类型)</li>
	 * 		<li>使用者务必传入propId，propName和price。此时propId应该对应典游的计费信息，而propName和price应根据游戏计费的实际情况传入</li>
	 * </ul></strong>
	 * <font color="red"><code><font color="blue" style="font-weight:bold;font-style:italic;">注意:</font>初始化完成时，调用此方法才有效</code></font>
	 * @param activity - 正在交互的activity
	 * @param propId - 道具id(由典游方给出相关计费点信息)
	 * @param propName - 道具名称
	 * @param propPrice - 道具价格(单位:分)
	 * @param cpprivate - CP的透传参数，可以完整传回CP
	 * @param payCallBack - 回调接口 {@link cn.yunbee.cn.wangyoujar.joggle.IPayCallBack 看类IPayCallBack说明}
	 * @return - void
	 */
	public void oGpay(final Activity activity, String propId, String propName, String propPrice, String cpprivate, IPayCallBack payCallBack) {
		iYunbee.oGpay(activity, propId, propName, propPrice, cpprivate, payCallBack);
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <font color="red"><code>退出接口,最好在activity的onDestroy方法里面调用,或最后释放资源的地方.切记,旋转屏幕执行onDestroy的时候调用这个方法的时候,必须重新初始化</code></font>
	 * @param activity - 当前页面，此页面应该和初始化页面一致
	 * @return - void
	 */
	public void exit(Activity activity) {
		iYunbee.exit(activity);
		clearData(activity);
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <font color="red"><code>是否开启debug调试功能,标记为: yunbee_processing。<br><br>测试时可置为true标志</code></font>
	 * @return - void
	 */
	public void setDebug(boolean isDebug) {
		iYunbee.setDebug(isDebug);
		mEntryInfo.isDebug = isDebug;
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <font color="red"><code>重新设置游戏名称-如果cp方更换了游戏名称，请调用此方法通知典游</code></font>
	 * @return - void
	 */
	public void setGameName(String gameName) {
		iYunbee.setGameName(gameName);
		mEntryInfo.gameName = gameName;
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>查询</code>---------<br><br>
	 * 当支付成功之后可调用该方法,必须在此{@link cn.yunbee.cn.wangyoujar.joggle.IPayCallBack#onSuccess(String) onSuccess(String)}方法中调用该函数接口<br>
	 * <strong>说明:</strong><font style="color:red">该方法会花费较长时间，cp方最好能弹出对话款显示等待效果</font>
	 * @return boolean--true:支付成功  false:支付失败
	 */
	public void pc_query(Activity activity, IQueryListener listener) {
		iYunbee.pc_query(activity, listener);
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>不查询</code>---------<br><br>
	 * 当支付成功之后可调用该方法,必须在此{@link cn.yunbee.cn.wangyoujar.joggle.IPayCallBack#onSuccess(String) onSuccess(String)}方法中调用该函数接口<br>
	 * <strong>说明:</strong><font style="color:red">该方法暂时预留，不做任何处理</font>
	 * @return
	 */
	public void pc_no_query() {
		iYunbee.pc_no_query();
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>登录接口</code>---------<br><br>
	 * 如果需要接入典游登录系统，请使用者调用这个接口（此接口必须在初始化完成之后才能调用）
	 * @param activity - 当前请求的activity
	 * @return
	 */
	public void doLogin(Activity activity) {
		iYunbee.doLogin(activity);
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>登出接口</code>---------<br><br>
	 * 如果需要退出典游登录系统，请使用者调用这个接口
	 * @param activity - 当前请求的activity
	 * @param isPopOutWindow - 是否弹出登出的提示框
	 * @return
	 */
	public void doLogout(Activity activity, boolean isPopOutWindow) {
		iYunbee.doLogout(activity, isPopOutWindow);
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>设置登录状态监听</code>---------<br><br>
	 * @param loginListener - {@link cn.yunbee.cn.wangyoujar.joggle.ILoginListener ILoginListener}
	 * @return
	 */
	public YunbeeUtils setLoginListener(ILoginListener loginListener) {
		iYunbee.setLoginListener(loginListener);
		mEntryInfo.loginListener = loginListener;
		return this;
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>设置初始化状态监听</code>---------<br><br>
	 * @param loginListener - {@link cn.yunbee.cn.wangyoujar.joggle.IInitListener ILoginListener}
	 * @return
	 */
	public YunbeeUtils setInitListener(IInitListener initListener) {
		iYunbee.setInitListener(initListener);
		mEntryInfo.initListener = initListener;
		return this;
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>设置登出状态监听</code>---------<br><br>
	 * @param logoutListener - {@link cn.yunbee.cn.wangyoujar.joggle.ILogoutListener ILogoutListener}
	 * @return
	 */
	public YunbeeUtils setLogoutListener(ILogoutListener logoutListener) {
		iYunbee.setLogoutListener(logoutListener);
		mEntryInfo.logoutListener = logoutListener;
		return this;
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>是否开启了自动登录</code>---------<br><br>
	 * 如果用户最近登录了一次，并且该账户开启了自动登录功能，该接口就返回true，否则返回false<br><br>
	 * <strong>注意：</strong>如果最近一次登录的账户开启了自动登录功能，当用户调用{@link #doLogin doLogin}接口时，不会再次弹出登录窗口，不能切换其他账号了。
	 * @return
	 */
	public boolean isAutoLogin() {
		return iYunbee.isAutoLogin();
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>设置自动登录</code>---------<br><br>
	 * 如果用户最近登录了一次，并且该账户开启了自动登录功能，调用该接口则可以取消该账户的自动登录功能
	 * @return
	 */
	public void setAutoLogin(boolean isAutoLogin) {
		iYunbee.setAutoLogin(isAutoLogin);
		mEntryInfo.isAutoLogin = isAutoLogin;
	}
	
	public Activity getYunbeeActivity() {
		try {
			Class<?> clazz = Class.forName("cg.yunbee.cn.wangyoujar.YunbeeActivity", true, yunbeeClassLoader);
			return (Activity) clazz.newInstance();
		} catch (Exception e) {
			Log.i("chao", "........findYunBeeInstance:" + e.toString());
		}
		return null;
	}
	
	
	private IYunbee findYunBeeInstance(Activity activity) {
		try {
			Class<?> clazz = Class.forName("cg.yunbee.cn.wangyoujar.Yunbee", true, yunbeeClassLoader);
			Method method = clazz.getDeclaredMethod("getInstance", new Class<?>[]{Activity.class});
			IYunbee instance = (IYunbee) method.invoke(clazz, new Object[]{activity});
			Log.i("chao", "........instance:" + instance);
			return instance;
		} catch (Exception e) {
			Log.i("chao", "........findYunBeeInstance:" + e.toString());
		}
		return new YunbeeNull(activity);
	}
	
	private IUpdate findUpdateInstance(Activity activity) {
		try {
			Class<?> clazz = Class.forName("cn.yunbee.cn.wangyoujar.update.YunbeeUpdate", true, yunbeeClassLoader);
			Method method = clazz.getDeclaredMethod("newInstance", new Class<?>[]{Activity.class});
			IUpdate instance = (IUpdate) method.invoke(clazz, new Object[]{activity});
			return instance;
		} catch (Exception e) {
			Log.i("chao", "........findUpdateInstance:" + e.toString());
		}
		return new UpdateNull(activity);
	}
}
