package com.blueodin.wifisniffer;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.blueodin.wifisniffer.fragments.HistoricalFragment;
import com.blueodin.wifisniffer.fragments.MapDetailFragment;
import com.blueodin.wifisniffer.fragments.OverviewFragment;
import com.blueodin.wifisniffer.fragments.WifiListFragment;
import com.blueodin.wifisniffer.fragments.WifiListFragment.IClickHandler;
import com.blueodin.wifisniffer.helpers.TabContentFragment;
import com.blueodin.wifisniffer.providers.WifiScanContract;
import com.blueodin.wifisniffer.providers.WifiScanResult;
import com.blueodin.wifisniffer.services.ScanResultsReceiver;
import com.blueodin.wifisniffer.services.WifiLockService;
import com.blueodin.wifisniffer.R;

public class MainActivity extends Activity implements IClickHandler {
	public static final String FLAG_FROM_NOTIFICATION = "arg_from_notification";
	private MenuItem mToggleScanningItem;
	private boolean mIsScanning = false;
    private Location mLastLocation;
    private LocationManager mLocationManager;
    
	private ScanResultsReceiver mResultsReceiver = new ScanResultsReceiver() {
		@Override
		public void updateResults(List<ScanResult> results) {
			if(mLastLocation == null)
				mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			for (ScanResult result : results)
				getContentResolver().insert(
						WifiScanContract.Results.CONTENT_URI,
						WifiScanResult.fromWifiScanResult(result, mLastLocation)
								.getContentValues());

			Log.d(TAG, "Inserted " + results.size() + " entries into the DB");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		setContentView(R.layout.activity_main);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
		
		actionBar.addTab(actionBar.newTab()
				.setText("Overview")
				.setTabListener(new TabListener(new OverviewFragment())));
		
		actionBar.addTab(actionBar.newTab()
				.setText("Historical")
				.setTabListener(new TabListener(new HistoricalFragment())));
		
		actionBar.addTab(actionBar.newTab()
				.setText("Map Results")
				.setTabListener(new TabListener(new MapDetailFragment())));
		
		getFragmentManager().beginTransaction()
			.add(R.id.main_left_fragment, new WifiListFragment())
			.commit();
	}
	
	public class TabListener implements ActionBar.TabListener {
		private TabContentFragment mFragment;

		public TabListener(TabContentFragment fragment) {
			mFragment = fragment;
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.add(R.id.main_detail_fragment, mFragment);
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(mFragment);
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			WifiListFragment f = (WifiListFragment)getFragmentManager().findFragmentById(R.id.main_left_fragment);
			mFragment.updateTabContent((WifiScanResult)f.getListView().getSelectedItem());
		}

	}

	@Override
	public void onPause() {
		if(mIsScanning)
			unregisterReceiver(mResultsReceiver);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(mIsScanning)
			registerReceiver(mResultsReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		mToggleScanningItem = menu.findItem(R.id.menu_toggle_scanning);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_toggle_scanning:
			toggleScanning();
			return true;
		case R.id.menu_exit:
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void toggleScanning() {
		if (mIsScanning) {
			stopScanning();
			mToggleScanningItem.setChecked(false).setIcon(
					R.drawable.ic_action_wifi);
		} else {
			startScanning();
			mToggleScanningItem.setChecked(true).setIcon(
					R.drawable.ic_wifi_orange);
		}

		mIsScanning = !mIsScanning;
	}

	private void startScanning() {
		Intent serviceIntent = new Intent(this, WifiLockService.class);
		startService(serviceIntent);
		registerReceiver(mResultsReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	private void stopScanning() {
		unregisterReceiver(mResultsReceiver);
		stopService(new Intent(this, WifiLockService.class));
	}

	@Override
	public void onItemSelected(WifiScanResult result) {
		((TabContentFragment)getFragmentManager().findFragmentById(R.id.main_detail_fragment)).updateTabContent(result);
	}
}
