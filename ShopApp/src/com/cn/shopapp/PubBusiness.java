package com.cn.shopapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.cn.telapp.config.Config;
import com.cn.telapp.http.HttpJsonData;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.FlagToString;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class PubBusiness extends Activity {

	private Bundle bundle = null;
	private RadioGroup r_group = null;
	private RadioButton r_buy = null;
	private RadioButton r_sell = null;
	private LinearLayout layoutprice = null;
	private LinearLayout layoutimg = null;
	private EditText 	titleEditText = null;
	private EditText	detailEditText = null;
	private EditText 	priceEditText = null;
	private EditText	linkerEditText = null;
	private EditText 	phoneEditText = null;
	private EditText 	qqEditText = null;
	private EditText	imgEditText  = null;
	private Button addBusiness = null;
	private Button addimg = null;
	private int  type = 0;
	private final String IMAGE_TYPE = "image/*";
	private final int IMAGE_CODE = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pubbusiness);
		
		bundle = getIntent().getBundleExtra("bundle");
		
        layoutprice = (LinearLayout)findViewById(R.id.layoutprice);
        layoutimg = (LinearLayout)findViewById(R.id.layoutimg);
		r_group = (RadioGroup)findViewById(R.id.usergroup);
        r_buy = (RadioButton)findViewById(R.id.buy);
        r_sell = (RadioButton)findViewById(R.id.sell);
        r_group.setOnCheckedChangeListener(mChangeRadio);
        r_group.check(r_buy.getId());
        addBusiness = (Button)findViewById(R.id.addbusiness);
        addimg = (Button)findViewById(R.id.addimg);
        
        titleEditText = (EditText)findViewById(R.id.title);
        detailEditText = (EditText)findViewById(R.id.detail);
        priceEditText = (EditText)findViewById(R.id.price);
        linkerEditText = (EditText)findViewById(R.id.linker);
        phoneEditText = (EditText)findViewById(R.id.phone);
        qqEditText = (EditText)findViewById(R.id.qq);
        imgEditText = (EditText)findViewById(R.id.businessimg);
        
        addBusiness.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog alertDialog = null;
				if (titleEditText.getText().toString().equals("")) {
					alertDialog = new AlertDialog.Builder(v.getContext())
							.setMessage("������д���⣡")
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
				addBusiness.setText("�����ύ����...");
				addBusiness.setEnabled(false);
				boolean success = addBusiness();
				if (success)
				{
					setResult(RESULT_OK);
					finish();
				}
				addBusiness.setText("���");
				addBusiness.setEnabled(true);
			}
		});
		
        
        addimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
				getAlbum.setType(IMAGE_TYPE);
				startActivityForResult(getAlbum, IMAGE_CODE);
			}
		});
	}

	private boolean addBusiness() {
		String url = Config.baseUrl + Config.workspace + "business/addbusiness.jsp";
		Map<String, String> map = new HashMap<String, String>();
		map.put("userid", bundle.getInt("userid")+"");
		map.put("title", titleEditText.getText().toString().trim());
		map.put("detail", detailEditText.getText().toString().trim());
		if (type == 0) {
			map.put("price", "0");
			map.put("businessimg", "");
			map.put("img", "");
		} else {
			map.put("price", priceEditText.getText().toString().trim());
			if (!imgEditText.getText().toString().equals("")) {
				String[] imgString = imgEditText.getText().toString()
						.split("/");
				map.put("businessimg", imgString[imgString.length - 1]);
				FileInputStream in = null;
				try {
					in = new FileInputStream(imgEditText.getText().toString());
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
			} else {
				map.put("img", "");
				map.put("businessimg", "");
			}
		}
		map.put("phone", phoneEditText.getText().toString().trim());
		map.put("linker", linkerEditText.getText().toString().trim());
		map.put("qq", qqEditText.getText().toString().trim());
		map.put("flag", type + "");

		try {
			HttpJsonData jsonData = new HttpJsonData(map);
			String value = jsonData.getJson(url);
			if (value.trim().equals("true")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	 private RadioGroup.OnCheckedChangeListener mChangeRadio = new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == r_buy.getId())
				{
					type = 0;//��
				}else if(checkedId == r_sell.getId()) {
					type = 1;//����
				}
				isHidden();
			}
	    };
	    
	    private void isHidden()
	    {
	    	if (type == 0)
	    	{
	    		layoutimg.setVisibility(View.GONE);
	    		layoutprice.setVisibility(View.GONE);
	    	}else {
	    		layoutimg.setVisibility(View.VISIBLE);
	    		layoutprice.setVisibility(View.VISIBLE);
			}
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
					imgEditText.setText(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	    
}
