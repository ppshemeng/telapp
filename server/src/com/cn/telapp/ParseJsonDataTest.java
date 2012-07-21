package com.cn.telapp;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ParseJsonDataTest {

	public  void parseJsonArray(String json)
	{
		 //�����ַ�������JSON����
		 JSONArray resultArray = null;
		try {
			resultArray = new JSONArray(json);
			 JSONObject resultObj = resultArray.optJSONObject(0);
			 
			 //��ȡ������
			 String username = resultObj.getString("username");
			 
			 //��ȡ���ݶ���
			 JSONObject user = resultObj.getJSONObject("user_json");
			 String nickname = user.getString("nickname");
			 System.out.println(username);
			 System.out.println(nickname);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static String jsonToString()
	{
		//����JSONObject����
        JSONObject json = new JSONObject();
        JSONArray num = new JSONArray();
        //��json���������
        try {
			json.put("username", "wanglihong");
	        json.put("height", 12.5);
	        json.put("age", 24);
	        
	        Map map = new HashMap();
	        map.put("aa", 10);
	        map.put("bb", 20);
	        num.put(map);
	        json.put("cc", num);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        //����JSONArray���飬����json��ӵ�����
        JSONArray array = new JSONArray();
        array.put(json);
        array.put(num);
        
        //ת��Ϊ�ַ���
        String jsonStr = array.toString();
        
        System.out.println(jsonStr);
        return jsonStr;
	}
}
