package com.blueodin.wifisniffer.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.providers.WifiScanResult;
import com.blueodin.wifisniffer.providers.WifiScanContract;

public class WifiListFragment extends ListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final int LOADER_ID = 0x01;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private IClickHandler mCallbacks = mDefaultClickHandler;
    private ArrayAdapter<WifiScanResult> mAdapter;

    public interface IClickHandler {
        public void onItemSelected(WifiScanResult result);
    }

    private static IClickHandler mDefaultClickHandler = new IClickHandler() {
        @Override
        public void onItemSelected(WifiScanResult result) {
        }
    };

    
    public WifiListFragment() {
        
    }
    
    public class ScanResultsAdapter extends ArrayAdapter<WifiScanResult> {
        private Context mContext;
        
        public ScanResultsAdapter(Context context) {
            super(context, R.layout.wifiscanresult_list_row, R.id.textSSID);
            mContext = context;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.wifiscanresult_list_row, parent, false);
            
            WifiScanResult item = (WifiScanResult)this.getItem(position);
            
            ((TextView)v.findViewById(R.id.textSSID)).setText(item.ssid);
            ((TextView)v.findViewById(R.id.textBSSID)).setText(item.bssid);
            ((TextView)v.findViewById(R.id.textLevel)).setText(String.format("Level: %d dBm", item.level));
            ((TextView)v.findViewById(R.id.textTimestamp)).setText("Timestamp: " + DateUtils.getRelativeTimeSpanString(item.timestamp));
            ((ImageView)v.findViewById(R.id.imageViewIcon)).setImageResource(item.getSecurityIcon());
            
            return v;
        }    
    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mAdapter = new ScanResultsAdapter(getActivity());
        
        setListAdapter(mAdapter);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        
        setEmptyText("No entries...");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof IClickHandler))
            throw new IllegalStateException("Activity must implement fragment's callbacks.");

        mCallbacks = (IClickHandler) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = mDefaultClickHandler;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(mAdapter.getItem(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION)
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION)
            getListView().setItemChecked(mActivatedPosition, false);
        else
            getListView().setItemChecked(position, true);

        mActivatedPosition = position;
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return (new CursorLoader(getActivity(), WifiScanContract.ScanResult.CONTENT_URI, 
               WifiScanContract.ScanResult.DEFAULT_PROJECTION, null, null, null));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if((data == null) || (data.getCount() <= 1))
            return;
        
        data.moveToFirst();
        while(!data.isAfterLast()) {
            mAdapter.add(WifiScanResult.fromCursor(data));
            data.moveToNext();
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        
    }
}
