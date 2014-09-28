package com.jike.supercollection.update;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.w3c.dom.Text;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jike.supercollection.MyApp;
import com.jike.supercollection.R;


public class UpdateManager
{
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	protected static final int UPDATE = 3;
	protected static final int NOUPDATE = 4;
//	/* 保存解析的XML信息 */
//	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	private String nameOfNode;
	
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private Thread mThread;
	private UpdateNode myNode;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};
	
	private Handler isUpdateHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case UPDATE:
//				showNoticeDialog();
				showCustomerNoticeDialog();
				break;
			case NOUPDATE:
				
				break;
			default:
				break;
			}
		};
	};
	
	public UpdateManager(Context context,String nameOfNode)
	{
		this.mContext = context;
		this.nameOfNode=nameOfNode;
	}


	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

//	
	private void showCustomerNoticeDialog()
	{
		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		final Dialog noticeDialog = builder.create();
		noticeDialog.setCancelable(false);
		noticeDialog.show();
		
		noticeDialog.getWindow().setContentView(R.layout.softupdate_notificationdialog);
		noticeDialog.getWindow().findViewById(R.id.update_now_tv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				noticeDialog.dismiss();
				// 显示下载对话框
				showDownloadDialog();
			}
		});
		noticeDialog.getWindow().findViewById(R.id.update_later_tv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				noticeDialog.dismiss();
			}
		});
		TextView dialog_content_tv=(TextView)noticeDialog.getWindow().findViewById(R.id.dialog_content_tv);
		dialog_content_tv.setText(myNode.getContent().replace("\\r\\n", "\r\n"));
		
		}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog()
	{
		downloadApk();//调到前面
		
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 设置取消状态			设置下载时是否可取消
				cancelUpdate = false;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.setCancelable(false);
		mDownloadDialog.show();
		// 现在文件
//		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 */
	public class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(myNode.getDownload_url());
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath,myNode.getSoftname());
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, myNode.getSoftname());
		if (!apkfile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
	
	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlStr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public InputStream getInputStreamFromUrl(String urlStr)throws MalformedURLException, IOException {
	    URL url = new URL(urlStr);
	    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	    InputStream inputStream = urlConn.getInputStream();
	    return inputStream;
	}
	
	//获取服务器上xml文件的内容
	protected String sendPost() {
		HttpURLConnection uRLConnection = null;
		InputStream is = null;
		BufferedReader buffer = null;
		String result = null;
		try {
			MyApp ma = new MyApp(mContext);
			URL url = new URL(ma.getUpdateServeUrl());
			uRLConnection = (HttpURLConnection) url.openConnection();
//			uRLConnection.setDoInput(true);
//			uRLConnection.setDoOutput(true);
			uRLConnection.setRequestMethod("GET");
			uRLConnection.setUseCaches(false);
			uRLConnection.setConnectTimeout(10 * 1000);
			uRLConnection.setReadTimeout(10 * 1000);
			uRLConnection.setInstanceFollowRedirects(false);
			uRLConnection.setRequestProperty("Connection", "Keep-Alive");
			uRLConnection.setRequestProperty("Charset", "UTF-8");
			uRLConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");
			uRLConnection
					.setRequestProperty("Content-Type", "application/xml");
			uRLConnection.connect();

			is = uRLConnection.getInputStream();

			String content_encode = uRLConnection.getContentEncoding();

			if (null != content_encode && !"".equals(content_encode)
					&& content_encode.equals("gzip")) {
				is = new GZIPInputStream(is);
			}
			buffer = new BufferedReader(new InputStreamReader(is));
			StringBuilder strBuilder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				strBuilder.append(line);
			}
			result = strBuilder.toString();
		} catch (Exception e) {
			Log.e("UpadteManager", "http post error", e);
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (uRLConnection != null) {
				uRLConnection.disconnect();
			}
		}
		return result;
	}
	
	public void checkForUpdates() {
		mThread = new Thread() {
			@Override
			public void run() {
				// if (isNetworkAvailable(mContext)) {
				String info = sendPost();
				if (info != null) {
//					parseJson(info);
					try {
						myNode=ParseXmlService.getNodeFromXml(info, nameOfNode);
						parseXml(myNode);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Log.e("UpdateManager", "can't get app update info");
				}
			}

		};
		mThread.start();
	}
	
	/*
	 * 从节点中获取信息并显示更新对话框，或返回没有更新结果false
	 */
	private Boolean parseXml(UpdateNode myNode) {
		mThread.interrupt();
		Looper.prepare();
		try {
			String updateMessage = myNode.getContent();
			String apkUrl = myNode.getDownload_url();
			int apkCode = myNode.getVersionCode();// 以后的版本按照VersionCode更新，避免手机上显示的版本过高
			String apkVersionName = myNode.getVersion();// 老版本是按Version号更新的，如8.0.0

			int versionCode = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionCode;
			String versionName = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionName;

			if (apkCode > versionCode) {//|| apkVersionName.compareTo(versionName) > 0
				Message msg=new Message();
				msg.what=UPDATE;
				isUpdateHandler.sendMessage(msg);
				return true;
			} else {
				Message msg=new Message();
				msg.what=NOUPDATE;
				isUpdateHandler.sendMessage(msg);
				return false;
			}

		} catch (PackageManager.NameNotFoundException ignored) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
