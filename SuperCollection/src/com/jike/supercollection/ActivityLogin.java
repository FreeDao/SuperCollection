package com.jike.supercollection;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class ActivityLogin extends Activity {
	
	private EditText uername_input_et,password_input_et;
	private Button login_btn;
	private Context context;
	private ImageView autologin_checkbox_iv;
	private RelativeLayout autologin_rl;
	private Boolean auto = true;
	private Drawable checkedDrawable, uncheckedDrawable;
	private SharedPreferences sp;
	private String loginReturnJson;// 登录验证后返回的结果数据
	private CustomProgressDialog progressdialog;
	private Boolean isTimeout=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		context=this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		auto = sp.getBoolean(SPkeys.autoLogin.getString(), true);
		if (sp.getBoolean(SPkeys.loginState.getString(), false)) {
			startActivity(new Intent(context,ActivityQianbao.class));
			return;
		}
		checkedDrawable = context.getResources().getDrawable(
				R.drawable.fuxuankuang_yes);
		uncheckedDrawable = context.getResources().getDrawable(
				R.drawable.fuxuankuang_no);
		uername_input_et=(EditText) findViewById(R.id.uername_input_et);
		password_input_et=(EditText) findViewById(R.id.password_input_et);
		login_btn=(Button) findViewById(R.id.login_btn);
		autologin_rl=(RelativeLayout) findViewById(R.id.autologin_rl);
		autologin_checkbox_iv=(ImageView) findViewById(R.id.autologin_checkbox_iv);
		login_btn.setOnClickListener(loginClickListener);
		autologin_rl.setOnClickListener(autologinClickListener);
		uername_input_et.setText(sp.getString(SPkeys.lastusername.getString(), ""));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (sp.getBoolean(SPkeys.loginState.getString(), false)) {
//			startActivity(new Intent(context,ActivityQianbao.class));
			finish();
			return;
		}
	}

	View.OnClickListener loginClickListener =new OnClickListener() {
		public void onClick(View v) {
			if (uername_input_et.getText().toString().trim().length() == 0) {
				new AlertDialog.Builder(context).setTitle("用户名不能为空")
						.setMessage("请输入用户名").setPositiveButton("确定", null)
						.show();
				return;
			}
			if (password_input_et.getText().toString().trim().length() == 0) {
				new AlertDialog.Builder(context).setTitle("密码不能为空")
						.setMessage("请输入密码").setPositiveButton("确定", null)
						.show();
				return;
			}
			if (HttpUtils.showNetCannotUse(context)) {
				return;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					MyApp ma = new MyApp(context);
					String str = "{\"username\":\""
							+ uername_input_et.getText().toString().trim()
							+ "\",pwd:\""
							+ password_input_et.getText().toString().trim()+"\"}";
					String param = "action=suppaylogin&sitekey=&userkey="
							+ MyApp.userkey
							+ "&str="
							+ str
							+ "&sign="
							+ CommonFunc.MD5(MyApp.userkey + "suppaylogin"
									+ str);
					loginReturnJson = HttpUtils.getJsonContent(
							ma.getServeUrl(), param);
					Log.v("loginReturnJson", loginReturnJson);
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
					handler.sendEmptyMessageDelayed(2, 3000);
				}
			}).start();
			progressdialog = CustomProgressDialog.createDialog(context);
			progressdialog.setMessage("正在登录，请稍候...");
			progressdialog.setCancelable(true);
			progressdialog.show();
		}
	};

	View.OnClickListener autologinClickListener =new OnClickListener() {
		@SuppressLint("NewApi")
		public void onClick(View v) {
			auto = !auto;
			if (auto) {
				autologin_checkbox_iv.setBackground(checkedDrawable);
			} else {
				autologin_checkbox_iv.setBackground(uncheckedDrawable);
			}
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				if(isTimeout){
					new AlertDialog.Builder(context).setTitle("请求超时，请稍后再试！")
					.setPositiveButton("确认", null).show();
					progressdialog.dismiss();
				}
				break;
			case 1:// 获取登录返回的数据
				isTimeout=false;
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(loginReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						String content = jsonObject.getString("d");
						sp.edit()
								.putBoolean(SPkeys.autoLogin.getString(), auto)
								.commit();
						// 以下代码将用户信息反序列化到SharedPreferences中
						UserInfo user = JSONHelper.parseObject(content,UserInfo.class);
						sp.edit().putString(SPkeys.lastusername.getString(),uername_input_et.getText().toString().trim()).commit();
						sp.edit().putString(SPkeys.lastpassword.getString(),password_input_et.getText().toString().trim()).commit();
						sp.edit().putString(SPkeys.userid.getString(),user.getUserid()).commit();
						sp.edit().putString(SPkeys.username.getString(),user.getUsername()).commit();
						sp.edit().putString(SPkeys.amount.getString(),user.getAmount()).commit();
						sp.edit().putString(SPkeys.siteid.getString(),user.getSiteid()).commit();
						sp.edit().putString(SPkeys.mobile.getString(),user.getMobile()).commit();
						sp.edit().putString(SPkeys.unavailableamount.getString(),user.getUnavailableamount()).commit();
						//其他信息以后用时再增加
						// 登录后将登录状态置为true
						sp.edit().putBoolean(SPkeys.loginState.getString(), true)
								.commit();
						startActivity(new Intent(context,ActivityQianbao.class));
						ActivityLogin.this.finish();
					} else {
						new AlertDialog.Builder(context).setTitle("登录失败")
								.setPositiveButton("确认", null).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				progressdialog.dismiss();
				break;
			}
		}
	};

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
