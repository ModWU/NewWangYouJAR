package cg.yunbee.cn.wangyoujar.pojo;

public enum NetState {
	CLOSE{
		public String getState(){
			return "CLOSE";
		}
	},WIFI{
		public String getState(){
			return "WIFI";
		}
	},MOBILE{
		public String getState(){
			return "MOBILE";
		}
	},UNKNOWN{
		public String getState(){
			return "UNKNOWN";
		}
	};
	
	public abstract String getState();
}
