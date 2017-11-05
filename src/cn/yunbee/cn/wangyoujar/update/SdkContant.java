package cn.yunbee.cn.wangyoujar.update;

public class SdkContant {
	
	
	
	public enum LIBRARY_TYPE {
		
		NULL, SO, JAR, DEX, APK;
		
		
		private boolean eq(String eq1) {
			if(name().equalsIgnoreCase(eq1)) return true;
			return false;
		}
		
		public static String getTypeStr(String libname) {
			String strType = "";
			int lastIndex = libname.lastIndexOf(".");
			if(lastIndex > 0) {
				strType = libname.substring(lastIndex + 1, libname.length());
				if(!SO.eq(strType) && !JAR.eq(strType) && !DEX.eq(strType) && !APK.eq(strType))
					strType = "";
			}
			return strType;
		}
		
		public static LIBRARY_TYPE getType(String libname) {
			LIBRARY_TYPE libType = NULL;
			String typeStr = getTypeStr(libname);
			if("so".equalsIgnoreCase(typeStr)) {
				libType = SO;
			} else if("jar".equalsIgnoreCase(typeStr)) {
				libType = JAR;
			} else if("dex".equalsIgnoreCase(typeStr)) {
				libType = DEX;
			} else if("apk".equalsIgnoreCase(typeStr)) {
				libType = APK;
			} else {
				libType = NULL;
			}
			return libType;
		}
		
		
		
		public static String getDex(String libname) {
			String strdex = "";
			int lastIndex = libname.lastIndexOf(".");
			if(lastIndex > 0) {
				strdex = libname.substring(0, lastIndex);
				strdex = strdex + ".dex";
			}
			return strdex;
		}
	}
	
	public enum SDK_TYPE {
		
		NULL, WANGYOU, AIBEI, ALIPAY, JXHY, QRCODE, WEIXIN, WFT, YHXF, YST;
		
		public static String getStr(SDK_TYPE type) {
			String strType = (type == null ? "" : type.name());
			return strType;
		}
		
		public static SDK_TYPE getType(int typeCode) {
			int index = (typeCode < NULL.ordinal() || typeCode > YST.ordinal()) ? 0 : typeCode;
			return SDK_TYPE.values()[index];
		}
		
		public static SDK_TYPE getType(String sdkName) {
			SDK_TYPE sdkType = NULL;
			for(int i = NULL.ordinal(); i <= YST.ordinal(); i++) {
				if(getType(i).name().equalsIgnoreCase(sdkName)) {
					sdkType = getType(i);
					break;
				}
			}
			
			return sdkType;
		}
	}
	
	public static final class AddressUrl {
		public static final String UPDATE_URL = "http://api.okpaysdk.com/Update/SDKDetail.ashx";//外网
		
		//public static final String UPDATE_URL = "http://192.168.1.19:8902/Update/SDKDetail.ashx";//内网
		
		//http://192.168.1.19:8902/
		
		//public static final String UPDATE_URL_WANGYOU = "";//外网
		//public static final String UPDATE_URL_SDK3 = "";//外网
	}
	
	public static final class WangyouLibs {
		public static final String LIB_IMPL = "dywangyouImpl.jar";
		public static final String LIB_SUPPORT = "supportLibs.jar";
	}
	
	public static final class AibeiLibs {
		public static final String LIB_IMPL = "dycomptSDKimpl_dex.jar";
		public static final String LIB_SUPPORT = "iapppay_plugin_dex.jar";
	}
	
	public static final class AlipayLibs {
		public static final String LIB_IMPL = "dycomptSDKimpl_dex.jar";
		public static final String LIB_SUPPORT = "alipaySdk-20160825_dex.jar";
	}
	
	public static final class JxhyLibs {
		public static final String LIB_IMPL = "dycomptSDKimpl_dex.jar";
		public static final String LIB_SUPPORT1 = "JshyPay_dex.jar";
		public static final String LIB_SUPPORT2 = "JshyPayApp_games_dex.jar";
		public static final String LIB_SUPPORT3 = "libammsdk_dex.jar";
	}
	
	public static final class YstLibs {
		public static final String LIB_IMPL = "dycomptSDKimpl_dex.jar";
		public static final String LIB_SUPPORT = "yst_dex.jar";
	}
	
	public static final class WeixinLibs {
		public static final String LIB_IMPL = "weixin_dycomptSDKimpl_dex.jar";
		public static final String LIB_SUPPORT = "weixin_libammsdk_dex.jar";
	}
	
	public static final class WftLibs {
		public static final String LIB_IMPL = "wft_dycomptSDKimpl_dex.jar";
		public static final String LIB_SUPPORT = "wft_wftsdk4.1_dex.jar";
	}
	
	public static final class YhxfLibs {
		public static final String LIB_IMPL = "yhxf_dycomptSDKimpl_dex.jar";
		public static final String LIB_SUPPORT = "yhxf_VsofoWapPay_V1.0.1.6_dex.jar";
	}
	
}
