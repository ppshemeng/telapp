package com.cn.shopapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.cn.telapp.config.Config;
import com.cn.telapp.http.HttpJsonData;

import android.app.Activity;
import android.app.AlertDialog;
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

public class AddItem extends Activity {

	private final String IMAGE_TYPE = "image/*";
	private final int IMAGE_CODE = 0;
	private Bundle bundle = null;
	private EditText itemnameEditText = null;
	private EditText itemdescEditText = null;
	private EditText itempriceEditText = null;
	private EditText itemimgEditText = null;
	private Button additem = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.additem);
		
		bundle = getIntent().getBundleExtra("bundle");
		
		itemnameEditText = (EditText)findViewById(R.id.itemname);
		itemdescEditText = (EditText)findViewById(R.id.itemdesc);
		itempriceEditText = (EditText)findViewById(R.id.itemprice);
		itemimgEditText = (EditText)findViewById(R.id.itemimg);
		additem = (Button)findViewById(R.id.additem);
		Button addimg = (Button)findViewById(R.id.addimg);
		//ѡ��ͼƬ
		addimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
				getAlbum.setType(IMAGE_TYPE);
				startActivityForResult(getAlbum, IMAGE_CODE);
			}
		});
		//�����Ʒ
		additem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog alertDialog = null;
				if (itemnameEditText.getText().toString().equals("")) {
					alertDialog = new AlertDialog.Builder(v.getContext())
							.setMessage("������д��Ʒ����")
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
				additem.setText("�����ύ����...");
				additem.setEnabled(false);
				boolean success = additem();
				if (success)
				{
					setResult(RESULT_OK);
					finish();
				}
				additem.setText("���");
				additem.setEnabled(true);
			}
		});
	}

	// http json
	private boolean additem() {
		Map map = new HashMap();
		map.put("shopid", bundle.getInt("shopid"));
		map.put("itemname", itemnameEditText.getText().toString());
		map.put("itemdesc", itemdescEditText.getText().toString());
		map.put("itemprice", itempriceEditText.getText().toString());
		if (!itemimgEditText.getText().toString().equals("")) {
			String[] imgString = itemimgEditText.getText().toString()
					.split("/");
			map.put("itemimg", imgString[imgString.length - 1]);
			FileInputStream in = null;
			try {
				in = new FileInputStream(itemimgEditText.getText().toString());
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
			map.put("itemimg", "");
		}
		HttpJsonData jsonData = new HttpJsonData(map);
		String url = Config.baseUrl + "telapp/business/additem.jsp";
		String value = jsonData.getJson(url).trim();
		if (value.trim().equals("true")) {
			return true;
		}
		return false;
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
				itemimgEditText.setText(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
