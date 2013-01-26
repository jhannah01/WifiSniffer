package com.blueodin.wifisniffer.services;

import com.blueodin.wifisniffer.MainActivity;
import com.blueodin.wifisniffer.R;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class WifiLockService extends Service {
	public static final String SET_SCAN_INTERVAL = "SET_SCAN_INTERVAL";
	public static final int DEFAULT_SCAN_INTERVAL = 5*1000;
	
	private static final String TAG = "WifiLockService";
	private static final int NOTIFICATION_SERVICE_ID = 1;
	
	private final IBinder mBinder = new WifiServiceBinder();
	private WifiScanManager mWifiScanManager;
	private int mScanInterval = DEFAULT_SCAN_INTERVAL;
	private NotificationManager mNotificationManager;
 
    public class WifiServiceBinder extends Binder {
        public WifiLockService getService() {
            return WifiLockService.this; 
        }
    }
    
    @Override
	public IBinder onBind(Intent intent) {
    	Log.i(TAG, "onBind(" + intent.toString() + ") completed.");
    	return this.mBinder;
	}
    
    @Override
    public void onRebind(Intent intent) {
    	super.onRebind(intent);
    	Log.i(TAG, "onRebind(" + intent.toString() + ") completed.");
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
    	Log.i(TAG, "onUnbind(" + intent.toString() + ") completed.");
    	return true;
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		
		if((bundle != null) && (bundle.containsKey(SET_SCAN_INTERVAL)))
			this.mScanInterval = bundle.getInt(SET_SCAN_INTERVAL);
    	
    	this.mWifiScanManager = new WifiScanManager(this, this.mScanInterval);
    	
    	Log.i(TAG, "Got a request to start. Scan interval: " + this.mScanInterval);
		
		showNotification();
    	return START_STICKY;
    }
	
	private void showNotification() {
	    this.mNotificationManager.notify(NOTIFICATION_SERVICE_ID, buildNotification());
	}
	
	private void closeNotification() {
		this.mNotificationManager.cancel(NOTIFICATION_SERVICE_ID);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private Notification buildNotification() {
    	Context ctx = getApplicationContext();
    	
    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
    	stackBuilder.addParentStack(MainActivity.class);
    	
    	Intent resultIntent = new Intent(this, MainActivity.class);
    	resultIntent.putExtra(MainActivity.FLAG_FROM_NOTIFICATION, true);
    	stackBuilder.addNextIntent(resultIntent);
    	
    	return (new Notification.Builder(ctx))
    		.setContentText(ctx.getText(R.string.notif_service_content))
    		.setContentTitle(ctx.getText(R.string.notif_service_title))
    		.setSmallIcon(R.drawable.ic_action_bars)
    		.setOngoing(true) // are you sure?
    		.setAutoCancel(false)
			.setWhen(System.currentTimeMillis())
			.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))
			.build();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Stopping WifiLockService...");
        mWifiScanManager.stop();
        mWifiScanManager = null;
        closeNotification();
    }
    
    public int getScanInterval() {
    	return this.mScanInterval;
    }
    
    public void setScanInterval(int value) {
    	this.mScanInterval = value;
    	this.mWifiScanManager.stop();
    	this.mWifiScanManager = new WifiScanManager(this, this.mScanInterval);
    }

}
