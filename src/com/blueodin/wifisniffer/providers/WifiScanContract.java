package com.blueodin.wifisniffer.providers;

import com.blueodin.wifisniffer.helpers.DBHelper;

import android.net.Uri;

public class WifiScanContract {
    // Unique identifier for this contract of content provider.
    public static final String AUTHORITY = "com.blueodin.wifisniffer.provider";
    
    // URI for the content provider.
    public static final String BASE_URI = "content://" + AUTHORITY + "/";
    
    public static final class Results {
        public static final String PATH = "results";
        public static final String PATH_BYID = "result/";
        public static final String PATH_UNIQUE = "unique";
        
        public static final int PATH_BYID_IDPOSITION = 1;
                
        // Static URIs and utility methods for creating them
        public static final Uri CONTENT_URI =  Uri.parse(BASE_URI + PATH);
        public static final Uri CONTENT_UNIQUE_URI = Uri.parse(BASE_URI + PATH_UNIQUE);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(BASE_URI + PATH_BYID);
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(BASE_URI + PATH_BYID + "#");
        
        public static Uri uriById(long id) {
            return Uri.parse(BASE_URI + PATH_BYID + id);
        }

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + ".results";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + ".results";
		public static final String DEFAULT_ORDER_BY = DBHelper.ResultsColumns.COLUMN_TIMESTAMP + " DESC";
    }
}
