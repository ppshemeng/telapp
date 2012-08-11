package com.cn.telapp.location;

import java.util.Iterator;

import com.cn.telapp.util.GPRS;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

public class PhoneLocation {
	private LocationManager locManager = null;
	private ConnectivityManager mCM = null;
	private Location location  = null;
	private GpsStatus gpsstatus = null;
	private boolean isgpsopen = false;
	
	public PhoneLocation(LocationManager loc, ConnectivityManager mCM)
	{
		locManager = loc;
		this.mCM = mCM;
	}
	 //��ȡ��ǰλ�õľ�γ��
    public Location getLocation()
    {
    	//ͨ��gps���ж�λ
    	getGPSLocation();
    	
    	//���gps��λʧ�ܣ�����ͨ��������ж�λ
    	if( location == null)
    	{
    		getNETWORKLocation();
    	}
    	return location;
    }
    
    //ͨ��gps��λ
    private void getGPSLocation()
    {
    	boolean listener = false;
    	
    	//�ж�gps�Ƿ��������û�п� ���û�����
    	openGPSSettings();
    	if (!isgpsopen)
    		return;
    	
        //�������õ�Criteria���󣬻�ȡ����ϴ˱�׼��provider����
        String currentProvider = locManager.getProvider(LocationManager.GPS_PROVIDER).getName();
        
        //���ݵ�ǰprovider�����ȡ���һ��λ����Ϣ
        location = locManager.getLastKnownLocation(currentProvider);
        //���λ����ϢΪnull�����������λ����Ϣ
        if(location == null)
        {
        	locManager.requestLocationUpdates(currentProvider, 0, 0, locationListener);
        	listener = true;
        }
        //����GPS״̬������
       // locManager.addGpsStatusListener(gpsListener);
        int timeout = 0;
        while (location == null)
		{
			try {
				Thread.sleep(1000);
				timeout++;
				if (timeout > 5) break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
        
        //locManager.removeGpsStatusListener(gpsListener);
        if (listener)
        {
        	locManager.removeUpdates(locationListener);
        	listener = false;
        }
    }
    //ͨ�����綨λ
    private void getNETWORKLocation()
    {
    	boolean listener = false;
    	
    	//��¼gprs�Ƿ���
    	boolean isopengprs = false;
    	GPRS gprs = new GPRS(mCM);
    	isopengprs = gprs.gprsIsOpenMethod();
    	if (!isopengprs)
    	{
    		gprs.setGprsEnabled(true);
    	}
		// gps��λʧ�ܣ�����ͨ�����綨λ
		String currentProvider = locManager.getProvider(
				LocationManager.NETWORK_PROVIDER).getName();

		// ���ݵ�ǰprovider�����ȡ���һ��λ����Ϣ
		location = locManager
				.getLastKnownLocation(currentProvider);
		// ���λ����ϢΪnull�����������λ����Ϣ
		if (location == null) {
			locManager.requestLocationUpdates(currentProvider, 0, 0,
					locationListener);
			listener = true;
		}
		int timeout = 0;
		
		 while (location == null)
			{
				try {
					Thread.sleep(1000);
					timeout++;
					if (timeout > 5) break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		 
		if (listener) {
			locManager.removeUpdates(locationListener);
		}
		
		//�ָ�gprsԭ����״̬
		if (!isopengprs)
    	{
    		gprs.setGprsEnabled(isopengprs);
    	}
    }
    
    private void openGPSSettings() 
	{
        if (locManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
        	isgpsopen = true;
            return;
        }
    }
	
	 private GpsStatus.Listener gpsListener = new GpsStatus.Listener(){
         //GPS״̬�����仯ʱ����
         @Override
         public void onGpsStatusChanged(int event) {
             //��ȡ��ǰ״̬
             gpsstatus=locManager.getGpsStatus(null);
             switch(event){
                 //��һ�ζ�λʱ���¼�
                 case GpsStatus.GPS_EVENT_FIRST_FIX:
                     break;
                 //��ʼ��λ���¼�
                 case GpsStatus.GPS_EVENT_STARTED:
                     break;
                 //����GPS����״̬�¼�
                 case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    // Toast.makeText(TelAppActivity.this, "GPS_EVENT_SATELLITE_STATUS", Toast.LENGTH_SHORT).show();
                     Iterable<GpsSatellite> allSatellites = gpsstatus.getSatellites();   
                     Iterator<GpsSatellite> it=allSatellites.iterator(); 
                     int count = 0;
                     while(it.hasNext())   
                     {   
                         count++;
                     }
                   //  Toast.makeText(TelAppActivity.this, "Satellite Count:" + count, Toast.LENGTH_SHORT).show();
                     break;
                 //ֹͣ��λ�¼�
                 case GpsStatus.GPS_EVENT_STOPPED:
                     Log.d("Location", "GPS_EVENT_STOPPED");
                     break;
             }
         }
     };
     
     
     //����λ�ü�����
     private LocationListener locationListener = new LocationListener(){
         //λ�÷����ı�ʱ����
         @Override
         public void onLocationChanged(Location loc) {
        	 location = loc;
         }
 
         //providerʧЧʱ����
         @Override
         public void onProviderDisabled(String provider) {
         }
 
         //provider����ʱ����
         @Override
         public void onProviderEnabled(String provider) {
         }
 
         //״̬�ı�ʱ����
         @Override
         public void onStatusChanged(String provider, int status, Bundle extras) {
         }
     };
}
