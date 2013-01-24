package com.blueodin.wifisniffer.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blueodin.wifisniffer.providers.WifiScanContract;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_FILENAME = "wifisniffer.db";
    private static final int DATABASE_VERSION = 4;

    private static final String _SQL_CREATE_RESULTS_TABLE = 
            "CREATE TABLE IF NOT EXISTS " + WifiScanContract.ScanResult.TABLE_NAME + " (" 
            + WifiScanContract.ScanResult._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WifiScanContract.ScanResult.COLUMN_NAME_BSSID + " TEXT NOT NULL,"
            + WifiScanContract.ScanResult.COLUMN_NAME_SSID + " TEXT, "
            + WifiScanContract.ScanResult.COLUMN_NAME_LEVEL + " INTEGER, "
            + WifiScanContract.ScanResult.COLUMN_NAME_FREQUENCY + " INTEGER, "
            + WifiScanContract.ScanResult.COLUMN_NAME_CAPABILITIES + " TEXT, "
            + WifiScanContract.ScanResult.COLUMN_NAME_LAT + " REAL, "
            + WifiScanContract.ScanResult.COLUMN_NAME_LON + " REAL, "
            + WifiScanContract.ScanResult.COLUMN_NAME_GEOHASH + " TEXT, "
            + WifiScanContract.ScanResult.COLUMN_NAME_TIMESTAMP + " INTEGER);";           

    private static final String _SQL_CREATE_LOCATION_TABLE =
            "CREATE TABLE IF NOT EXISTS " + WifiScanContract.ScanLocation.TABLE_NAME + " ("
            + WifiScanContract.ScanLocation._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WifiScanContract.ScanLocation.COLUMN_NAME_FK_RESULT + " TEXT, "
            + WifiScanContract.ScanLocation.COLUMN_NAME_LAT + " REAL, "
            + WifiScanContract.ScanLocation.COLUMN_NAME_LON + " REAL, "
            + WifiScanContract.ScanLocation.COLUMN_NAME_ALT + " REAL, "
            + WifiScanContract.ScanLocation.COLUMN_NAME_GEOHASH + " TEXT, "
            + WifiScanContract.ScanLocation.COLUMN_NAME_TIMESTAMP + " INTEGER, "
            + WifiScanContract.ScanLocation.COLUMN_NAME_LEVEL + " INTEGER);";
    
    private static final String _SQL_DROP_TABLES = 
            "DROP TABLE IF EXISTS " + WifiScanContract.ScanResult.TABLE_NAME + ";" +
            "DROP TABLE IF EXISTS " + WifiScanContract.ScanLocation.TABLE_NAME + ";";

    public DBHelper(Context context)
    {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(_SQL_CREATE_RESULTS_TABLE);
        db.execSQL(_SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(_SQL_DROP_TABLES);
        onCreate(db);
    }
}
