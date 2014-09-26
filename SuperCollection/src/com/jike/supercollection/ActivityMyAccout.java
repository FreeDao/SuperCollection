package com.jike.supercollection;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.renderscript.Int2;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityMyAccout extends Activity {
	public static final int JILU=2;
	private ImageButton back_iv;
	private Button logout_button;
	private TextView username_tv,phone_tv,dangqianyue_tv,bukeyongyue_tv ;
	private SharedPreferences sp;
	private Context context;
	private RelativeLayout jiaoyijilu_rl;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myaccount);

		context=this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		back_iv = (ImageButton) findViewById(R.id.back_imgbtn);
		back_iv.setOnClickListener(btnClickListner);
		logout_button=(Button) findViewById(R.id.logout_button);
		logout_button.setOnClickListener(btnClickListner);
		
		username_tv=(TextView) findViewById(R.id.username_tv);
		dangqianyue_tv=(TextView) findViewById(R.id.dangqianyue_tv);
		bukeyongyue_tv=(TextView) findViewById(R.id.bukeyongyue_tv);
		phone_tv=(TextView) findViewById(R.id.phone_tv);
		
		username_tv.setText(sp.getString(SPkeys.username.getString(), ""));
		dangqianyue_tv.setText("￥"+sp.getString(SPkeys.amount.getString(), ""));
		phone_tv.setText(sp.getString(SPkeys.mobile.getString(), ""));
		if(!sp.getString(SPkeys.unavailableamount.getString(), "").isEmpty())
			bukeyongyue_tv.setText("￥"+sp.getString(SPkeys.unavailableamount.getString(), ""));
		bukeyongyue_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		((RelativeLayout) findViewById(R.id.jiaoyijilu_rl)).setOnClickListener(btnClickListner);
	}

	View.OnClickListener btnClickListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_imgbtn:
				ActivityMyAccout.this.finish();
				break;
			case R.id.jiaoyijilu_rl:
				setResult(JILU, getIntent().putExtra("operation", "查看交易记录"));
				ActivityMyAccout.this.finish();
				break;
			case R.id.logout_button:
				new AlertDialog.Builder(context).setTitle("确认注销").setNegativeButton("取消", null)
				.setMessage("确认注销当前用户？").setPositiveButton("注销", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						sp.edit().putString(SPkeys.userid.getString(),"").commit();
						sp.edit().putString(SPkeys.username.getString(),"").commit();
						sp.edit().putBoolean(SPkeys.loginState.getString(), false).commit();
						startActivity(new Intent(context,ActivityLogin.class));
						ActivityMyAccout.this.finish();
					}
				}).show();
				break;
			default:
				break;
			}
		}
	};

}
