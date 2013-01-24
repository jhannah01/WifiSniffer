
package com.blueodin.wifisniffer.providers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;

import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.providers.WifiScanContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WifiScanResult implements Parcelable
{
    public String bssid;
    public String ssid;
    public int level;
    public int frequency;
    public String capabilities;
    public long timestamp;

    private static final String TAG = WifiScanResult.class.toString();

    public WifiScanResult() {
    }

    public WifiScanResult(String bssid, String ssid, int level, int frequency, String capabilities,
            long timestamp)
    {
        this.bssid = bssid;
        this.ssid = ssid;
        this.level = level;
        this.frequency = frequency;
        this.capabilities = capabilities;
        this.timestamp = timestamp;
    }

    public static final Parcelable.Creator<WifiScanResult> CREATOR = new Parcelable.Creator<WifiScanResult>()
    {
        @Override
        public WifiScanResult createFromParcel(Parcel parcel)
        {
            return new WifiScanResult(parcel);
        }

        @Override
        public WifiScanResult[] newArray(int i)
        {
            return new WifiScanResult[i];
        }
    };

    private WifiScanResult(Parcel in)
    {
        bssid = in.readString();
        ssid = in.readString();
        level = in.readInt();
        frequency = in.readInt();
        capabilities = in.readString();
        timestamp = in.readLong();
    }

    public String getFormattedTimestamp() {
        return (String) DateFormat.format("MMM dd, yyyy h:mmaa", this.timestamp);
    }

    public String getFormattedCapabilities() {
        List<String> securities = new ArrayList<String>();
        
        if(capabilities.contains("[WPA2-"))
            securities.add("WPA2");
        
        if(capabilities.contains("[WPA-"))
            securities.add("WPA");
        
        if(capabilities.contains("[WPS]"))
            securities.add("WPS");
        
        if(capabilities.contains("[WEP]"))
            securities.add("WEP");
            
        return Arrays.toString(securities.toArray());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(bssid);
        parcel.writeString(ssid);
        parcel.writeInt(level);
        parcel.writeInt(frequency);
        parcel.writeString(capabilities);
        parcel.writeLong(timestamp);
    }

    public static WifiScanResult fromCursor(Cursor cursor) {
        WifiScanResult entry = null;

        try {
            entry = new WifiScanResult(
                    cursor.getString(cursor.getColumnIndex(WifiScanContract.ScanResult.COLUMN_NAME_BSSID)), // BSSID
                    cursor.getString(cursor.getColumnIndex(WifiScanContract.ScanResult.COLUMN_NAME_SSID)), // SSID
                    cursor.getInt(cursor.getColumnIndex(WifiScanContract.ScanResult.COLUMN_NAME_LEVEL)), // Level
                    cursor.getInt(cursor.getColumnIndex(WifiScanContract.ScanResult.COLUMN_NAME_FREQUENCY)), // Frequency
                    cursor.getString(cursor.getColumnIndex(WifiScanContract.ScanResult.COLUMN_NAME_CAPABILITIES)), // Capabilities
                    cursor.getLong(cursor.getColumnIndex(WifiScanContract.ScanResult.COLUMN_NAME_TIMESTAMP))); // Timestamp
        } catch (Exception ex) {
            Log.e(TAG, "Unable to parse cursor to retrieve network entry: '" + ex.toString() + "'");
            return null;
        }

        return entry;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(WifiScanContract.ScanResult.COLUMN_NAME_BSSID, bssid);
        values.put(WifiScanContract.ScanResult.COLUMN_NAME_SSID, ssid);
        values.put(WifiScanContract.ScanResult.COLUMN_NAME_LEVEL, level);
        values.put(WifiScanContract.ScanResult.COLUMN_NAME_FREQUENCY, frequency);
        values.put(WifiScanContract.ScanResult.COLUMN_NAME_CAPABILITIES, capabilities);
        values.put(WifiScanContract.ScanResult.COLUMN_NAME_TIMESTAMP, timestamp);
        return values;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString()
    {
        return String.format("ScannedResult(bssid=%s,ssid=%s,level=%d,frequency=%d,capabilities=%s,timestamp=%d)",
                        bssid, ssid, level, frequency, capabilities, timestamp);
    }

    public int getSignalIcon() {
        if(level >= -30)
            return R.drawable.ic_bars_four;
        
        if(level >= -60)
            return R.drawable.ic_bars_three;
        
        if(level >= -70)
            return R.drawable.ic_bars_two;
        
        if(level >= -80)
            return R.drawable.ic_bars_one;
        
        return R.drawable.ic_bars_none;
    }
    
    public int getSecurityIcon() {
        if(capabilities.contains("[WPA2-"))
            return R.drawable.ic_wifi_green;
        
        if(capabilities.contains("[WPA-"))
            return R.drawable.ic_wifi_blue;
        
        if(capabilities.contains("[WEP]"))
            return R.drawable.ic_wifi_orange;
        
        return R.drawable.ic_wifi_red;
    }
}
