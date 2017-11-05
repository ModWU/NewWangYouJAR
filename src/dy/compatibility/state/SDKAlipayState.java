package dy.compatibility.state;

import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import dy.compatibility.work.Contant;
import dy.compatibility.work.Utils;

public class SDKAlipayState implements IState  {

	@Override
	public boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir,
			boolean isReplace) {
		String assets_jar = fromAssetsDir + File.separator + Contant.FILE_JAR;
		
		AssetManager am = context.getApplicationContext().getAssets();
		
		//copy支付宝
		String assets_alipay_jar_dir = assets_jar + File.separator + Contant.FILE_ALIPAY;
		String to_alipay_jar_dir = toJarDir + File.separator + Contant.FILE_ALIPAY;
		Log.i("INFO", "-------------assets->copy支付宝:jar---------------");
		boolean isCoptySuccess = Utils.private_copyAssets(am, assets_alipay_jar_dir, to_alipay_jar_dir, isReplace);
		if(isCoptySuccess) SDKStateContext.saveLocalJarFlagsToPhone(context, Contant.COMPT_ALIPAY, to_alipay_jar_dir);
		
		Log.i("INFO", "alipay copy result: " + isCoptySuccess);
		
		return isCoptySuccess;
	}

	@Override
	public String getJarPathsFlag() {
		return "flag_alipay_jarPaths";
	}

	@Override
	public boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir) {
		Log.i("INFO", "-------------支付宝->checkCopyFile---------------");
		boolean isReCopy = Utils.checkCopyFile(context, getJarPathsFlag(), fromAssetsDir, toJarDir, toSoDir);
		if(isReCopy) {
			Log.i("INFO", "重新copy所有文件(不替换已存在的文件)");
			copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, false);
		}
		return isReCopy;
	}


}
