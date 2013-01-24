package com.blueodin.wifisniffer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.providers.WifiScanResult;

public class OverviewFragment extends MainDetailFragment {
    public OverviewFragment() {
        
    }
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_overview, container, false);

        if(mItem == null)
            return v;

        updateItemDetails(v);
        
        return v;
    }

    private void updateItemDetails(View v) {
        ((TextView)v.findViewById(R.id.text_details_BSSID)).setText("BSSID: " + mItem.bssid);
        ((TextView)v.findViewById(R.id.text_details_SSID)).setText(mItem.ssid);
        ((TextView)v.findViewById(R.id.text_details_Level)).setText(String.format("Level: %d dBm", mItem.level));
        ((TextView)v.findViewById(R.id.text_details_Frequency)).setText(String.format("Frequency: %d MHz", mItem.frequency));
        ((TextView)v.findViewById(R.id.text_details_Capabilities)).setText("Security: " + mItem.getFormattedCapabilities());
        ((TextView)v.findViewById(R.id.text_details_Timestamp)).setText(mItem.getFormattedTimestamp());
        ((ImageView)v.findViewById(R.id.image_details_Signal)).setImageResource(mItem.getSignalIcon());
        ((ImageView)v.findViewById(R.id.image_details_Security)).setImageResource(mItem.getSecurityIcon());
    }

    @Override
    public void updateSelectedItem(WifiScanResult result) {
        this.mItem = result;
        updateItemDetails(getView());
    }
}
