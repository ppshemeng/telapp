package com.cn.telapp.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

	//�ѷ�����ʱ����ʾΪ ����ǰ ��Сʱǰ ������ǰ
	public String getPubTime(String pubtime)
	{
		long diff = 0;
		try {
			diff = new Date().getTime() - new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(pubtime).getTime();
			return TimeFormat(diff/1000, pubtime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String TimeFormat(long sum, String pubtime)
	{
		String value = "";
		if (sum < 1) {
			value = "�ո�";
			return value;
		}
		if (sum < 60) {
			value = sum + "��ǰ";
			return value;
		}
		if (sum < 3600) {
			value = sum/60 + "����ǰ";
			return value;
		}
		if (sum < 86400) {
			value = sum/3600 + "Сʱǰ";
			return value;
		}
		if (sum < 432000) {
			value = sum / 86400 + "��ǰ";
			return value;
		}
		
		long aa;
		try {
			aa = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(pubtime).getTime();
			String str = new SimpleDateFormat("yyyy��MM��dd��").format(new Date(aa));
			return str;
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
