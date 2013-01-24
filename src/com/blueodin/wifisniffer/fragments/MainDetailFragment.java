package com.blueodin.wifisniffer.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.blueodin.wifisniffer.MainActivity;
import com.blueodin.wifisniffer.MainActivity.IUpdateFragment;
import com.blueodin.wifisniffer.providers.WifiScanResult;

public abstract class MainDetailFragment extends Fragment implements IUpdateFragment {
    protected WifiScanResult mItem;
    
    public static final String ARG_ITEM = "arg_item";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseArguments(getArguments());
    }
    
    @Override
    public void parseArguments(Bundle arguments) {
        if(arguments == null)
            return;
        
        if(arguments.containsKey(ARG_ITEM))
            mItem = arguments.getParcelable(ARG_ITEM);
    }

}
