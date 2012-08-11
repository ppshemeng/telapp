package com.cn.telapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ContextUtil {

	private Context context;
	public  int SCREEN_WIDTH;
	public  int SCREEN_HEIGHT;
	public  int SCREEN_DPI;
	
	public ContextUtil(Context context) {
		this.context = context;
		initScreenData();
	}
	
	public void noTitleBar()
	{
		((Activity)context).requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public  void setFullScreen()
	{
		//����ȫ��
		((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//���ú���
		//((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	/**
	 * ��ȡ��Ļ�ߴ�
	 */
	private void initScreenData() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);

		SCREEN_WIDTH = dm.widthPixels;
		SCREEN_HEIGHT = dm.heightPixels;
		SCREEN_DPI = dm.densityDpi;
	}
	//��ȡ��Ļ���
	public int getScreenWidth() {
		return this.SCREEN_WIDTH;
	}
	//��ȡ��Ļ�߶�
	public int getScreenHeight() {
		return this.SCREEN_HEIGHT;
	}
	
	//��ȡ��Ļ�ֱ���
	public int getScreenDPI() {
		return this.SCREEN_DPI;
	}

	// ������Ϣʱʹ��
	public static void ShowXYMessage(Context con, String str) {
		Toast.makeText(con, str, Toast.LENGTH_SHORT).show();
	}
	
	// ͣ��ָ��ʱ��
	public void SleepTime(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
