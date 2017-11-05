package dy.compatibility.state;

import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import dy.compatibility.work.Contant;
import dy.compatibility.work.Utils;

public class SDKAibeiState implements IState {

	@Override
	public boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir,
			boolean isReplace) {
		String assets_jar = fromAssetsDir + File.separator + Contant.FILE_JAR;
		//String assets_so = fromAssetsDir + File.separator + Contant.FILE_SO;
		
		AssetManager am = context.getApplicationContext().getAssets();
		//����sdk�ŵ�����������
		/**
		 * copy jar�ļ�
		 */
		//copy����
		String assets_aibei_jar_dir = assets_jar + File.separator + Contant.FILE_AIBEI;
		String to_aibei_jar_dir =  toJarDir + File.separator + Contant.FILE_AIBEI;
		
		Log.i("INFO", "-------------assets->copy����:jar---------------");
		boolean isSuccess = Utils.private_copyAssets(am, assets_aibei_jar_dir, to_aibei_jar_dir, isReplace);
		if(isSuccess) SDKStateContext.saveLocalJarFlagsToPhone(context, Contant.COMPT_AIBEI, to_aibei_jar_dir);
		
		/**
		 * copy so-----------------�ļ�
		 *//*
		//����ֻ�cpu���ͣ�ѡ�񱾵���ƥ���Ŀ¼(ֻ��Ŀ¼)
		Log.i("INFO", "-------------assets->copy����:so---------------");
		Log.i("INFO", "Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
		String soLibraryDir = Utils.chooseBestLibraryFromAssets(am, assets_so + File.separator + Contant.FILE_AIBEI);
		//��ʱ���ǽ���ĵ�����sdkû��so���ӿ⣬����ֻ���ð���������һ��
		//copy����
		String assets_aibei_so_dir = assets_so + File.separator + Contant.FILE_AIBEI + File.separator + soLibraryDir;
		String to_aibei_so_dir =  toSoDir + File.separator + Contant.FILE_AIBEI;
		isSuccess &= Utils.private_copyAssets(am, assets_aibei_so_dir, to_aibei_so_dir, isReplace);*/
		
		Log.i("INFO", "aibei copy result: " + isSuccess);
		
		return isSuccess;
	}

	@Override
	public String getJarPathsFlag() {
		return "flag_aibei_jarPaths";
	}

	@Override
	public boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir) {
		Log.i("INFO", "-------------����->checkCopyFile---------------");
		boolean isReCopy = Utils.checkCopyFile(context, getJarPathsFlag(), fromAssetsDir, toJarDir, toSoDir);
		if(isReCopy) {
			Log.i("INFO", "����copy�����ļ�(���滻�Ѵ��ڵ��ļ�)");
			copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, false);
		}
		return isReCopy;
	}


}
