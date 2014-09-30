package com.jike.supercollection;

import java.util.HashMap;

import android.content.Context;

//百度地图 SHA1 0D:3C:EC:2E:C2:01:A0:E6:C7:AE:44:B4:05:17:9D:F8:BE:A9:70:9E
public class MyApp {
	private Context context;
	
	HashMap<String,Object> self_hm=new HashMap<String,Object>();//际珂B2C
	HashMap<String,Object> self_b_hm=new HashMap<String,Object>();//际珂B2B
	HashMap<String,Object> nanbei_hm=new HashMap<String,Object>();
	HashMap<String,Object> nanbei_b_hm=new HashMap<String,Object>();
	HashMap<String,Object> menghang_hm=new HashMap<String,Object>();
	HashMap<String,Object> menghang_b_hm=new HashMap<String,Object>();
	
//	HashMap的使用		正式接口测试账号 wzxiang06  898989
//	put(K key, V value) 
//	hm.put(a,b); //插入值为b,key值为a
//	hm.get(key); //返回值为value
	/**打包不同程序时更改此处
	 * 此类中只需更改以下三个值：RELEASE、hm、platform
	 */
	public static boolean RELEASE = true;//测试  or 发布，接口
	private HashMap<String,Object> hm=self_hm;
	
	/*正式 ffdd14d2e6c26b70749c8b2c08067c69
	 *测试 5b13658a9fc945e34893f806027d467a
	 * */
	public static String userkey="ffdd14d2e6c26b70749c8b2c08067c69";
	public static String sitekey="";
	
	public MyApp(Context context){
		this.context=context;
	}
	
	public boolean isRELEASE() {
		return RELEASE;
	}
	
	public HashMap<String, Object> getHm() {
		return hm;
	}
	
	
	/**获取接口的地址
	 */
	public String getServeUrl() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_server_url);
		else return context.getResources().getString(R.string.test_server_url);
	}
	/**获取支付接口的地址
	 */
	public String getPayServeUrl() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_pay_server_url);
		else return context.getResources().getString(R.string.test_pay_server_url);
	}
	/**获取update接口的地址
	 */
	public String getUpdateServeUrl() {
		if(RELEASE)
			return context.getResources().getString(R.string.formal_update_url);
		else return context.getResources().getString(R.string.test_update_url);
	}
}
