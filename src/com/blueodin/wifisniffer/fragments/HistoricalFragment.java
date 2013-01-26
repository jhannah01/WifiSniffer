package com.blueodin.wifisniffer.fragments;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract.Colors;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.helpers.DBHelper;
import com.blueodin.wifisniffer.helpers.TabContentFragment;
import com.blueodin.wifisniffer.providers.WifiScanContract;
import com.blueodin.wifisniffer.providers.WifiScanResult;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;

public class HistoricalFragment extends TabContentFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	public static final String ARG_ITEM_BSSID = "arg_item";
	private BarGraphView mGraphView;
	private GraphViewSeries mGraphSeries = null;
	private TextView mTextRecords;
	private TextView mTextSSID;
	private static final int LOADER_ID = 0x02;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;

		View v = inflater.inflate(R.layout.fragment_historical, container,
				false);

		mTextRecords = (TextView) v.findViewById(R.id.text_historical_records);
		mTextSSID = (TextView) v.findViewById(R.id.text_historical_SSID);

		mTextRecords.setText("Records: #0");
		mTextSSID.setText("SSID: N/A");

		mGraphView = new BarGraphView(getActivity(), "") {
			protected String formatLabel(double value, boolean isValueX) {
				if (!isValueX) {
					return String.format("%.02f dBm", value);
				} else {
					return (String) DateUtils
							.getRelativeTimeSpanString((long) value);
				}
			}
		};

		Resources res = getResources();
		GraphViewStyle graphViewStyle = mGraphView.getGraphViewStyle();

		mGraphView.setBackgroundColor(res
				.getColor(android.R.color.background_dark));
		graphViewStyle.setGridColor(res.getColor(android.R.color.darker_gray));
		graphViewStyle.setHorizontalLabelsColor(res
				.getColor(android.R.color.holo_orange_light));
		graphViewStyle.setVerticalLabelsColor(res
				.getColor(android.R.color.holo_blue_dark));
		mGraphView.setScalable(true);
		mGraphView.setScrollable(true);
		
		mGraphView.setViewPort(System.currentTimeMillis() - (60 * 60 * 1000),
				(60 * 60 * 1000));

		((FrameLayout) v.findViewById(R.id.layout_historical_graph))
				.addView(mGraphView);

		return v;
	}

	@Override
	public void updateTabContent(WifiScanResult result) {
		if (result == null)
			return;

		Bundle args = new Bundle();
		args.putString(ARG_ITEM_BSSID, result.bssid);

		getLoaderManager().restartLoader(LOADER_ID, args, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if ((args == null) || (!args.containsKey(ARG_ITEM_BSSID))) {
			return null;
		}

		return (new CursorLoader(getActivity(),
				WifiScanContract.Results.CONTENT_URI, null,
				DBHelper.ResultsColumns.COLUMN_BSSID + " = ?",
				new String[] { args.getString(ARG_ITEM_BSSID) },
				DBHelper.ResultsColumns.COLUMN_TIMESTAMP + " ASC"));
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		updateGraphData(data);
	}

	private void updateGraphData(Cursor data) {
		data.moveToFirst();

		mTextSSID.setText("SSID: " + data.getString(data
				.getColumnIndex(DBHelper.ResultsColumns.COLUMN_BSSID)));
		mTextRecords.setText("Results: #" + data.getCount());

		GraphViewData[] graphData = new GraphViewData[data.getCount()];

		while (!data.isAfterLast()) {
			graphData[data.getPosition()] = new GraphViewData(
					data.getDouble(data
							.getColumnIndex(DBHelper.ResultsColumns.COLUMN_TIMESTAMP)),
					data.getDouble(data
							.getColumnIndex(DBHelper.ResultsColumns.COLUMN_LEVEL)));
			data.moveToNext();
		}

		if (mGraphSeries == null) {
			mGraphSeries = new GraphViewSeries(graphData);
			mGraphView.addSeries(mGraphSeries);
		} else
			mGraphSeries.resetData(graphData);
		
		mGraphView.scrollToEnd();
		mGraphView.setViewPort(System.currentTimeMillis() - (60 * 60 * 1000),
				(60 * 60 * 1000));

		mGraphView.invalidate();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
