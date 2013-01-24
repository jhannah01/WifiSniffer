
package com.blueodin.wifisniffer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Fragment;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blueodin.wifisniffer.fragments.HistoricalFragment;
import com.blueodin.wifisniffer.fragments.MainDetailFragment;
import com.blueodin.wifisniffer.fragments.MapDetailFragment;
import com.blueodin.wifisniffer.fragments.OverviewFragment;
import com.blueodin.wifisniffer.fragments.WifiListFragment.IClickHandler;
import com.blueodin.wifisniffer.providers.ScanResultsProvider;
import com.blueodin.wifisniffer.providers.WifiScanContract;
import com.blueodin.wifisniffer.providers.WifiScanResult;
import com.blueodin.wifisniffer.R;

public class MainActivity extends Activity implements ActionBar.TabListener, IClickHandler {
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int TAB_OVERVIEW = 0;
    private static final int TAB_MAP = 1;
    private static final int TAB_HISTORY = 2;
    
    private WifiScanResult mItem;
    
    public interface IUpdateFragment {
        void updateSelectedItem(WifiScanResult result);
        void parseArguments(Bundle arguments);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        actionBar.addTab(actionBar.newTab().setText("Overview").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Map").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("History").setTabListener(this));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM))
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_add_entries:
                addFakeEntries();
                return true;
            case R.id.menu_exit:
                finish();
                return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void addFakeEntries() {
        WifiScanResult[] results = {
                new WifiScanResult("00:11:22:33:44:55", "fakeAP", -45, 2505, "[WEP][ESS]", System.currentTimeMillis()),
                new WifiScanResult("ba:db:ee:fd:ea:d1", "myWiFi", -32, 2100, "[WPA2-PSK-TKIP][WPS][ESS]", System.currentTimeMillis() - (30*1000)),
                new WifiScanResult("fe:f0:dd:00:44:11", "wireless", -55, 2350, "[WPA2-PSK-TKIP][ESS]", System.currentTimeMillis() - (40*1000)),
                new WifiScanResult("00:11:22:33:44:55", "fakeAP", -45, 2505, "[WEP][ESS]", System.currentTimeMillis() - (5*60*1000)),
                new WifiScanResult("ba:db:ee:fd:ea:d1", "myWiFi", -32, 2100, "[WPA2-PSK-TKIP][WPS][ESS]", System.currentTimeMillis() - (6*60*1000)),
                new WifiScanResult("fe:f0:dd:00:44:11", "wireless", -55, 2350, "[WPA2-PSK-TKIP][ESS]", System.currentTimeMillis() - (7*60*1000))
        };
        
        for(WifiScanResult r : results)
            getContentResolver().insert(WifiScanContract.ScanResult.CONTENT_URI, r.getContentValues());
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Fragment f;
        
        switch(tab.getPosition()) {
            case TAB_OVERVIEW:
                f = new OverviewFragment();
                break;
            case TAB_MAP:
                f = new MapDetailFragment();
                break;
            case TAB_HISTORY:
                f = new HistoricalFragment();
                break;
            default:
                throw new IllegalArgumentException("Invalid Tab Position: #" + tab.getPosition());
        }
        
        Bundle args = new Bundle();
        args.putParcelable(MainDetailFragment.ARG_ITEM, mItem);
        f.setArguments(args);
        
        getFragmentManager().beginTransaction()
            .replace(R.id.container, f)
            .commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onItemSelected(WifiScanResult result) {
        IUpdateFragment f = (IUpdateFragment)getFragmentManager().findFragmentById(R.id.container);
        f.updateSelectedItem(result);
    }
}
