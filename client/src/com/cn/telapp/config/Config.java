package com.cn.telapp.config;

public class Config {

	public static final String  baseUrl= "http://219.245.92.195:8080/";
	
	public static final String workspace = "telapp/";
	//�����ռ�
	public static final String targetNameSpace = baseUrl + "axis/TelApp.jws";
	//wsdl address
	public static final String WSDL= baseUrl + "axis/TelApp.jws";
	//ÿҳ��ʾ������
	public static int PAGE_SIZE = 15;
	public static int THREADPOOL_SIZE = 4;//�̳߳صĴ�С
}
