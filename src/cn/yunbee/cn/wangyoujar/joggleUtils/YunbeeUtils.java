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
 * <strong>���νӿ���</strong></br></br>
 * ���飺����ʱ���ӿ�getInstance(activity)��yunbeeInit(activity)ͬʱ�ŵ����߳��У���ֹ���߳��ڴ˹����л��ѹ���ʱ�䡣
 * @author Administrator-2017/1/6
 */
public class YunbeeUtils {
	
	private DexClassLoader yunbeeClassLoader;
	
	private String realJarDir;
	private String realSoDir;
	private String realOutDexDir;
	
	//���ص�·���������sd��
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
	
	//��¼��������ֹ�滻�˼�����֮�����¼����࣬��Ҫ����������Щ���ù��Ĳ���
	private EntryInfo mEntryInfo = new EntryInfo();
	
	private ClassLoader mParentClassLoader;
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br> 
	 * <font color="red"><code>���������һ�ε��ûỨ��һ����ʱ�䣬��õ�һ�η������߳��е���.����cp�������˵��εĿ������棬�����Ļ���������ᱻ�����ȵ��á�</code></font>
	 * @param activity - ���ڽ�����activity
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
	 * <font color="red"><code>��getInstance(Activity)�������ù�һ��֮�󣬴˷����ſ��Ե��ã���Ȼ�ͻ��׳��쳣��</code></font>
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
			Log.i("INFO", "YunbeeUtils-->yunbeeInit-->�ֻ����jar��ɾ���ˣ�����copyAssetsFiles");
			//��copy->�ֻ��е�assets,������
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
	 * <font color="red"><code>��ʼ���ӿڣ���ʽ��ʼ����������sdk�����Էŵ����߳��е���</code></font>
	 * @param activity - ���ڽ�����activity
	 * @return - void
	 */
	public void yunbeeInit(Activity activity) {
		Log.i("baba2", "----yunbeeInit----ʲô��û����ֻ��ʼ��");
		Log.i("baba2", "yunbeeInit-->currentThread: " + Thread.currentThread().toString());
		Log.i("baba2", "����7.0�汾");
		final Activity finalActivity = activity;
		
		mParentClassLoader = Thread.currentThread().getContextClassLoader();
		//checkJarAndComfirm(activity);
		iUpdate = findUpdateInstance(activity);
		Utils.replaceClassLoader(yunbeeClassLoader, finalActivity);
		//��������������Ƿ���Ҫ����dynewwangyou.jar
				
		//checkWangyouSdkUpdate(finalActivity);//�˷������ڲ���
				
		iUpdate.tryAddSdkLibs();
				
		iUpdate.checkUpdate(new ICheckUpdate() {
			
			@Override
			public void checkFinished(boolean isUpdate) {
				Log.i("INFO", "------------checkFinished-�ļ��Ѿ�����------------");
				Log.i("INFO","isUpdate: " + isUpdate);
				if(isUpdate) {
					//��������,��ֹ�ײ�c���뽫������������´����ļ��������бȽ���ɱ���
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
		
		//ֻcopyһ�飬�мǣ�
		boolean isCoppySuccess = spfUtils.getBoolean("assets_dywangyou_copy_success", false);
		
		if(!isCoppySuccess) {
			AssetManager am = context.getAssets();
			isCoppySuccess = Utils.private_copyAssets(am, ASSETS_DIR + File.separator + "jar", realJarDir, false);
			//�ⲿ�ļ�
			if(isCoppySuccess) {
				//�����ֻ����Ѵ��ڵ�jar·����
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
	
	

	//�����Լ�������jarû��so�ļ������Ժܼ�
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
		
		boolean api_lt7_0 = Build.VERSION.SDK_INT < Build.VERSION_CODES.N;//����7.0�汾
		
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
	 * <strong>�˷���Ϊ֧����ͳһ�ӿڡ�<br>
	 * <ul type="square">
	 * 		<li>���ν�֧���ļ۸�ֳɴ����ͼ۸�͹̶��۸�(��ͨ�۸�)</li>
	 * 		<li>һ���ԼƷ��ļ�Ϊ׼��������ʲô���͵ļ۸񣬼Ʒ��ļ���Ϣ����׼ȷ��(��̨���Զ����ּ۸������)</li>
	 * 		<li>ʹ������ش���propId��propName��price����ʱpropIdӦ�ö�Ӧ���εļƷ���Ϣ����propName��priceӦ������Ϸ�Ʒѵ�ʵ���������</li>
	 * </ul></strong>
	 * <font color="red"><code><font color="blue" style="font-weight:bold;font-style:italic;">ע��:</font>��ʼ�����ʱ�����ô˷�������Ч</code></font>
	 * @param activity - ���ڽ�����activity
	 * @param propId - ����id(�ɵ��η�������ؼƷѵ���Ϣ)
	 * @param propName - ��������
	 * @param propPrice - ���߼۸�(��λ:��)
	 * @param cpprivate - CP��͸��������������������CP
	 * @param payCallBack - �ص��ӿ� {@link cn.yunbee.cn.wangyoujar.joggle.IPayCallBack ����IPayCallBack˵��}
	 * @return - void
	 */
	public void oGpay(final Activity activity, String propId, String propName, String propPrice, String cpprivate, IPayCallBack payCallBack) {
		iYunbee.oGpay(activity, propId, propName, propPrice, cpprivate, payCallBack);
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <font color="red"><code>�˳��ӿ�,�����activity��onDestroy�����������,������ͷ���Դ�ĵط�.�м�,��ת��Ļִ��onDestroy��ʱ��������������ʱ��,�������³�ʼ��</code></font>
	 * @param activity - ��ǰҳ�棬��ҳ��Ӧ�úͳ�ʼ��ҳ��һ��
	 * @return - void
	 */
	public void exit(Activity activity) {
		iYunbee.exit(activity);
		clearData(activity);
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <font color="red"><code>�Ƿ���debug���Թ���,���Ϊ: yunbee_processing��<br><br>����ʱ����Ϊtrue��־</code></font>
	 * @return - void
	 */
	public void setDebug(boolean isDebug) {
		iYunbee.setDebug(isDebug);
		mEntryInfo.isDebug = isDebug;
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * <font color="red"><code>����������Ϸ����-���cp����������Ϸ���ƣ�����ô˷���֪ͨ����</code></font>
	 * @return - void
	 */
	public void setGameName(String gameName) {
		iYunbee.setGameName(gameName);
		mEntryInfo.gameName = gameName;
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>��ѯ</code>---------<br><br>
	 * ��֧���ɹ�֮��ɵ��ø÷���,�����ڴ�{@link cn.yunbee.cn.wangyoujar.joggle.IPayCallBack#onSuccess(String) onSuccess(String)}�����е��øú����ӿ�<br>
	 * <strong>˵��:</strong><font style="color:red">�÷����Ứ�ѽϳ�ʱ�䣬cp������ܵ����Ի�����ʾ�ȴ�Ч��</font>
	 * @return boolean--true:֧���ɹ�  false:֧��ʧ��
	 */
	public void pc_query(Activity activity, IQueryListener listener) {
		iYunbee.pc_query(activity, listener);
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>����ѯ</code>---------<br><br>
	 * ��֧���ɹ�֮��ɵ��ø÷���,�����ڴ�{@link cn.yunbee.cn.wangyoujar.joggle.IPayCallBack#onSuccess(String) onSuccess(String)}�����е��øú����ӿ�<br>
	 * <strong>˵��:</strong><font style="color:red">�÷�����ʱԤ���������κδ���</font>
	 * @return
	 */
	public void pc_no_query() {
		iYunbee.pc_no_query();
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>��¼�ӿ�</code>---------<br><br>
	 * �����Ҫ������ε�¼ϵͳ����ʹ���ߵ�������ӿڣ��˽ӿڱ����ڳ�ʼ�����֮����ܵ��ã�
	 * @param activity - ��ǰ�����activity
	 * @return
	 */
	public void doLogin(Activity activity) {
		iYunbee.doLogin(activity);
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>�ǳ��ӿ�</code>---------<br><br>
	 * �����Ҫ�˳����ε�¼ϵͳ����ʹ���ߵ�������ӿ�
	 * @param activity - ��ǰ�����activity
	 * @param isPopOutWindow - �Ƿ񵯳��ǳ�����ʾ��
	 * @return
	 */
	public void doLogout(Activity activity, boolean isPopOutWindow) {
		iYunbee.doLogout(activity, isPopOutWindow);
	}
	
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>���õ�¼״̬����</code>---------<br><br>
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
	 * ---------<code>���ó�ʼ��״̬����</code>---------<br><br>
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
	 * ---------<code>���õǳ�״̬����</code>---------<br><br>
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
	 * ---------<code>�Ƿ������Զ���¼</code>---------<br><br>
	 * ����û������¼��һ�Σ����Ҹ��˻��������Զ���¼���ܣ��ýӿھͷ���true�����򷵻�false<br><br>
	 * <strong>ע�⣺</strong>������һ�ε�¼���˻��������Զ���¼���ܣ����û�����{@link #doLogin doLogin}�ӿ�ʱ�������ٴε�����¼���ڣ������л������˺��ˡ�
	 * @return
	 */
	public boolean isAutoLogin() {
		return iYunbee.isAutoLogin();
	}
	
	/**
	 * <code>encoding:</code><strong>GBK</strong>&nbsp(Please adjust the encoding environment of the current file to GBK type)<br><br>
	 * ---------<code>�����Զ���¼</code>---------<br><br>
	 * ����û������¼��һ�Σ����Ҹ��˻��������Զ���¼���ܣ����øýӿ������ȡ�����˻����Զ���¼����
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
