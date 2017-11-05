package dy.compatibility.state;

import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import dy.compatibility.work.Contant;
import dy.compatibility.work.Utils;

public class SDKWeiXinState implements IState  {

	@Override
	public boolean copyAssetsFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir,
			boolean isReplace) {
		
		String assets_jar = fromAssetsDir + File.separator + Contant.FILE_JAR;
		
		AssetManager am = context.getApplicationContext().getAssets();
		
		/************************all�ļ�jar************************************/
		/**
		 * copy jar�ļ�
		 */
		//copy΢�� jar
		String assets_weixin_jar_dir = assets_jar + File.separator + Contant.FILE_ALL + File.separator + Contant.FILE_WEIXIN;
		String to_weixin_jar_dir =  toJarDir + File.separator + Contant.FILE_ALL + File.separator + Contant.FILE_WEIXIN;
		Log.i("INFO", "-------------assets->copy΢��:jar---------------");
		boolean isCoptySuccess = Utils.private_copyAssets(am, assets_weixin_jar_dir, to_weixin_jar_dir, isReplace);	
		if(isCoptySuccess) SDKStateContext.saveLocalJarFlagsToPhone(context, Contant.COMPT_ALL_WEIXIN, to_weixin_jar_dir);
		Log.i("INFO", "weixin copy result: " + isCoptySuccess);
		return isCoptySuccess;
	}


	@Override
	public String getJarPathsFlag() {
		return "flag_weixin_jarPaths";
	}

	@Override
	public boolean checkCopyFile(Context context, String fromAssetsDir, String toJarDir, String toSoDir) {
		Log.i("INFO", "-------------΢��->checkCopyFile---------------");
		boolean isReCopy = Utils.checkCopyFile(context, getJarPathsFlag(), fromAssetsDir, toJarDir, toSoDir);
		if(isReCopy) {
			Log.i("INFO", "����copy�����ļ�(���滻�Ѵ��ڵ��ļ�)");
			copyAssetsFile(context, fromAssetsDir, toJarDir, toSoDir, false);
		}
		return isReCopy;
	}

}
