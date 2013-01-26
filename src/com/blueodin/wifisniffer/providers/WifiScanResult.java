
package com.blueodin.wifisniffer.providers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.helpers.DBHelper;
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
    public double longitude;
	public double latitude;
	public double altitude;
	public long timestamp;

    private static final String TAG = WifiScanResult.class.toString();

    public WifiScanResult() {
    }

    public WifiScanResult(String bssid, String ssid, int level, int frequency, String capabilities, double longitude, double latitude, double altitude, long timestamp)
    {
        this.bssid = bssid;
        this.ssid = ssid;
        this.level = level;
        this.frequency = frequency;
        this.capabilities = capabilities;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }

    public static final Parcelable.Creator<WifiScanResult> CREATOR = new Parcelable.Creator<WifiScanResult>()
    {
        @Override
        public WifiScanResult createFromParcel(Parcel parcel) { return new WifiScanResult(parcel); }

        @Override
        public WifiScanResult[] newArray(int i) { return new WifiScanResult[i]; }
    };

    protected WifiScanResult(Parcel in)
    {
        bssid = in.readString();
        ssid = in.readString();
        level = in.readInt();
        frequency = in.readInt();
        capabilities = in.readString();
        longitude = in.readInt();
        latitude = in.readInt();
        altitude = in.readInt();
        timestamp = in.readLong();
    }

    public String getFormattedTimestamp() {
        return (String) DateFormat.format("MMM dd, yyyy h:mmaa", this.timestamp);
    }
    
    public String getRelativeTimestamp(Context context) {
    	return (String) DateUtils.formatDateTime(context, this.timestamp, DateUtils.FORMAT_ABBREV_RELATIVE); 
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
                    cursor.getString(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_BSSID)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_SSID)),
                    cursor.getInt(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_LEVEL)),
                    cursor.getInt(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_FREQUENCY)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_CAPABILITIES)),
                    cursor.getDouble(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_LONGITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_ALTITUDE)),
                    cursor.getLong(cursor.getColumnIndex(DBHelper.ResultsColumns.COLUMN_TIMESTAMP)));
        } catch (Exception ex) {
            Log.e(TAG, "Unable to parse cursor to retrieve network entry: '" + ex.toString() + "'");
            return null;
        }

        return entry;
    }
    
    public static WifiScanResult fromWifiScanResult(android.net.wifi.ScanResult result, Location location) {
    	double lat = 0, lon = 0, alt = 0;
    	if(location != null) {
    		lat = location.getLatitude();
    		lon = location.getLongitude();
    		alt = location.getAltitude();
    	}
    	
    	return new WifiScanResult(result.BSSID, result.SSID, result.level, result.frequency, result.capabilities, lon, lat, alt, System.currentTimeMillis());
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(DBHelper.ResultsColumns.COLUMN_BSSID, bssid);
        values.put(DBHelper.ResultsColumns.COLUMN_SSID, ssid);
        values.put(DBHelper.ResultsColumns.COLUMN_LEVEL, level);
        values.put(DBHelper.ResultsColumns.COLUMN_FREQUENCY, frequency);
        values.put(DBHelper.ResultsColumns.COLUMN_CAPABILITIES, capabilities);
        values.put(DBHelper.ResultsColumns.COLUMN_LONGITUDE, longitude);
        values.put(DBHelper.ResultsColumns.COLUMN_LATITUDE, latitude);
        values.put(DBHelper.ResultsColumns.COLUMN_ALTITUDE, altitude);
        values.put(DBHelper.ResultsColumns.COLUMN_TIMESTAMP, timestamp);
        return values;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString()
    {
        return String.format("ScannedResult(bssid=%s,ssid=%s,level=%d,frequency=%d,capabilities=%s,longitude=%d,latitude=%d,altitude=%d,timestamp=%d)",
        		bssid, ssid, level, frequency, capabilities, longitude, latitude, altitude, timestamp);
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
        
        return R.drawable.ic_action_bars;
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
