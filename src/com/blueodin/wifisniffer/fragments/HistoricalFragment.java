
package com.blueodin.wifisniffer.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

import com.blueodin.wifisniffer.MainActivity;
import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.adapters.NetworkExpandableListAdapter;
import com.blueodin.wifisniffer.providers.WifiScanContract;
import com.blueodin.wifisniffer.providers.WifiScanResult;

import java.util.ArrayList;
import java.util.List;

public class HistoricalFragment extends MainDetailFragment {
    private List<WifiScanResult> mResults = new ArrayList<WifiScanResult>();
    private NetworkExpandableListAdapter mListAdapter;

    public HistoricalFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mItem != null)
            mListAdapter = new NetworkExpandableListAdapter(getActivity(), mItem, mResults);
    }

    private void updateListAdapter(WifiScanResult item, boolean clean) {
        if(clean)
            mResults.clear();
        
        Cursor c = getActivity().getContentResolver().query(
                WifiScanContract.ScanResult.CONTENT_URI,
                WifiScanContract.ScanResult.DEFAULT_PROJECTION,
                WifiScanContract.ScanResult.COLUMN_NAME_BSSID + " = '?'", 
                new String[] { item.bssid }, 
                WifiScanContract.ScanResult.DEFAULT_ORDER_BY);

        if ((c.getCount() > 0) && (c.moveToFirst())) {
            while (!c.isAfterLast()) {
                mResults.add(WifiScanResult.fromCursor(c));
                c.moveToNext();
            }
        }
        this.mListAdapter.notifyDataSetChanged();
        c.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historical, container, false);

        ((ExpandableListView) v.findViewById(R.id.listview_historical)).setAdapter(mListAdapter);

        return v;
    }

    @Override
    public void updateSelectedItem(WifiScanResult result) {
        updateListAdapter(result, true);
        
    }
}
