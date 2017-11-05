package dy.compatibility.state;

import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import dy.compatibility.work.Contant;
import dy.compatibility.work.Utils;

public class SDKWftState implements IState {

	@Override
	public boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir,
			boolean isReplace) {
		
		String assets_jar = fromAssetsDir + File.separator + Contant.FILE_JAR;
		
		AssetManager am = context.getApplicationContext().getAssets();
		
		/************************all文件jar************************************/
		/**
		 * copy jar文件
		 */
		//copy威富通 jar
		String assets_wft_jar_dir = assets_jar + File.separator + Contant.FILE_ALL + File.separator + Contant.FILE_WFT;
		String to_wft_jar_dir =  toJarDir + File.separator + Contant.FILE_ALL + File.separator + Contant.FILE_WFT;
		Log.i("INFO", "-------------assets->copy威富通:jar---------------");
		boolean isCoptySuccess = Utils.private_copyAssets(am, assets_wft_jar_dir, to_wft_jar_dir, isReplace);	
		if(isCoptySuccess) SDKStateContext.saveLocalJarFlagsToPhone(context, Contant.COMPT_ALL_WFT, to_wft_jar_dir);
		Log.i("INFO", "wft copy result: " + isCoptySuccess);
		return isCoptySuccess;
	}


	@Override
	public String getJarPathsFlag() {
		return "flag_wft_jarPaths";
	}

	@Override
	public boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir) {
		Log.i("INFO", "-------------威富通->checkCopyFile---------------");
		boolean isReCopy = Utils.checkCopyFile(context, getJarPathsFlag(), fromAssetsDir, toJarDir, toSoDir);
		if(isReCopy) {
			Log.i("INFO", "重新copy所有文件(不替换已存在的文件)");
			copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, false);
		}
		return isReCopy;
	}

}
