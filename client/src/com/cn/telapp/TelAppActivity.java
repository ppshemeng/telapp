package com.cn.telapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cn.telapp.asynimg.ImageAndText;
import com.cn.telapp.asynimg.ImageAndTextListAdapter;
import com.cn.telapp.config.Config;
import com.cn.telapp.location.PhoneLocation;
import com.cn.telapp.gprs.GPRS;
import com.cn.telapp.httpjson.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TelAppActivity extends Activity {

	private ListView listView = null;
	private LocationManager locManager = null;
	private Location location = null;
	private ArrayList<ImageAndText> listItem = null;
	private ProgressDialog mLoadingDialog = null;
	private ConnectivityManager mCM = null;
	private TextView txtTextView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.shopview);
		showDialogLoading();
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.maintitle);

		TextView title = (TextView) findViewById(R.id.maintitle);
		title.setText("�����Ĳ���");

		
		
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
        // ��ȡλ����Ϣ
		// ��ȡ��LocationManager����
		locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		location = new PhoneLocation(locManager, mCM).getLocation();
		
		if (location == null) {
			displayToast("��λʧ��");
			unShowDialogLoading();
			finish();
		}
		
		txtTextView = (TextView)findViewById(R.id.txtWarning);
		listView = (ListView) findViewById(R.id.listview);
		getData(location);
		ImageAndTextListAdapter listItemAdapter = new ImageAndTextListAdapter(
				this, listItem, listView);
		listView.setAdapter(listItemAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				ImageAndText imageAndText = listItem.get(position);
				Intent intent = new Intent();

				Bundle bundle = new Bundle();
				bundle.putInt("shopid", imageAndText.getShopId());
				bundle.putString("tel", imageAndText.getShopTel());
				bundle.putString("name", imageAndText.getShopName());
				bundle.putString("url", imageAndText.getImageUrl());

				intent.putExtra("bundle", bundle);
				intent.setClass(TelAppActivity.this, MenuActivity.class);
				startActivityForResult(intent, 10);
			}
		});
		
		unShowDialogLoading();
	}

	/*
	 * webservice ��ʽ private void getData(Location location) { listItem = new
	 * ArrayList<ImageAndText>(); List<String> list = null; list =
	 * WebServiceUtil.getShopInfo( Double.toString(location.getLatitude()),
	 * Double.toString(location.getLongitude())); if (list == null) return; for
	 * (int i = 0; i < list.size(); i++) { String[] shopinfos =
	 * list.get(i).split(";"); int id = Integer.parseInt(shopinfos[0]); String
	 * imgUrl = Config.baseUrl + shopinfos[2]; String content = "   ����: " +
	 * shopinfos[1] + "\n   �绰: " + shopinfos[3] + "\n   ����: "; if
	 * (Integer.valueOf(shopinfos[8]) / 1000 >= 1) { content +=
	 * Double.parseDouble(shopinfos[8]) / 1000 + "ǧ��"; } else { content +=
	 * shopinfos[8] + "��"; } String name = shopinfos[1]; String tel =
	 * shopinfos[3]; ImageAndText imageAndText = new ImageAndText(imgUrl,
	 * content, id, name, tel); listItem.add(imageAndText); } }
	 */
	// http json
	private void getData(Location location) {
		listItem = new ArrayList<ImageAndText>();
		Map map = new HashMap();
		map.put("latitude", location.getLatitude());
		map.put("longitude", location.getLongitude());
		JsonData jsonData = new JsonData(map);
		String url = Config.baseUrl + "telapp/shop.jsp";
		String shopinfo = jsonData.getJson(url).trim();
		JSONArray array;
		try {
			array = new JSONArray(shopinfo);
			if (array.length() == 0) { txtTextView.setText("��Χ10������û������������"); return;}
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = new JSONObject(array.getString(i));
				int id = Integer.parseInt(object.getString("shopid"));
				String imgUrl = Config.baseUrl + Config.workspace + object.getString("shopimg");
				String content = "   ����: " + object.getString("shopname")
						+ "\n   �绰: " + object.getString("shopphone")
						+ "\n   ����: ";
				if (Integer.valueOf(object.getString("shopdistance")) / 1000 >= 1) {
					content += Double.parseDouble(object
							.getString("shopdistance")) / 1000 + "ǧ��";
				} else {
					content += object.getString("shopdistance") + "��";
				}
				String name = object.getString("shopname");
				String tel = object.getString("shopphone");
				ImageAndText imageAndText = new ImageAndText(imgUrl, content,
						id, name, tel);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
				listItem.add(imageAndText);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showDialogLoading() {
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// spinner ������ ������������
		mLoadingDialog.setMessage("���ڶ�λ,���Ժ�...");
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

	private void displayToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
}