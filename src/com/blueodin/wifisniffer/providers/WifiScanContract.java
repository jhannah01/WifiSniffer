package com.blueodin.wifisniffer.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class WifiScanContract {
    // Unique identifier for this contract of content provider.
    public static final String AUTHORITY = "com.blueodin.wifisniffer.provider.results";
    
    // URI for the content provider.
    public static final String BASE_URI = "content://" + AUTHORITY + "/";
    
    public static final class ScanResult implements BaseColumns
    {
        public static final String TABLE_NAME = "results";

        public static final String PATH = "results";
        public static final String PATH_BYID = "result/";
        
        // Position in the path (0-index based) where to find the ID
        public static final int PATH_BYID_IDPOSITION = 1;
        
        // Static URIs and utility methods for creating them
        public static final Uri CONTENT_URI =  Uri.parse(BASE_URI + PATH);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(BASE_URI + PATH_BYID);
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(BASE_URI + PATH_BYID + "#");
        
        public static Uri uriById(long id) {
            return Uri.parse(BASE_URI + PATH_BYID + id);
        }

        // Type for directory of all results
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + TABLE_NAME;

        // Type for a single result
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + TABLE_NAME;

        // Columns
        public static final String COLUMN_NAME_BSSID = "bssid";
        public static final String COLUMN_NAME_SSID = "ssid";
        public static final String COLUMN_NAME_LEVEL = "level";
        public static final String COLUMN_NAME_FREQUENCY = "frequency";
        public static final String COLUMN_NAME_CAPABILITIES = "capabilities";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
        public static final String COLUMN_NAME_ALT = "alt";
        public static final String COLUMN_NAME_GEOHASH = "geohash";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        
        // Default order
        public static final String DEFAULT_ORDER_BY = COLUMN_NAME_TIMESTAMP + " DESC";

        public static final String[] DEFAULT_PROJECTION = {
            COLUMN_NAME_BSSID,
            COLUMN_NAME_SSID,
            COLUMN_NAME_LEVEL,
            COLUMN_NAME_FREQUENCY,
            COLUMN_NAME_CAPABILITIES,
            //COLUMN_NAME_LAT,
            //COLUMN_NAME_LON,
            //COLUMN_NAME_ALT,
            //COLUMN_NAME_GEOHASH,
            COLUMN_NAME_TIMESTAMP
        };
    }

    public static final class ScanLocation implements BaseColumns
    {
        public static final String TABLE_NAME = "location";

        public static final String PATH = "locations";
        public static final String PATH_BYID = "location/";
        
        // Position in the path (0-index based) where to find the ID
        public static final int PATH_BYID_IDPOSITION = 1;
        
        // Static URIs and utility methods for creating them
        public static final Uri CONTENT_URI =  Uri.parse(BASE_URI + PATH);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(BASE_URI + PATH_BYID);
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(BASE_URI + PATH_BYID + "#");
        
        public static Uri uriById(long id) {
            return Uri.parse(BASE_URI + PATH_BYID + id);
        }

        // Type for directory of all locations
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + TABLE_NAME;
        
        // Content type of a single location
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + TABLE_NAME;

        // Columns
        public static final String COLUMN_NAME_FK_RESULT = "fk_result";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
        public static final String COLUMN_NAME_ALT = "alt";
        public static final String COLUMN_NAME_GEOHASH = "geohash";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_LEVEL = "level";

        // Order by default
        public static final String DEFAULT_ORDER_BY = COLUMN_NAME_TIMESTAMP + " DESC";
        
        public static final String[] DEFAULT_PROJECTION = {
            COLUMN_NAME_FK_RESULT,
            COLUMN_NAME_LAT,
            COLUMN_NAME_LON,
            COLUMN_NAME_ALT,
            COLUMN_NAME_GEOHASH,
            COLUMN_NAME_TIMESTAMP
        };
    }
}
