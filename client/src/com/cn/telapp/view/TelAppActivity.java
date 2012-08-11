package com.cn.telapp.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cn.telapp.R;
import com.cn.telapp.adapter.ShopListAdapter;
import com.cn.telapp.config.Config;
import com.cn.telapp.http.*;
import com.cn.telapp.model.ShopInfo;
import com.cn.telapp.util.ContextUtil;
import com.cn.telapp.util.GPRS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TelAppActivity extends Activity {

	private ListView listView = null;
	private ArrayList<ShopInfo> listItem = new ArrayList<ShopInfo>();
	private ProgressDialog mLoadingDialog = null;
	private ConnectivityManager mCM = null;
	private LocationClient mLocationClient = null;
	private int TOTAL_PAGE = 0;
	private ShopListAdapter listItemAdapter;
	private ExecutorService executorService;
	private double lat;
	private double lng;
	private Handler handler;
	private TextView tv_msg;
	private LinearLayout list_footer;
	private LinearLayout loading;
	private int currentSize = 0;
	private int lastSize = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContextUtil contextUtil = new ContextUtil(this);
		contextUtil.noTitleBar();
		contextUtil.setFullScreen();
		setContentView(R.layout.shopview);
		
		showDialogLoading("���ڶ�λ,���Ժ�...");
		
        mCM = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
        GPRS gprs = new GPRS(mCM);
        Dialog alertDialog = null;
        if (!gprs.isOpenNet())
        {
	        alertDialog = new AlertDialog.Builder(this). 
	                setMessage("���ȿ�������"). 
	                setPositiveButton("ȷ��", new DialogInterface.OnClickListener() { 
	                     
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                         dialog.dismiss();
	                         finish();
	                    } 
	                }). 
	                create(); 
	        alertDialog.show();
        }
		setUpViews();
        //���ðٶȵ�api���ж�λ
        mLocationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);								//��gps
        option.setCoorType("bd09ll");							//������������Ϊbd09ll
        option.setPriority(LocationClientOption.GpsFirst);	//������������
        option.setProdName("telapp");						//���ò�Ʒ������
        option.setScanSpan(3000);								//��ʱ��λ��ÿ��3���Ӷ�λһ�Ρ�
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(bdLocationListener);
        mLocationClient.start();
	}

	private void setUpViews(){
		handler = new GetDataHandler();
		listView = (ListView) findViewById(R.id.listview);
		list_footer = (LinearLayout)LayoutInflater.from(TelAppActivity.this).inflate(R.layout.list_footer, null);
		tv_msg = (TextView)list_footer.findViewById(R.id.tv_msg);
		loading = (LinearLayout)list_footer.findViewById(R.id.loading);
		listView.addFooterView(list_footer);//����ǹؼ��еĹؼ�ѽ������FooterVIew��ҳ��̬����
		List<ShopInfo> item = new ArrayList<ShopInfo>();
		listItemAdapter = new ShopListAdapter(TelAppActivity.this, item);
		listView.setAdapter(listItemAdapter);
		executorService = Executors.newFixedThreadPool(Config.THREADPOOL_SIZE);
		
		tv_msg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lastSize = currentSize;
				TOTAL_PAGE++;
				executorService.submit(new GetDataThread());
				tv_msg.setVisibility(View.GONE);//���ظ�����ʾ��TextView
				loading.setVisibility(View.VISIBLE);//��ʾ���·��Ľ�����
			}
		});
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				ShopInfo item = listItem.get(position);
				Intent intent = new Intent();

				Bundle bundle = new Bundle();
				bundle.putInt("shopid", item.getShopId());
				bundle.putString("tel", item.getShopTel());
				bundle.putString("name", item.getShopName());
				bundle.putString("url", item.getShopUrl());

				intent.putExtra("bundle", bundle);
				intent.setClass(TelAppActivity.this, MenuActivity.class);
				startActivityForResult(intent, 10);
			}
		});
	}
	
	BDLocationListener bdLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation loc) {
			if (loc == null) return;
			if (loc.getLocType() == 65) return;
			unShowDialogLoading();
			lat = loc.getLatitude();
			lng = loc.getLongitude();
			executorService.submit(new GetDataThread());
			tv_msg.setVisibility(View.GONE);//���ظ�����ʾ��TextView
			loading.setVisibility(View.VISIBLE);//��ʾ���·��Ľ�����
			mLocationClient.unRegisterLocationListener(bdLocationListener);
			mLocationClient.stop();
		}
	};
	
	class GetDataHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if(currentSize > lastSize){
				for (int i = lastSize; i < currentSize; i++) {
					listItemAdapter.add(listItem.get(i));
				}
				listItemAdapter.notifyDataSetChanged();
				listView.setSelection(lastSize + 1);//�������»�ȡһҳ���ݳɹ�����ʾ���ݵ���ʼ����
			}
			loading.setVisibility(View.GONE);//�����·��Ľ�����
			tv_msg.setVisibility(View.VISIBLE);//��ʾ��������ʾTextView
		}
	}
	
	class GetDataThread implements Runnable{
		@Override
		public void run() {
			getData(lat, lng);
			Message msg = handler.obtainMessage();//֪ͨ�߳�������Χ������
			handler.sendMessage(msg);		
		}
	}
	
	// http json
	private void getData(double latitude, double longitude) {
		Map map = new HashMap();
		map.put("pagefirst", currentSize + 1);
		map.put("pagesize", (TOTAL_PAGE + 1) * Config.PAGE_SIZE);
		map.put("latitude", latitude);
		map.put("longitude", longitude);
		HttpJsonData jsonData = new HttpJsonData(map);
		String url = Config.baseUrl + "telapp/shop.jsp";
		String shopinfo = jsonData.getJson(url).trim();
		JSONArray array;
		try {
			array = new JSONArray(shopinfo);
			currentSize += array.length();
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = new JSONObject(array.getString(i));
				
				int shopId = Integer.parseInt(object.getString("shopid"));
				String imgUrl = Config.baseUrl + Config.workspace + object.getString("shopimg");
				String shopName = object.getString("shopname");
				String shopInfo = object.getString("shopphone");
				String shopDistance = object.getString("shopdistance");
				String shopTel = object.getString("shopphone");
				
				ShopInfo shop = new ShopInfo();
				shop.setShopId(shopId);
				shop.setShopUrl(imgUrl);
				shop.setShopTel(shopTel);
				shop.setShopInfo(shopInfo);
				shop.setShopName(shopName);
				shop.setShopDistance(shopDistance);
				listItem.add(shop);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showDialogLoading(String message) {
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// spinner ������ ������������
		mLoadingDialog.setMessage(message);
		mLoadingDialog.setIndeterminate(false);// ���ý������Ƿ�Ϊ����ȷ
		mLoadingDialog.setCancelable(true);// ���ý������Ƿ���԰��˻ؽ�ȡ��
		mLoadingDialog.show();
	}

	private void unShowDialogLoading() {
		if (mLoadingDialog == null)
			return;
		else
			mLoadingDialog.dismiss();
	}
}