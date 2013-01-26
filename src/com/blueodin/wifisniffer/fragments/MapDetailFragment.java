package com.blueodin.wifisniffer.fragments;

import java.util.ArrayList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.helpers.TabContentFragment;
import com.blueodin.wifisniffer.providers.WifiScanResult;

public class MapDetailFragment extends TabContentFragment {
	private static final String ARG_RESULTS = "arg_results";
			
	private ArrayList<WifiScanResult> mResults = null;
	private GoogleMap mMap = null;

	public static MapDetailFragment newInstance(ArrayList<WifiScanResult> results) {
		MapDetailFragment f = new MapDetailFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList(ARG_RESULTS, results);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null)
			return null;
		
		View v = inflater.inflate(R.layout.fragment_mapdetail, container, false);
		
		if(!parseArguments())
			return v;
		
		setUpMap();
		
		return v;
	}
	
	private boolean parseArguments() {
		Bundle args = getArguments();
		
		if((args == null) || (!args.containsKey(ARG_RESULTS)))
			return false;
		
		mResults = args.getParcelableArrayList(ARG_RESULTS);
		return true;
	}

	private void setUpMap() {
		MapFragment mapFragment = MapFragment.newInstance();
		
		getFragmentManager().beginTransaction()
			.replace(R.id.map_detail_fragment, mapFragment)
			.commit();
		
		
		mMap = mapFragment.getMap();
		
		if (mMap == null)
			return;

		LatLng myLocation = new LatLng(39.780899, -75.881798);
		mMap.addMarker(new MarkerOptions()
				.title("Hiya")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
				.position(myLocation));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
	}

	@Override
	public void updateTabContent(WifiScanResult result) {
		
	}
}
