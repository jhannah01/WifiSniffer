package com.blueodin.wifisniffer.helpers;

import com.blueodin.wifisniffer.providers.WifiScanContract;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_FILENAME = "wifisniffer.db";
    private static final int DATABASE_VERSION = 21;
    
    public static final String TABLE_RESULTS = "results";
    public static final String VIEW_UNIQUE_RESULTS = "unique_results";
	public static final String VIEW_NETWORKS = "networks";
	
    public static class ResultsColumns implements BaseColumns {
        public static final String COLUMN_BSSID = "bssid";
        public static final String COLUMN_SSID = "ssid";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_FREQUENCY = "frequency";
        public static final String COLUMN_CAPABILITIES = "capabilities";
        public static final String COLUMN_LONGITUDE = "lon";
        public static final String COLUMN_LATITUDE = "lat";
        public static final String COLUMN_ALTITUDE = "alt";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
    
    private static final String _SQL_CREATE_RESULTS_TABLE = 
    		"CREATE TABLE IF NOT EXISTS " + TABLE_RESULTS + " ( " +
				ResultsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
           	   	ResultsColumns.COLUMN_BSSID + " TEXT NOT NULL," +
           	   	ResultsColumns.COLUMN_SSID + " TEXT," +
           	   	ResultsColumns.COLUMN_LEVEL + " INTEGER, " +
           	   	ResultsColumns.COLUMN_FREQUENCY + " INTEGER, " +
           	   	ResultsColumns.COLUMN_CAPABILITIES + " TEXT, " +
           	   	ResultsColumns.COLUMN_LONGITUDE + " REAL, " +
           	   	ResultsColumns.COLUMN_LATITUDE + " REAL, " +
           	   	ResultsColumns.COLUMN_ALTITUDE + " INTEGER, " +
           	   	ResultsColumns.COLUMN_TIMESTAMP + " INTEGER)";
	
    private static final String _SQL_CREATE_UNIQUE_RESULTS_VIEW = 
    		"CREATE VIEW IF NOT EXISTS " + VIEW_UNIQUE_RESULTS + 
    		" AS SELECT DISTINCT bssid,ssid FROM " + TABLE_RESULTS;
	    
    private static final String _SQL_CREATE_NETWORKS_VIEW = 
    		"CREATE VIEW IF NOT EXISTS " + VIEW_NETWORKS + " AS " + 
    		"SELECT r._id as _id, unique_results.bssid, unique_results.ssid, r.level, r.frequency, r.capabilities, r.lon, r.lat, r.alt, r.timestamp, count(r._id) as _count FROM results " +
    		"AS r LEFT JOIN unique_results ON (unique_results.bssid=r.bssid and unique_results.ssid=r.ssid) " + 
			"GROUP BY unique_results.bssid ORDER BY r.timestamp DESC;";
   
    private static final String[] _SQL_CREATE_STATEMENTS = new String[] {
    	_SQL_CREATE_RESULTS_TABLE,
    	_SQL_CREATE_UNIQUE_RESULTS_VIEW,
    	_SQL_CREATE_NETWORKS_VIEW
    };
    
    private static final String[] _SQL_DROP_STATEMENTS = new String[] {
		"DROP VIEW IF EXISTS " + VIEW_NETWORKS + ";",
		"DROP VIEW IF EXISTS " + VIEW_UNIQUE_RESULTS + ";",
    	"DROP TABLE IF EXISTS " + TABLE_RESULTS + ";",
    };

    public DBHelper(Context context)
    {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
    	for(String sql : _SQL_CREATE_STATEMENTS)
    		db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    	for(String sql : _SQL_DROP_STATEMENTS)
    		db.execSQL(sql);
    	
    	onCreate(db);
    }
    /*
    public Cursor getUniqueResults(SQLiteDatabase db) {
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	queryBuilder.setTables(VIEW_UNIQUE_RESULTS);
    	queryBuilder.appendWhere(ResultsColumns.COLUMN_BSSID + "= ?");
    	queryBuilder.query(db, , selection, selectionArgs, groupBy, having, sortOrder)
    	
    }
    */
}