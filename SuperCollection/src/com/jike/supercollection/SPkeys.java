package com.jike.supercollection;


/**��¼SharedPreferences�еļ�ֵ
 * */
public enum SPkeys {
	SPNAME("mySP_SuperCollection"),//SharedPreferences������
	
	userid("userid"),
	username("username"),
	mobile("mobile"),
	unavailableamount("unavailableamount"),
	amount("amount"),//�����˺Ž��
	siteid("siteid"),//ϵͳid
	autoLogin("autoLogin"),
	loginState("loginState"),
	lastusername("lastusername"),//��¼��¼ʱ�õ��û�������
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
