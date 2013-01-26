package com.blueodin.wifisniffer.helpers;

import com.blueodin.wifisniffer.providers.WifiScanResult;

import android.app.Fragment;

public abstract class TabContentFragment extends Fragment {
	public TabContentFragment() {
		
	}
	
	public abstract void updateTabContent(WifiScanResult result); 
}