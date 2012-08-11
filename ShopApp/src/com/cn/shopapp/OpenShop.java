package com.cn.shopapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cn.telapp.config.Config;
import com.cn.telapp.http.HttpJsonData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OpenShop extends Activity {

	private final String IMAGE_TYPE = "image/*";
	private final int IMAGE_CODE = 0;
	private LocationClient mLocationClient = null;
	private ProgressDialog mLoadingDialog = null;
	private Bundle bundle = null;
	private EditText shopnameEditText = null;
	private EditText shopdescEditText = null;
	private EditText shopaddrEditText = null;
	private EditText shoplinkerEditText = null;
	private EditText phoneEditText = null;
	private EditText qqEditText = null;
	private EditText shopimgEditText  = null;
	private TextView latTextView = null;
	private TextView lngTextView = null;
	private Button openshop = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openshop);
		
		bundle = getIntent().getBundleExtra("bundle");
		
		shopnameEditText = (EditText)findViewById(R.id.shopname);
		shopdescEditText = (EditText)findViewById(R.id.shopdesc);
		shopaddrEditText = (EditText)findViewById(R.id.shopaddr);
		shoplinkerEditText = (EditText)findViewById(R.id.shoplinker);
		phoneEditText = (EditText)findViewById(R.id.shopphone);
		qqEditText = (EditText)findViewById(R.id.shopqq);
		shopimgEditText = (EditText)findViewById(R.id.shopimg);
		latTextView = (TextView)findViewById(R.id.lat);
		lngTextView = (TextView)findViewById(R.id.lng);
		
		Button addimg = (Button)findViewById(R.id.addimg);
		openshop = (Button)findViewById(R.id.openshop);
		
		 //���ðٶȵ�api���ж�λ
        showDialogLoading();
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
		
		//ѡ��ͼƬ
		addimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
				getAlbum.setType(IMAGE_TYPE);
				startActivityForResult(getAlbum, IMAGE_CODE);
			}
		});
		//��ͨ
		openshop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog alertDialog = null;
				if (shopnameEditText.getText().toString().equals("")) {
					alertDialog = new AlertDialog.Builder(v.getContext())
							.setMessage("������д������")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create();
					alertDialog.show();
					return;
				}
				if (latTextView.getText().toString().equals("") || lngTextView.getText().toString().equals("")) {
					alertDialog = new AlertDialog.Builder(v.getContext())
							.setMessage("��λʧ�ܣ�")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create();
					alertDialog.show();
					return;
				}
				openshop.setText("�����ύ����...");
				openshop.setEnabled(false);
				boolean success = OpenShop();
				if (success)
				{
					Intent intent = new Intent(OpenShop.this, ShopDetail.class);
					intent.putExtra("bundle", bundle);
					startActivity(intent);
				}
				openshop.setText("���");
				openshop.setEnabled(true);
			}
		});
		
	}
	
	// http json
	private boolean OpenShop() {
		Map map = new HashMap();
		map.put("userid", bundle.getInt("userid"));
		map.put("shopname", shopnameEditText.getText().toString());
		map.put("shopdesc", shopdescEditText.getText().toString());
		map.put("shopaddr", shopaddrEditText.getText().toString());
		map.put("linker", shoplinkerEditText.getText().toString());
		map.put("phone", phoneEditText.getText().toString());
		map.put("qq", qqEditText.getText().toString());
		map.put("lat", latTextView.getText().toString());
		map.put("lng", lngTextView.getText().toString());
		if (!shopimgEditText.getText().toString().equals("")) {
			String[] imgString = shopimgEditText.getText().toString()
					.split("/");
			map.put("shopimg", imgString[imgString.length - 1]);
			FileInputStream in = null;
			try {
				in = new FileInputStream(shopimgEditText.getText().toString());
				byte buffer[] = null;
				try {
					buffer = HttpJsonData.read(in);
				} catch (Exception e) {
					e.printStackTrace();
				}// ��ͼƬ�ļ���ת��byte����
				byte[] encod = Base64.encode(buffer, Base64.DEFAULT);// ʹ��base64����
				map.put("img", new String(encod));// �������ݵ�map����
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			map.put("img", "");
			map.put("shopimg", "");
		}
		HttpJsonData jsonData = new HttpJsonData(map);
		String url = Config.baseUrl + "telapp/business/openshop.jsp";
		String value = jsonData.getJson(url).trim();
		if (value.trim().equals("true")) {
			return true;
		}
		return false;
	}
		
	BDLocationListener bdLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation loc) {
			if (loc == null) return;
			if (loc.getLocType() == 65) return;
			String position = "γ��:" + loc.getLatitude() + "\n����" + loc.getLongitude();
			latTextView.setText(String.valueOf(loc.getLatitude()));
			lngTextView.setText(String.valueOf(loc.getLongitude()));
			shopaddrEditText.setText(loc.getAddrStr());
			displayToast(position);
			
			mLocationClient.unRegisterLocationListener(bdLocationListener);
			mLocationClient.stop();
			unShowDialogLoading();
		}
	};
	
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) { // �˴��� RESULT_OK ��ϵͳ�Զ����һ������
			return;
		}
		Bitmap bm = null;
		ContentResolver resolver = getContentResolver();
		if (requestCode == IMAGE_CODE) {
			try {
				Uri originalUri = data.getData(); // ���ͼƬ��uri
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // �Եõ�bitmapͼƬ
				// ���￪ʼ�ĵڶ����֣���ȡͼƬ��·����
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(originalUri, proj, null, null,
						null);
				// ���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				// ����������ֵ��ȡͼƬ·��
				String path = cursor.getString(column_index);
				shopimgEditText.setText(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void displayToast(String str)
    {
    	Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
