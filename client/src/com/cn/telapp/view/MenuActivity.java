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

import com.cn.telapp.R;
import com.cn.telapp.adapter.MenuListAdapter;
import com.cn.telapp.config.Config;
import com.cn.telapp.http.HttpJsonData;
import com.cn.telapp.model.MenuInfo;
import com.cn.telapp.util.ContextUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MenuActivity extends Activity {

	private ListView listView = null;
	private Bundle bundle = null;
	private ProgressDialog mLoadingDialog = null;
	private ArrayList<MenuInfo> listItem = new ArrayList<MenuInfo>();
	MenuListAdapter listItemAdapter = null;
	private LinearLayout list_footer;
	private TextView tv_msg;
	private LinearLayout loading;
	private int TOTAL_PAGE = 0;
	private ExecutorService executorService;
	private int currentSize = 0;
	private int lastSize = 0;
	private Handler handler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContextUtil contextUtil = new ContextUtil(this);
		contextUtil.noTitleBar();
		contextUtil.setFullScreen();
		setContentView(R.layout.menuview);
		showDialogLoading();
	
		bundle = getIntent().getBundleExtra("bundle");
		
		setUpViews();
		executorService.submit(new GetDataThread());
		tv_msg.setVisibility(View.GONE);//���ظ�����ʾ��TextView
		loading.setVisibility(View.VISIBLE);//��ʾ���·��Ľ�����
		
		TextView shopName = (TextView)findViewById(R.id.top_title);
		shopName.setText(bundle.getString("name"));
		
		// ����
		ImageButton call = (ImageButton) findViewById(R.id.menucall);
		Button back = (Button)findViewById(R.id.top_btn_back);
		
		back.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
		
		call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ bundle.getString("tel")));
				startActivity(intent);
			}
		});
		unShowDialogLoading();
	}
	
	private void setUpViews(){
		handler = new GetDataHandler();
		listView = (ListView) findViewById(R.id.listview);
		list_footer = (LinearLayout)LayoutInflater.from(MenuActivity.this).inflate(R.layout.list_footer, null);
		tv_msg = (TextView)list_footer.findViewById(R.id.tv_msg);
		loading = (LinearLayout)list_footer.findViewById(R.id.loading);
		listView.addFooterView(list_footer);//����ǹؼ��еĹؼ�ѽ������FooterVIew��ҳ��̬����
		List<MenuInfo> item = new ArrayList<MenuInfo>();
		listItemAdapter = new MenuListAdapter(this, item);
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
	//�б�����¼�	
/*		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// displayToast( Integer.toString(articleIds.get(arg2)));
				MenuInfo item = listItem.get(position);
				Intent intent = new Intent();

				Bundle bund = new Bundle();
				bund.putString("url", item.getMenuUrl());
				bund.putString("tel", bundle.getString("tel"));
				bund.putString("menuname", item.getMenuName());
				bund.putString("menuprice", item.getMenuPrice());
				bund.putString("shopname", bundle.getString("name"));
				intent.putExtra("bundle", bund);
				intent.setClass(MenuActivity.this, MenuDetail.class);
				startActivityForResult(intent, 11);
				// startActivity(intent);

			}
		});*/
	}
	
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
			getData();
			Message msg = handler.obtainMessage();//֪ͨ�߳�������Χ������
			handler.sendMessage(msg);		
		}
	}
	
	//http json
	private void getData() {
		Map map = new HashMap();
		map.put("pagefirst", currentSize + 1);
		map.put("pagesize", (TOTAL_PAGE + 1) * Config.PAGE_SIZE);
		map.put("shopid", bundle.getInt("shopid"));
		HttpJsonData jsonData = new HttpJsonData(map);
		String url = Config.baseUrl + "telapp/menu.jsp";
		String menuinfo = jsonData.getJson(url).trim();
		JSONArray array;
		try {
			array = new JSONArray(menuinfo);
			currentSize += array.length();
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = new JSONObject(array.getString(i));
				
				String menuUrl = Config.baseUrl + Config.workspace + object.getString("menuimage");
				int menuid = Integer.parseInt(object.getString("menuid"));
				String menuname = object.getString("menuname");
				String menuprice = object.getString("menuprice");
				
				MenuInfo menu = new MenuInfo();
				menu.setMenuName(menuname);
				menu.setMenuPrice(menuprice);
				menu.setMenuIcon(menuUrl);
				menu.setMenuId(menuid);
				listItem.add(menu);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void showDialogLoading() {
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// spinner ������ ������������
		mLoadingDialog.setMessage("������,���Ժ�...");
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
	//����ȫ��
	private void setFullScreen(){
	     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
	 }
}
