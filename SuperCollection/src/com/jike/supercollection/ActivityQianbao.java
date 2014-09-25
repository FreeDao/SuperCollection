package com.jike.supercollection;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class ActivityQianbao extends Activity  implements
RefreshListView.IOnRefreshListener, RefreshListView.IOnLoadMoreListener{
	
	private LinearLayout shoukuan_ll,jiaoyijilu_ll,jiaoyijilu_block_ll,shoukuan_block_ll;
	private TextView dangqianyue_tv,shijidaozhangjine_tv,shouxufei_tv;
	private EditText shoukuanjine_et;
	private SharedPreferences sp;
	private Button ok_button;
	private RefreshListView listview;
	private RelativeLayout T0_rl,T3_rl,T6_rl;
	private ImageView T0_select_imgbtn,T3_select_imgbtn,T6_select_imgbtn,sk_iv,jyjl_iv;
	private float rate=0.006f,realamount;
	private int time=6,records=0,curentPage=1;
	private Drawable selectedDrawable, unselectedDrawable;
	Context context;
	private CustomProgressDialog progressdialog;
	private String recordReturnJson="";
	private ArrayList<Record> records_list;
	ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qianbao);
		initView();
	}
	
	private void initView() {
		context=this;
		sp = getSharedPreferences(SPkeys.SPNAME.getString(), 0);
		records_list=new ArrayList<Record>();
		selectedDrawable = context.getResources().getDrawable(
				R.drawable.slqb_time_cur_select);
		unselectedDrawable = context.getResources().getDrawable(
				R.drawable.slqb_time_select);
		shoukuan_ll=(LinearLayout) findViewById(R.id.shoukuan_ll);
		jiaoyijilu_ll=(LinearLayout) findViewById(R.id.jiaoyijilu_ll);
		dangqianyue_tv=(TextView) findViewById(R.id.dangqianyue_tv); 
		shijidaozhangjine_tv=(TextView) findViewById(R.id.shijidaozhangjine_tv); 
		shouxufei_tv=(TextView) findViewById(R.id.shouxufei_tv);
		shoukuanjine_et=(EditText) findViewById(R.id.shoukuanjine_et);
		ok_button=(Button) findViewById(R.id.ok_button);
		T0_select_imgbtn=(ImageView) findViewById(R.id.T0_select_imgbtn);
		T3_select_imgbtn=(ImageView) findViewById(R.id.T3_select_imgbtn);
		T6_select_imgbtn=(ImageView) findViewById(R.id.T6_select_imgbtn);
		sk_iv=(ImageView) findViewById(R.id.sk_iv);
		jyjl_iv=(ImageView) findViewById(R.id.jyjl_iv);
		listview=(RefreshListView) findViewById(R.id.listview);
		jiaoyijilu_block_ll=(LinearLayout) findViewById(R.id.jiaoyijilu_block_ll);
		shoukuan_block_ll=(LinearLayout) findViewById(R.id.shoukuan_block_ll); 
		T0_rl=(RelativeLayout) findViewById(R.id.T0_rl);
		T3_rl=(RelativeLayout) findViewById(R.id.T3_rl);
		T6_rl=(RelativeLayout) findViewById(R.id.T6_rl);
		jiaoyijilu_ll.setOnClickListener(clickListener);
		shoukuan_ll.setOnClickListener(clickListener);
		T0_select_imgbtn.setOnClickListener(clickListener);
		T3_select_imgbtn.setOnClickListener(clickListener);
		T6_select_imgbtn.setOnClickListener(clickListener);
		T0_rl.setOnClickListener(clickListener);
		T3_rl.setOnClickListener(clickListener);
		T6_rl.setOnClickListener(clickListener);
		ok_button.setOnClickListener(clickListener);
		
		dangqianyue_tv.setText(sp.getString(SPkeys.amount.getString(), "0"));
	}
	
	OnClickListener clickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.shoukuan_ll:
				jiaoyijilu_block_ll.setVisibility(View.GONE);
				shoukuan_block_ll.setVisibility(View.VISIBLE);
				sk_iv.setBackground(getResources().getDrawable(R.drawable.slqb_shoukuan_blue_btn));
				jyjl_iv.setBackground(getResources().getDrawable(R.drawable.slqb_jilu_gray_btn));
				break;
			case R.id.jiaoyijilu_ll:
				jiaoyijilu_block_ll.setVisibility(View.VISIBLE);
				shoukuan_block_ll.setVisibility(View.GONE);
				sk_iv.setBackground(getResources().getDrawable(R.drawable.slqb_shoukuan_gray_btn));
				jyjl_iv.setBackground(getResources().getDrawable(R.drawable.slqb_jilu_blue_btn));
				startQueryRecord();
				break;
			case R.id.T0_rl:
			case R.id.T0_select_imgbtn:
				rate=0.006f;
				time=0;
				T0_select_imgbtn.setBackground(selectedDrawable);
				T3_select_imgbtn.setBackground(unselectedDrawable);
				T6_select_imgbtn.setBackground(unselectedDrawable);
				break;
			case R.id.T3_rl:
			case R.id.T3_select_imgbtn:
				rate=0.003f;
				time=3;
				T3_select_imgbtn.setBackground(selectedDrawable);
				T0_select_imgbtn.setBackground(unselectedDrawable);
				T6_select_imgbtn.setBackground(unselectedDrawable);
				break;
			case R.id.T6_rl:
			case R.id.T6_select_imgbtn:
				rate=0f;
				time=0;
				T6_select_imgbtn.setBackground(selectedDrawable);
				T3_select_imgbtn.setBackground(unselectedDrawable);
				T0_select_imgbtn.setBackground(unselectedDrawable);
				break;
			case R.id.ok_button:
				if (shoukuanjine_et.getText().toString().trim().length()==0) {
					new AlertDialog.Builder(context).setTitle("请输入收款金额！").setPositiveButton("确认", null).show();
					break;
				}
				realamount=rate*(Float.valueOf(shoukuanjine_et.getText().toString().trim()));
				String userid=sp.getString(SPkeys.userid.getString(), "");
				String siteid=sp.getString(SPkeys.siteid.getString(), "");
				//amount + time + realamount + userid + "_superpay";
				String sign=CommonFunc.MD5(shoukuanjine_et.getText().toString().trim() +time+realamount+ userid + "_superpay");
				MyApp ma = new MyApp(context);
				String url=String.format(ma.getPayServeUrl(), shoukuanjine_et.getText().toString().trim(),time,realamount,userid,siteid,sign);
				Intent intent=new Intent(context,Activity_Web_Pay.class);
				intent.putExtra(Activity_Web_Pay.URL, url);
				intent.putExtra(Activity_Web_Pay.TITLE, "无卡收款");
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};
	
	private void startQueryRecord() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// action=flist&str={'s':'sha','e':hfe,'sd':'2014-01-28','userid':'649','siteid':'65'}
				MyApp ma = new MyApp(context);
				String str = "{\"currpage\":\"" + curentPage + "\",\"pagesize\":\""
						+ 20 + "\",\"userid\":\"" + sp.getString(SPkeys.userid.getString(), "")
						+ "\",\"siteid\":\""  +sp.getString(SPkeys.siteid.getString(), "") + "\"}";
				String param = "action=suppayrecords&str=" + str + "&userkey="
						+ MyApp.userkey + "&sitekey=" + MyApp.sitekey
						+ "&sign="
						+ CommonFunc.MD5(MyApp.userkey + "suppayrecords" + str);
				recordReturnJson = HttpUtils.getJsonContent(ma.getServeUrl(),
						param);
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}).start();

		progressdialog = CustomProgressDialog.createDialog(context);
		progressdialog.setMessage("正在查询交易信息，请稍候...");
		progressdialog.setCancelable(false);
		progressdialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		progressdialog.show();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				JSONTokener jsonParser;
				jsonParser = new JSONTokener(recordReturnJson);
				try {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					String state = jsonObject.getString("c");

					if (state.equals("0000")) {
						createList(jsonObject.getJSONObject("d").getJSONArray("o"));
						records=jsonObject.getJSONObject("d").getInt("records");
						adapter = new ListAdapter(context, records_list);
						listview.setAdapter(adapter);
						if (records_list.size() == 0)
							new AlertDialog.Builder(context)
									.setTitle("未查交易记录信息")
									.setPositiveButton("确认", null).show();
						listview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								Record ql = records_list
										.get(position - 1);
								Intent intents = new Intent(context,
										ActivityRecordState.class);
								intents.putExtra(
										ActivityRecordState.RECORDINFO,
										JSONHelper.toJSON(ql));
								startActivity(intents);
							}
						});

					} else {
						String message = jsonObject.getString("msg");
						new AlertDialog.Builder(context).setTitle("查询失败")
								.setMessage(message)
								.setPositiveButton("确认", null).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				progressdialog.dismiss();
				break;
			}
		}

	};
	/**
	 * 构建list对象
	 * 
	 * @param flist_list
	 */
	private void createList(JSONArray flist_list) {
		for (int i = 0; i < flist_list.length(); i++) {
			try {
				Record hb = JSONHelper.parseObject(
						flist_list.getJSONObject(i), Record.class);
				records_list.add(hb);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ListAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;
		private List<Record> recordList;
		
		public ListAdapter(Context context, List<Record> list1) {
			this.inflater = LayoutInflater.from(context);
			this.recordList = list1;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public void refreshData(List<Record> data) {
			this.recordList = data;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.item_record_list, null);
			}
			TextView orderid_tv = (TextView) convertView
					.findViewById(R.id.orderid_tv);
			TextView bank_tv = (TextView) convertView
					.findViewById(R.id.bank_tv);
			TextView bankcard_tv = (TextView) convertView
					.findViewById(R.id.bankcard_tv);
			TextView receiveTime_tv = (TextView) convertView
					.findViewById(R.id.receiveTime_tv);
			TextView daozhangTime_tv = (TextView) convertView
					.findViewById(R.id.daozhangTime_tv);
			TextView state_tv = (TextView) convertView
					.findViewById(R.id.state_tv);
			TextView money_tv = (TextView) convertView
					.findViewById(R.id.money_tv);
			TextView time1_name_tv = (TextView) convertView
					.findViewById(R.id.time1_name_tv);
			TextView time2_name_tv = (TextView) convertView
					.findViewById(R.id.time2_name_tv);
			
//			orderid_tv.setText();
//			bank_tv.setText();
//			bankcard_tv.setText();
//			receiveTime_tv.setText();
//			daozhangTime_tv.setText();
//			state_tv.setText();
//			money_tv.setText();
//			time1_name_tv.setText();
//			time2_name_tv.setText();
			
			return convertView;
		}
	}
	
	@Override
	public void OnLoadMore() {
		LoadMoreDataAsynTask mLoadMoreAsynTask = new LoadMoreDataAsynTask();
		mLoadMoreAsynTask.execute();
	}

	@Override
	public void OnRefresh() {
		RefreshDataAsynTask mRefreshAsynTask = new RefreshDataAsynTask();
		mRefreshAsynTask.execute();
	}

	class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(3000);
				curentPage++;
				startQueryRecord();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.refreshData(records_list);
			listview.onRefreshComplete();
		}
	}

	class LoadMoreDataAsynTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(3000);
				curentPage++;
				startQueryRecord();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.refreshData(records_list);
			if (records_list.size() == Integer.valueOf(records)) {
				listview.onLoadMoreComplete(true);
			} else {
				listview.onLoadMoreComplete(false);
			}
		}
	}

}
