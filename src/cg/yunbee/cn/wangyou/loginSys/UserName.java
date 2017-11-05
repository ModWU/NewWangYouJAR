package cg.yunbee.cn.wangyou.loginSys;

public class UserName {
	private String content;
	private String left = "";
	private String center = "";
	private String right = "";
	private String prefixString = "";
	
	@Override
	public int hashCode() {
		return content.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof UserName) {
			UserName other = (UserName) o;
			return other.getContent() == null ? false : other.getContent().equals(content);
		}
		
		return false;
	}
	
	public UserName(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	public String getRight() {
		return right;
	}
	public void setRight(String right) {
		this.right = right;
	}
	
	
	
	public String getPrefixString() {
		return prefixString;
	}

	public void setPrefixString(String prefixString) {
		this.prefixString = prefixString;
	}

	public void setEmpty() {
		if(left == null) left = "";
		if(center == null) center = "";
		if(right == null) center = "";
		if(prefixString == null) prefixString = "";
	}
	
	
	
	
}
