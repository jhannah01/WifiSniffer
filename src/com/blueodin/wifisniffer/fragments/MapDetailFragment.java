package com.blueodin.wifisniffer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import com.blueodin.wifisniffer.MainActivity;
import com.blueodin.wifisniffer.providers.WifiScanResult;

public class MapDetailFragment extends MapFragment implements MainActivity.IUpdateFragment {
    protected WifiScanResult mItem;
    private GoogleMap mMap = null;
    
    public MapDetailFragment() {
        
    }
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseArguments(getArguments());
    }
    
    private void setUpMap() {
        LatLng myLocation = new LatLng(39.780899, -75.881798);
        mMap.addMarker(new MarkerOptions().title("Hiya").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).position(myLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        
    }

    @Override
    public void updateSelectedItem(WifiScanResult result) {
        mItem = result;
        
        mMap = this.getMap();
        
        if(mMap != null)
            setUpMap();
    }

    @Override
    public void parseArguments(Bundle arguments) {
        if(arguments == null)
            return;
        
        if(arguments.containsKey(MainDetailFragment.ARG_ITEM))
            mItem = arguments.getParcelable(MainDetailFragment.ARG_ITEM);
    }
}
