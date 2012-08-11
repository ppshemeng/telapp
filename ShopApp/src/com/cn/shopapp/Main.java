package com.cn.shopapp;

import java.util.HashMap;
import java.util.Map;

import org.androidpn.client.ServiceManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cn.telapp.config.Config;
import com.cn.telapp.http.HttpJsonData;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

public class Main extends TabActivity {

	private String lat = null;
	private String lng = null;
	private Bundle bundle = null;
	private LocationClient mLocationClient = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maininfo);
		//setFullScreen();
		
		bundle = getIntent().getBundleExtra("bundle");
		
	      //���ðٶȵ�api���ж�λ
        mLocationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);								//��gps
        option.setCoorType("bd09ll");							//������������Ϊbd09ll
        option.setPriority(LocationClientOption.GpsFirst);	//������������
        option.setProdName("ShopApp");						//���ò�Ʒ������
        option.setScanSpan(3000);								//��ʱ��λ��ÿ��3���Ӷ�λһ�Ρ�
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(bdLocationListener);
        mLocationClient.start();
        
        // Start the service
        ServiceManager serviceManager = new ServiceManager(Main.this, 
        		bundle.getString("username"),  bundle.getString("password"));
        
        serviceManager.setNotificationIcon(R.drawable.icon);
        serviceManager.startService();
        
		// tabHost��һ����ǩ����
		TabHost tabHost = this.getTabHost();

		Intent buy = new Intent(Main.this, All_Buy.class);
		buy.putExtra("bundle", bundle);
		// �����һ����ǩ
		tabHost.addTab(tabHost
				.newTabSpec("main_buy")
				.setIndicator("��",
						getResources().getDrawable(android.R.drawable.star_on))
				.setContent(buy));

		Intent sell = new Intent(Main.this, All_Sell.class);
		sell.putExtra("bundle", bundle);
		// ����ڶ�����ǩ
		tabHost.addTab(tabHost
				.newTabSpec("main_sell")
				.setIndicator("����",
						getResources().getDrawable(android.R.drawable.star_on))
				.setContent(sell));

		Intent shop = new Intent(Main.this, ShopList.class);
		shop.putExtra("bundle", bundle);
		tabHost.addTab(tabHost
				.newTabSpec("main_shop")
				.setIndicator("����",
						getResources().getDrawable(android.R.drawable.star_on))
				.setContent(shop));
		
		Intent info = new Intent(Main.this, Person.class);
		info.putExtra("bundle", bundle);
		tabHost.addTab(tabHost
				.newTabSpec("main_info")
				.setIndicator("��������",
						getResources().getDrawable(android.R.drawable.star_on))
				.setContent(info));
	}
	
	
	BDLocationListener bdLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation loc) {
			if (loc == null) return;

			if (loc.getLocType() == 61 || loc.getLocType() == 161) {
				lat = String.valueOf(loc.getLatitude());
				lng = String.valueOf(loc.getLongitude());
	
				//�����ҵ�λ��
				String url = Config.baseUrl + Config.workspace + "business/updateposition.jsp";
				Map<String, String> map = new HashMap<String, String>();
				map.put("userid", bundle.getInt("userid")+"");
				map.put("lat", lat);
				map.put("lng", lng);
	
				HttpJsonData jsonData = new HttpJsonData(map);
				String value = jsonData.getJson(url);
				if (value.trim().equals("true")) {
					mLocationClient.unRegisterLocationListener(bdLocationListener);
					bundle.putString("lat", lat);
					bundle.putString("lng", lng);
					mLocationClient.stop();
				}
			}
		}
	};
	
	//����ȫ��
	private void setFullScreen(){
	     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
	 }
}
