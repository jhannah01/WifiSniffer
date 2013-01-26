package com.blueodin.wifisniffer.fragments;

import java.text.DateFormat;
import java.util.Date;

import com.blueodin.wifisniffer.helpers.DBHelper;
import com.blueodin.wifisniffer.providers.WifiScanContract;
import com.blueodin.wifisniffer.providers.WifiScanResult;
import com.blueodin.wifisniffer.R;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class WifiListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String STATE_ACTIVATED_POSITION = "state_activated_position";
	private static final int LOADER_ID = 0x01;
	private IClickHandler mCallbacks = mDefaultClickHandler;
	private int mActivatedPosition;
	private WifiResultAdapter mListAdapter;

	private final static String[] mProjection = new String[] {
			DBHelper.ResultsColumns._ID, DBHelper.ResultsColumns.COLUMN_BSSID,
			DBHelper.ResultsColumns.COLUMN_SSID,
			DBHelper.ResultsColumns.COLUMN_LEVEL,
			DBHelper.ResultsColumns.COLUMN_TIMESTAMP,
			DBHelper.ResultsColumns._COUNT };

	private static int[] mOutProjection = new int[] { 0, R.id.textBSSID,
			R.id.textSSID, R.id.textLevel, R.id.textTimestamp, R.id.textRecords };

	public interface IClickHandler {
		public void onItemSelected(WifiScanResult result);
	}

	private static IClickHandler mDefaultClickHandler = new IClickHandler() {
		@Override
		public void onItemSelected(WifiScanResult result) {
		}
	};

	public WifiListFragment() {

	}

	public class WifiResultAdapter extends SimpleCursorAdapter {

		private SimpleCursorAdapter.ViewBinder mViewBinder = new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				switch (view.getId()) {
				case 0:
					return true;
				case R.id.textTimestamp:
					((TextView) view).setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(new Date(cursor.getLong(columnIndex))));
					return true;
				case R.id.textLevel:
					((TextView) view).setText(String.format("%d dBm",
							cursor.getInt(columnIndex)));
					return true;
				case R.id.textRecords:
					((TextView) view).setText("#" + cursor.getInt(columnIndex));
					return true;
				}
				return false;
			}
		};

		public WifiResultAdapter(Context context) {
			super(context, R.layout.wifiscanresult_list_row, null, mProjection,
					mOutProjection,
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			setViewBinder(mViewBinder);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(LOADER_ID, null, this);
		mListAdapter = new WifiResultAdapter(getActivity());
		setListAdapter(mListAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof IClickHandler))
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");

		mCallbacks = (IClickHandler) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = mDefaultClickHandler;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		Cursor c = (Cursor) this.mListAdapter.getItem(position);

		Cursor data = getActivity().getContentResolver().query(
				WifiScanContract.Results.uriById(c.getLong(c
						.getColumnIndex(DBHelper.ResultsColumns._ID))), null,
				null, null, null);

		data.moveToFirst();
		WifiScanResult result = WifiScanResult.fromCursor(data);
		data.close();

		mCallbacks.onItemSelected(result);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION)
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return (new CursorLoader(getActivity(),
				WifiScanContract.Results.CONTENT_UNIQUE_URI, mProjection, null, null,
				null));
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.mListAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		this.mListAdapter.swapCursor(null);

	}
}
