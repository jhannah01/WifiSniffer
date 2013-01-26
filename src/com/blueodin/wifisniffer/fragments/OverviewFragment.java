package com.blueodin.wifisniffer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.helpers.TabContentFragment;
import com.blueodin.wifisniffer.providers.WifiScanResult;

public class OverviewFragment extends TabContentFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;

		return inflater.inflate(R.layout.fragment_overview, container, false);
	}

	@Override
	public void updateTabContent(WifiScanResult result) {
		View v = getView();
		
		if (v == null)
			return;

		((TextView) v.findViewById(R.id.text_details_BSSID)).setText("BSSID: "
				+ result.bssid);
		((TextView) v.findViewById(R.id.text_details_SSID))
				.setText(result.ssid);
		((TextView) v.findViewById(R.id.text_details_Level)).setText(String
				.format("Level: %d dBm", result.level));
		((TextView) v.findViewById(R.id.text_details_Frequency)).setText(String
				.format("Frequency: %d MHz", result.frequency));
		((TextView) v.findViewById(R.id.text_details_Capabilities))
				.setText("Security: " + result.getFormattedCapabilities());
		((TextView) v.findViewById(R.id.text_details_Timestamp)).setText(result
				.getFormattedTimestamp());
		((ImageView) v.findViewById(R.id.image_details_Signal))
				.setImageResource(result.getSignalIcon());
		((ImageView) v.findViewById(R.id.image_details_Security))
				.setImageResource(result.getSecurityIcon());
	}

	/**/
}
