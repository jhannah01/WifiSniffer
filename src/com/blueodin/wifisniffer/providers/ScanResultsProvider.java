package com.blueodin.wifisniffer.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.blueodin.wifisniffer.helpers.DBHelper;
import com.blueodin.wifisniffer.providers.WifiScanContract;

import java.util.HashMap;

public class ScanResultsProvider extends ContentProvider {
    private static final HashMap<String, String> sProjectionMap = new HashMap<String, String>();
    private static final HashMap<String, String> sProjectionMapLocations = new HashMap<String, String>();
    
    private static final UriMatcher sUriMatcher;
    
    private static final int URIMATCH_SCANRESULTS = 0x01;
    private static final int URIMATCH_SCANRESULT_BYID = 0x02;
    private static final int URIMATCH_SCANLOCATIONS = 0x03;
    private static final int URIMATCH_SCANLOCATION_BYID = 0x04;
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(WifiScanContract.AUTHORITY, WifiScanContract.ScanResult.PATH, URIMATCH_SCANRESULTS);
        sUriMatcher.addURI(WifiScanContract.AUTHORITY, WifiScanContract.ScanResult.PATH_BYID + "#", URIMATCH_SCANRESULT_BYID);
        sUriMatcher.addURI(WifiScanContract.AUTHORITY, WifiScanContract.ScanLocation.PATH, URIMATCH_SCANLOCATIONS);
        sUriMatcher.addURI(WifiScanContract.AUTHORITY, WifiScanContract.ScanLocation.PATH_BYID + "#", URIMATCH_SCANLOCATION_BYID);
        
        sProjectionMap.put(WifiScanContract.ScanResult._ID, WifiScanContract.ScanResult._ID);
        sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_BSSID, WifiScanContract.ScanResult.COLUMN_NAME_BSSID);
        sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_SSID, WifiScanContract.ScanResult.COLUMN_NAME_SSID);
        sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_LEVEL, WifiScanContract.ScanResult.COLUMN_NAME_LEVEL);
        sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_FREQUENCY, WifiScanContract.ScanResult.COLUMN_NAME_FREQUENCY);
        sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_CAPABILITIES, WifiScanContract.ScanResult.COLUMN_NAME_CAPABILITIES);
        //sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_LAT, WifiScanContract.ScanResult.COLUMN_NAME_LAT);
        //sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_LON, WifiScanContract.ScanResult.COLUMN_NAME_LON);
        //sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_ALT, WifiScanContract.ScanResult.COLUMN_NAME_ALT);
        //sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_GEOHASH, WifiScanContract.ScanResult.COLUMN_NAME_GEOHASH);
        sProjectionMap.put(WifiScanContract.ScanResult.COLUMN_NAME_TIMESTAMP, WifiScanContract.ScanResult.COLUMN_NAME_TIMESTAMP);
        
        sProjectionMapLocations.put(WifiScanContract.ScanLocation._ID, WifiScanContract.ScanLocation._ID);
        sProjectionMapLocations.put(WifiScanContract.ScanLocation.COLUMN_NAME_FK_RESULT, WifiScanContract.ScanLocation.COLUMN_NAME_FK_RESULT);
        sProjectionMapLocations.put(WifiScanContract.ScanLocation.COLUMN_NAME_LAT, WifiScanContract.ScanLocation.COLUMN_NAME_LAT);
        sProjectionMapLocations.put(WifiScanContract.ScanLocation.COLUMN_NAME_LON, WifiScanContract.ScanLocation.COLUMN_NAME_LON);
        sProjectionMapLocations.put(WifiScanContract.ScanLocation.COLUMN_NAME_ALT, WifiScanContract.ScanLocation.COLUMN_NAME_ALT);
        sProjectionMapLocations.put(WifiScanContract.ScanLocation.COLUMN_NAME_GEOHASH, WifiScanContract.ScanLocation.COLUMN_NAME_GEOHASH);
        sProjectionMapLocations.put(WifiScanContract.ScanLocation.COLUMN_NAME_TIMESTAMP, WifiScanContract.ScanLocation.COLUMN_NAME_TIMESTAMP);
        sProjectionMapLocations.put(WifiScanContract.ScanLocation.COLUMN_NAME_LEVEL, WifiScanContract.ScanLocation.COLUMN_NAME_LEVEL);
    }
    
    private DBHelper mDbHelper;
    
    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        return true;
    }
    
    @Override
    public void shutdown() {
        super.shutdown();
        mDbHelper.close();
    }
    
    @Override
    public String getType(Uri uri)
    {
        switch (sUriMatcher.match(uri))
        {
            case URIMATCH_SCANRESULTS:
                return WifiScanContract.ScanResult.CONTENT_TYPE;
            case URIMATCH_SCANRESULT_BYID:
                return WifiScanContract.ScanResult.CONTENT_ITEM_TYPE;
            case URIMATCH_SCANLOCATIONS:
                return WifiScanContract.ScanLocation.CONTENT_TYPE;
            case URIMATCH_SCANLOCATION_BYID:
                return WifiScanContract.ScanLocation.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String orderBy;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        
        switch(sUriMatcher.match(uri)) {
            case URIMATCH_SCANRESULT_BYID:
                queryBuilder.appendWhere(WifiScanContract.ScanResult._ID + "=" + uri.getPathSegments().get(WifiScanContract.ScanResult.PATH_BYID_IDPOSITION));
            case URIMATCH_SCANRESULTS:
                queryBuilder.setTables(WifiScanContract.ScanResult.TABLE_NAME);
                queryBuilder.setProjectionMap(sProjectionMap);
                orderBy = WifiScanContract.ScanResult.DEFAULT_ORDER_BY;
                break;
            
            case URIMATCH_SCANLOCATION_BYID:
                queryBuilder.appendWhere(WifiScanContract.ScanLocation._ID + "=" + uri.getPathSegments().get(WifiScanContract.ScanLocation.PATH_BYID_IDPOSITION));
            case URIMATCH_SCANLOCATIONS:
                queryBuilder.setTables(WifiScanContract.ScanLocation.TABLE_NAME);
                queryBuilder.setProjectionMap(sProjectionMapLocations);
                orderBy = WifiScanContract.ScanLocation.DEFAULT_ORDER_BY;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        
        if(!TextUtils.isEmpty(sortOrder))
            orderBy = sortOrder;
            
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        
        Cursor c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        
        return c;
    }
    
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        int uriMatch = sUriMatcher.match(uri);
        Uri baseUri;
        String tableName;
        
        switch(uriMatch) {
            case URIMATCH_SCANRESULTS:
                baseUri = WifiScanContract.ScanResult.CONTENT_ID_URI_BASE;
                tableName = WifiScanContract.ScanResult.TABLE_NAME;
                break;
            case URIMATCH_SCANLOCATIONS:
                baseUri = WifiScanContract.ScanLocation.CONTENT_ID_URI_BASE;
                tableName = WifiScanContract.ScanLocation.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        
        if(values == null)
            values = new ContentValues();
        
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        
        long rowId = db.insert(tableName, null, values);
        
        if (rowId < 1)
            throw new SQLException("Failed to insert row for URI: " + uri);
        
        Uri insertedUri = ContentUris.withAppendedId(baseUri, rowId);
        
        getContext().getContentResolver().notifyChange(insertedUri, null);
        
        return insertedUri;
            
    }
    
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        String finalWhere;
        
        switch(sUriMatcher.match(uri)) {
            case URIMATCH_SCANRESULTS:
                count = db.delete(WifiScanContract.ScanResult.TABLE_NAME, where, whereArgs);
                break;
            case URIMATCH_SCANRESULT_BYID:
                finalWhere = (WifiScanContract.ScanResult._ID + " = " + uri.getPathSegments().get(WifiScanContract.ScanResult.PATH_BYID_IDPOSITION));
                if(!TextUtils.isEmpty(where))
                    finalWhere = finalWhere + " AND " + where;
                
                count = db.delete(WifiScanContract.ScanResult.TABLE_NAME, finalWhere, whereArgs);
                break;
            case URIMATCH_SCANLOCATIONS:
                count = db.delete(WifiScanContract.ScanLocation.TABLE_NAME, where, whereArgs);
                break;
            case URIMATCH_SCANLOCATION_BYID:
                finalWhere = (WifiScanContract.ScanLocation._ID + " = " + uri.getPathSegments().get(WifiScanContract.ScanLocation.PATH_BYID_IDPOSITION));
                if(!TextUtils.isEmpty(where))
                    finalWhere = finalWhere + " AND " + where;
                
                count = db.delete(WifiScanContract.ScanLocation.TABLE_NAME, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        
        if(count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        String finalWhere;
        
        switch(sUriMatcher.match(uri)) {
            case URIMATCH_SCANRESULTS:
                count = db.update(WifiScanContract.ScanResult.TABLE_NAME, values, where, whereArgs);
                break;
            case URIMATCH_SCANRESULT_BYID:
                finalWhere = (WifiScanContract.ScanResult._ID + " = " + uri.getPathSegments().get(WifiScanContract.ScanResult.PATH_BYID_IDPOSITION));
                if(!TextUtils.isEmpty(where))
                    finalWhere = finalWhere + " AND " + where;
                
                count = db.update(WifiScanContract.ScanResult.TABLE_NAME, values, finalWhere, whereArgs);
                break;
            case URIMATCH_SCANLOCATIONS:
                count = db.update(WifiScanContract.ScanLocation.TABLE_NAME, values, where, whereArgs);
                break;
            case URIMATCH_SCANLOCATION_BYID:
                finalWhere = (WifiScanContract.ScanLocation._ID + " = " + uri.getPathSegments().get(WifiScanContract.ScanLocation.PATH_BYID_IDPOSITION));
                if(!TextUtils.isEmpty(where))
                    finalWhere = finalWhere + " AND " + where;
                
                count = db.update(WifiScanContract.ScanLocation.TABLE_NAME, values, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        
        if(count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        
        return count;
    }
}
