package cg.yunbee.cn.wangyoujar.pojo;

import java.util.List;

public class PluginInfo {
	private String plugin_type;
	private String name;
	private int version;
	private List<String> jarUris;
	private List<String> soUris;
	public String getPlugin_type() {
		return plugin_type;
	}
	public void setPlugin_type(String plugin_type) {
		this.plugin_type = plugin_type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public List<String> getJarUris() {
		return jarUris;
	}
	public void setJarUris(List<String> jarUris) {
		this.jarUris = jarUris;
	}
	public List<String> getSoUris() {
		return soUris;
	}
	public void setSoUris(List<String> soUris) {
		this.soUris = soUris;
	}
	
	
	
}
