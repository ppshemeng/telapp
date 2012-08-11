package com.cn.shopapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cn.shopapp.adapter.MyBuyListAdapter;
import com.cn.shopapp.model.BusinessInfo;
import com.cn.telapp.config.Config;
import com.cn.telapp.http.HttpJsonData;
import com.cn.telapp.util.ContextUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class My_Buy extends Activity {

	private ListView listView = null;
	private Bundle bundle = null;
	private ProgressDialog mLoadingDialog = null;
	private ArrayList<BusinessInfo> listItem = new ArrayList<BusinessInfo>();
	private MyBuyListAdapter listItemAdapter = null;
	private int TOTAL_PAGE = 0;
	private ExecutorService executorService;
	private Handler handler;
	private TextView tv_msg;
	private LinearLayout list_footer;
	private LinearLayout loading;
	private int currentSize = 0;
	private int lastSize = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContextUtil contextUtil = new ContextUtil(this);
		contextUtil.noTitleBar();
		contextUtil.setFullScreen();
		setContentView(R.layout.listview);
		showDialogLoading();
		bundle = getIntent().getBundleExtra("bundle");
		
		TextView top_title = (TextView)findViewById(R.id.top_title);
		top_title.setText("�ҵ��󹺼�¼");
		
		setUpViews();
		executorService.submit(new GetDataThread());
		tv_msg.setVisibility(View.GONE);//���ظ�����ʾ��TextView
		loading.setVisibility(View.VISIBLE);//��ʾ���·��Ľ�����
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// displayToast( Integer.toString(articleIds.get(arg2)));
				BusinessInfo businessInfo = listItem.get(position);
				Intent intent = new Intent();

				Bundle bund = new Bundle();
				bund.putInt("businessid", businessInfo.getBusinessId());
				intent.putExtra("bundle", bund);
				intent.setClass(My_Buy.this, BusinessDetail.class);
				startActivityForResult(intent, 11);
			}
		});
		unShowDialogLoading();
	}

	private void setUpViews(){
		handler = new GetDataHandler();
		listView = (ListView) findViewById(R.id.listview);
		list_footer = (LinearLayout)LayoutInflater.from(My_Buy.this).inflate(R.layout.list_footer, null);
		tv_msg = (TextView)list_footer.findViewById(R.id.tv_msg);
		loading = (LinearLayout)list_footer.findViewById(R.id.loading);
		listView.addFooterView(list_footer);//����ǹؼ��еĹؼ�ѽ������FooterVIew��ҳ��̬����
		List<BusinessInfo> item = new ArrayList<BusinessInfo>();
		listItemAdapter = new MyBuyListAdapter(My_Buy.this, item);
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
				BusinessInfo businessInfo = listItem.get(position);
				Intent intent = new Intent();

				Bundle bund = new Bundle();
				bund.putInt("businessid", businessInfo.getBusinessId());
				intent.putExtra("bundle", bund);
				intent.setClass(My_Buy.this, BusinessDetail.class);
				startActivityForResult(intent, 11);
			}
		});
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
	
	// http json
	private void getData() {
		Map map = new HashMap();
		map.put("userid", bundle.getInt("userid"));
		map.put("pagefirst", currentSize + 1);
		map.put("pagesize", (TOTAL_PAGE + 1) * Config.PAGE_SIZE);
		HttpJsonData jsonData = new HttpJsonData(map);
		String url = Config.baseUrl + "telapp/business/getmybuy.jsp";
		String menuinfo = jsonData.getJson(url).trim();
		JSONArray array;
		try {
			array = new JSONArray(menuinfo);
			currentSize += array.length();
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = new JSONObject(array.getString(i));
				//�û�ͷ��
				String imgUrl = Config.baseUrl + Config.workspace + bundle.getString("userimg");
				int  businessId = Integer.parseInt(object.getString("businessid"));
				String title = object.getString("title");
				String detail = object.getString("detail");
				String distance = object.getString("distance");
				String pubDate = object.getString("pubtime");
				String price = object.getString("price");

				BusinessInfo businessInfo = new BusinessInfo();
				businessInfo.setBusinessBody(detail);
				businessInfo.setBusinessDistance(distance);
				businessInfo.setBusinessId(businessId);
				businessInfo.setBusinessTitle(title);
				businessInfo.setUserImgUrl(imgUrl);
				businessInfo.setPubDate(pubDate);
				businessInfo.setBusinessPrice(price);
				listItem.add(businessInfo);
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
}
