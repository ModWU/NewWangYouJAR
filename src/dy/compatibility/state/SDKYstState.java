package dy.compatibility.state;

import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import dy.compatibility.work.Contant;
import dy.compatibility.work.Utils;

public class SDKYstState implements IState {

	@Override
	public boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir,
			boolean isReplace) {
		String assets_jar = fromAssetsDir + File.separator + Contant.FILE_JAR;
		String assets_so = fromAssetsDir + File.separator + Contant.FILE_SO;
		
		AssetManager am = context.getApplicationContext().getAssets();
		//各个sdk放到各个包体下
		/**
		 * copy jar文件
		 */
		//copy爱贝
		String assets_yst_jar_dir = assets_jar + File.separator + Contant.FILE_YST;
		String to_yst_jar_dir =  toJarDir + File.separator + Contant.FILE_YST;
		
		Log.i("INFO", "-------------assets->copy银视通:jar---------------");
		boolean isSuccess = Utils.private_copyAssets(am, assets_yst_jar_dir, to_yst_jar_dir, isReplace);
		if(isSuccess) SDKStateContext.saveLocalJarFlagsToPhone(context, Contant.COMPT_YST, to_yst_jar_dir);
		
		/**
		 * copy so-----------------文件
		 */
		//检测手机cpu类型，选择本地最匹配的目录(只是目录)
		Log.i("INFO", "-------------assets->copy银视通:so---------------");
		Log.i("INFO", "Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
		String soLibraryDir = Utils.chooseBestLibraryFromAssets(am, assets_so + File.separator + Contant.FILE_YST);
		//copy爱贝
		String assets_yst_so_dir = assets_so + File.separator + Contant.FILE_YST + File.separator + soLibraryDir;
		String to_yst_so_dir =  toSoDir + File.separator + Contant.FILE_YST;
		isSuccess &= Utils.private_copyAssets(am, assets_yst_so_dir, to_yst_so_dir, isReplace);
		
		Log.i("INFO", "yst copy result: " + isSuccess);
		
		return isSuccess;
	}

	@Override
	public String getJarPathsFlag() {
		return "flag_yst_jarPaths";
	}
	
	@Override
	public boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir) {
		Log.i("INFO", "-------------银视通->checkCopyFile---------------");
		boolean isReCopy = Utils.checkCopyFile(context, getJarPathsFlag(), fromAssetsDir, toJarDir, toSoDir);
		if(isReCopy) {
			Log.i("INFO", "重新copy所有文件(不替换已存在的文件)");
			copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, false);
		}
		return isReCopy;
	}
}
