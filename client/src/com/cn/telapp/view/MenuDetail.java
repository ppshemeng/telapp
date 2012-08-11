package com.cn.telapp.view;

import java.io.IOException;
import java.net.URL;

import com.cn.telapp.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuDetail extends Activity {

	private Bundle bundle = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.menuitem);

		bundle = getIntent().getBundleExtra("bundle");

		TextView menuName = (TextView) findViewById(R.id.menuname);
		Button call = (Button) findViewById(R.id.menubutton);

		menuName.setText(bundle.getString("menuname"));
		loadImage(bundle.getString("url"), R.id.menuimg);

		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ bundle.getString("tel")));
				startActivity(intent);
				// ������
				/*
				 * Intent intent = new Intent(); //ϵͳĬ�ϵ�action��������Ĭ�ϵĶ��Ž���
				 * intent.setAction(Intent.ACTION_SENDTO); //��Ҫ����Ϣ�ĺ���
				 * intent.setData(Uri.parse("smsto:15091054550"));
				 * intent.putExtra("sms_body", "android test");
				 * startActivity(intent);
				 */
			}
		});

	}

	// Handler+Thread+Messageģʽ
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			((ImageView) MenuDetail.this.findViewById(msg.arg1))
					.setImageDrawable((Drawable) msg.obj);
		}
	};

	// ����handler+Threadģʽʵ�ֶ��߳��첽����
	private void loadImage(final String url, final int id) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Drawable drawable = null;
				try {
					drawable = Drawable.createFromStream(
							new URL(url).openStream(), "image.png");
				} catch (IOException e) {
					Log.d("test", e.getMessage());
				}

				Message message = handler.obtainMessage();
				message.arg1 = id;
				message.obj = drawable;
				handler.sendMessage(message);
			}
		};
		thread.start();
		thread = null;
	}

}
