
package com.blueodin.wifisniffer.helpers;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.providers.WifiScanResult;

public class WifiScanResultAdapter extends ArrayAdapter<WifiScanResult> {
    private Context mContext;

    public WifiScanResultAdapter(Context context) {
        super(context, R.layout.wifiscanresult_list_row,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            Log.i("TESTING", "I had to re-inflate myself");
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.wifiscanresult_list_row, null, false);
        }

        WifiScanResult item = (WifiScanResult) this.getItem(position);

        ((TextView) v.findViewById(R.id.textSSID)).setText(item.ssid);
        ((TextView) v.findViewById(R.id.textBSSID)).setText(item.bssid);
        ((TextView) v.findViewById(R.id.textLevel)).setText(String.format("Level: %d dBm",
                item.level));
        ((TextView) v.findViewById(R.id.textTimestamp)).setText("Timestamp: "
                + DateUtils.getRelativeTimeSpanString(item.timestamp));
        ((ImageView) v.findViewById(R.id.imageViewIcon)).setImageResource(item.getSecurityIcon());

        return v;
    }
}
