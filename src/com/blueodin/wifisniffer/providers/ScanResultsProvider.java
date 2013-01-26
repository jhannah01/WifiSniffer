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
	public static final HashMap<String,String> RESULTS_PROJECTION = new HashMap<String,String>();
	public static final HashMap<String,String> NETWORKS_PROJECTION;
	
	private static final UriMatcher sUriMatcher;

	private DBHelper mDbHelper;

	private static final int URIMATCH_SCANRESULTS = 0x01;
	private static final int URIMATCH_UNIQUE_NETWORKS = 0x02;
	private static final int URIMATCH_SCANRESULT_BYID = 0x03;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		sUriMatcher.addURI(WifiScanContract.AUTHORITY, WifiScanContract.Results.PATH, URIMATCH_SCANRESULTS);
		sUriMatcher.addURI(WifiScanContract.AUTHORITY, WifiScanContract.Results.PATH_UNIQUE, URIMATCH_UNIQUE_NETWORKS); 
		sUriMatcher.addURI(WifiScanContract.AUTHORITY, WifiScanContract.Results.PATH_BYID + "#", URIMATCH_SCANRESULT_BYID);
		
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns._ID, DBHelper.ResultsColumns._ID);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_BSSID, DBHelper.ResultsColumns.COLUMN_BSSID);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_SSID, DBHelper.ResultsColumns.COLUMN_SSID);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_LEVEL, DBHelper.ResultsColumns.COLUMN_LEVEL);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_FREQUENCY, DBHelper.ResultsColumns.COLUMN_FREQUENCY);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_CAPABILITIES, DBHelper.ResultsColumns.COLUMN_CAPABILITIES);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_LONGITUDE, DBHelper.ResultsColumns.COLUMN_LONGITUDE);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_LATITUDE, DBHelper.ResultsColumns.COLUMN_LATITUDE);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_ALTITUDE, DBHelper.ResultsColumns.COLUMN_ALTITUDE);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.COLUMN_TIMESTAMP, DBHelper.ResultsColumns.COLUMN_TIMESTAMP);
		
		NETWORKS_PROJECTION = new HashMap<String, String>(RESULTS_PROJECTION);
		NETWORKS_PROJECTION.put(DBHelper.ResultsColumns._COUNT, DBHelper.ResultsColumns._COUNT);
		
	}

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
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case URIMATCH_SCANRESULTS:
		case URIMATCH_UNIQUE_NETWORKS:
			return WifiScanContract.Results.CONTENT_TYPE;
		case URIMATCH_SCANRESULT_BYID:
			return WifiScanContract.Results.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String orderBy = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case URIMATCH_SCANRESULT_BYID:
			queryBuilder.appendWhere(DBHelper.ResultsColumns._ID + "=" + uri.getPathSegments().get(WifiScanContract.Results.PATH_BYID_IDPOSITION));
		case URIMATCH_SCANRESULTS:
			queryBuilder.setTables(DBHelper.TABLE_RESULTS);
			queryBuilder.setProjectionMap(RESULTS_PROJECTION);
			orderBy = WifiScanContract.Results.DEFAULT_ORDER_BY;
			break;
		case URIMATCH_UNIQUE_NETWORKS:
			queryBuilder.setTables(DBHelper.VIEW_NETWORKS);
			queryBuilder.setProjectionMap(NETWORKS_PROJECTION);
			orderBy = WifiScanContract.Results.DEFAULT_ORDER_BY;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		if (!TextUtils.isEmpty(sortOrder))
			orderBy = sortOrder;

		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		Cursor c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriMatch = sUriMatcher.match(uri);
		Uri baseUri;
		String tableName;

		switch (uriMatch) {
		case URIMATCH_SCANRESULTS:
			baseUri = WifiScanContract.Results.CONTENT_ID_URI_BASE;
			tableName = DBHelper.TABLE_RESULTS;
			break;
		default:
			throw new IllegalArgumentException("Invalid URI: " + uri);
		}

		if (values == null)
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

		switch (sUriMatcher.match(uri)) {
		case URIMATCH_SCANRESULTS:
			count = db.delete(DBHelper.TABLE_RESULTS, where, whereArgs);
			break;
		case URIMATCH_SCANRESULT_BYID:
			finalWhere = (DBHelper.ResultsColumns._ID + " = " + uri.getPathSegments().get(WifiScanContract.Results.PATH_BYID_IDPOSITION));
			if (!TextUtils.isEmpty(where))
				finalWhere = finalWhere + " AND " + where;

			count = db.delete(DBHelper.TABLE_RESULTS, finalWhere, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		if (count > 0)
			getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count;
		String finalWhere;

		switch (sUriMatcher.match(uri)) {
		case URIMATCH_SCANRESULTS:
			count = db.update(DBHelper.TABLE_RESULTS, values, where, whereArgs);
			break;
		case URIMATCH_SCANRESULT_BYID:
			finalWhere = (DBHelper.ResultsColumns._ID + " = " + uri.getPathSegments().get(WifiScanContract.Results.PATH_BYID_IDPOSITION));
			if (!TextUtils.isEmpty(where))
				finalWhere = finalWhere + " AND " + where;

			count = db.update(DBHelper.TABLE_RESULTS, values, finalWhere, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		if (count > 0)
			getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}
}
