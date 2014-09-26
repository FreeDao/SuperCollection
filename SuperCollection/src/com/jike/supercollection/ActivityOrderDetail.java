package com.jike.supercollection;

import org.json.JSONException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityOrderDetail extends Activity {
	public static final int JILU = 2;
	private ImageButton back_imgbtn;
	private ImageView finish_iv;
	private TextView state_tv, bankAndBankcard_tv, shoukuanjine_tv,
			shouxufei_tv, dangqianzhuangtai_tv, dingdanbianhao_tv,
			daozhangleixing_tv, time1_tv, time2_tv, time1_name_tv,
			time2_name_tv;
	private Context context;
	private RelativeLayout state_detail_rl;
	private Record record;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orderdetail);

		context = this;
		back_imgbtn = (ImageButton) findViewById(R.id.back_imgbtn);
		finish_iv = (ImageView) findViewById(R.id.finish_iv);
		state_detail_rl = (RelativeLayout) findViewById(R.id.state_detail_rl);
		state_tv = (TextView) findViewById(R.id.state_tv);
		bankAndBankcard_tv = (TextView) findViewById(R.id.bankAndBankcard_tv);
		shoukuanjine_tv = (TextView) findViewById(R.id.shoukuanjine_tv);
		shouxufei_tv = (TextView) findViewById(R.id.shouxufei_tv);
		dangqianzhuangtai_tv = (TextView) findViewById(R.id.dangqianzhuangtai_tv);
		dingdanbianhao_tv = (TextView) findViewById(R.id.dingdanbianhao_tv);
		daozhangleixing_tv = (TextView) findViewById(R.id.daozhangleixing_tv);
		time1_tv = (TextView) findViewById(R.id.time1_tv);
		time2_tv = (TextView) findViewById(R.id.time2_tv);
		time1_name_tv = (TextView) findViewById(R.id.time1_name_tv);
		time2_name_tv = (TextView) findViewById(R.id.time2_name_tv);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(ActivityRecordState.RECORDINFO)) {
				try {
					record = JSONHelper.parseObject(
							bundle.getString(ActivityRecordState.RECORDINFO),
							Record.class);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		back_imgbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityOrderDetail.this.finish();
			}
		});
		state_detail_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intents = new Intent(context,
						ActivityRecordState.class);
				intents.putExtra(
						ActivityRecordState.RECORDINFO,
						JSONHelper.toJSON(record));
				startActivity(intents);
			}
		});

		String state = record.getS();
//		state_tv.setText(state);
		String bank = record.getBn();
		String bankCard = record.getBc();
		dangqianzhuangtai_tv.setText(state);
		shoukuanjine_tv.setText("￥" +record.getA());
		shouxufei_tv.setText("手续费￥" + record.getPf());
		dingdanbianhao_tv.setText(record.getNo());
		daozhangleixing_tv.setText("T+" + record.getSr());
		bankAndBankcard_tv
				.setText((bank.length() > 3 ? (bank.substring(0, 4) + "...")
						: "")
						+ (bankCard.length() > 4 ? (bankCard.substring(
								bankCard.length() - 4, bankCard.length())) : ""));
		if (state.equals(StateEnum.neworder.getString())) {
			time1_tv.setText(record.getSt().replace("null", ""));
			time1_name_tv.setText("到账时间");
			time2_tv.setText(record.getPt().replace("null", ""));
			time2_name_tv.setText("付款成功");
			dangqianzhuangtai_tv.setTextColor(getResources().getColor(
					R.color.blue));
		} else if (state.equals(StateEnum.yishoukuan.getString())) {
			time1_tv.setText(record.getSt().replace("null", ""));
			time1_name_tv.setText("到账时间");
			time2_tv.setText(record.getPt());
			time2_name_tv.setText("收款时间");
			dangqianzhuangtai_tv.setTextColor(getResources().getColor(
					R.color.blue));
		} else if (state.equals(StateEnum.ruzhangzhong.getString())) {
			time1_tv.setText(record.getSt().replace("null", ""));
			time1_name_tv.setText("到账时间");
			time2_tv.setText(record.getSr());
			time2_name_tv.setText("入账时间");
			dangqianzhuangtai_tv.setTextColor(getResources().getColor(
					R.color.green));
		} else if (state.equals(StateEnum.yiwancheng.getString())) {
			time1_tv.setText(record.getSt().replace("null", ""));
			time1_name_tv.setText("到账时间");
			time2_tv.setText(record.getSr());
			time2_name_tv.setText("入账时间");
			finish_iv.setVisibility(View.VISIBLE);
			dangqianzhuangtai_tv.setTextColor(getResources().getColor(
					R.color.red));
		} else if (state.equals(StateEnum.yiquxiao.getString())) {
			time1_tv.setText(record.getCt().replace("null", ""));
			time1_name_tv.setText("创单时间");
			time2_tv.setText(record.getQt().replace("null", ""));
			time2_name_tv.setText("取消时间");
			dangqianzhuangtai_tv.setTextColor(getResources().getColor(
					R.color.red));
		}
	}

}
