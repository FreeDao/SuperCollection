package com.jike.supercollection;


/**记录SharedPreferences中的键值
 * */
public enum SPkeys {
	SPNAME("mySP_SuperCollection"),//SharedPreferences的名字
	
	userid("userid"),
	username("username"),
	mobile("mobile"),
	unavailableamount("unavailableamount"),
	amount("amount"),//虚拟账号金额
	siteid("siteid"),//系统id
	autoLogin("autoLogin"),
	loginState("loginState"),
	lastusername("lastusername"),//记录登录时用的用户名密码
	lastpassword("lastpassword"),
	;

	private String key;

	private SPkeys(String s) {
		key = s;
	}

	public String getString() {
		return key;
	}
};
