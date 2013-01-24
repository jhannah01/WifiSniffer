
package com.blueodin.wifisniffer.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blueodin.wifisniffer.R;
import com.blueodin.wifisniffer.providers.WifiScanResult;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.List;

public class NetworkExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private final WifiScanResult mPrimaryResult;
    private final List<WifiScanResult> mResults;
    private List<ListGroup> mGroups = new ArrayList<ListGroup>();

    public class ListGroup {
        private WifiScanResult mResult;
        private final ListChild mChild;
        
        public ListGroup(WifiScanResult result) {
            mResult = result;
            mChild = new ListChild(this);
        }
        
        public ListChild getChild() {
            return mChild;
        }
        
        public WifiScanResult getResult() {
            return mResult;
        }
        
        public void drawView(Context context, View view, ViewGroup parent) {
            ImageView imageGroupIcon = (ImageView) view.findViewById(R.id.imageview_group_icon);
            TextView textBSSID = (TextView) view.findViewById(R.id.textview_group_bssid);
            TextView textSSID = (TextView) view.findViewById(R.id.textview_group_ssid);
            TextView textTimestamp = (TextView) view.findViewById(R.id.textview_group_lastseen);
            
            imageGroupIcon.setImageResource(mResult.getSecurityIcon());
            textBSSID.setText(mResult.bssid);
            textSSID.setText(mResult.ssid);
            textTimestamp.setText(mResult.getFormattedTimestamp());
        }
    }
    
    public class ListChild {
        private final ListGroup mParent;
        private final GraphViewSeries mGraphSeries;
        private final LineGraphView mGraph;
        
        public ListChild(ListGroup parent) {
            mParent = parent;
            mGraphSeries = new GraphViewSeries(getSignalData(mResults));
            mGraph = buildGraph();
        }
        
        public ListGroup getParent() {
            return mParent;
        }
        
        private LineGraphView buildGraph() {
            LineGraphView graphView = new LineGraphView(mContext, "Historical Graph for " + mPrimaryResult.ssid) {
                protected String formatLabel(double value, boolean isValueX) {
                    if (!isValueX) {
                        return String.format("%.02f dBm", value);
                    } else {
                        return (String)DateUtils.formatElapsedTime((long)value);
                    }
                }
            };

            Resources res = mContext.getResources();

            GraphViewStyle graphViewStyle = graphView.getGraphViewStyle();
            graphViewStyle.setGridColor(res.getColor(R.color.bar_background_color));
            graphView.setBackgroundColor(res.getColor(R.color.primary_bg_color));
            graphView.getGraphViewStyle().setHorizontalLabelsColor(res.getColor(R.color.secondary_text_color));
            graphView.getGraphViewStyle().setVerticalLabelsColor(res.getColor(R.color.alt_text_color));
            graphView.setDrawBackground(true);
            
            double size = (5*60)*1000;
            double start = System.currentTimeMillis()-((15*60)*1000);
            
            graphView.addSeries(mGraphSeries);
            
            graphView.setScalable(true);
            graphView.setViewPort(start, size);
            
            return graphView;
        }

        public void drawView(Context context, View view, ViewGroup parent) {
            TextView textViewLevel = (TextView) view.findViewById(R.id.textview_child_level);
            TextView textViewFrequency = (TextView) view.findViewById(R.id.textview_child_frequency);
            TextView textViewCapabilities = (TextView) view.findViewById(R.id.textview_child_capabilities);
            
            textViewLevel.setText(String.format("%d dBm", mPrimaryResult.level));
            textViewFrequency.setText(String.format("%d MHz", mPrimaryResult.frequency));
            textViewCapabilities.setText(mPrimaryResult.getFormattedCapabilities());
            
            ((LinearLayout)view.findViewById(R.id.layout_child_graph)).addView(this.mGraph);
        }

        
        private GraphViewData[] getSignalData(List<WifiScanResult> results) {
            int idx;

            if(results.size() < 1)
                idx = 1;
            else 
                idx = results.size();
            
            GraphViewData[] signalData = new GraphViewData[idx];
            
            int i=0;
            for(WifiScanResult result : results)
                signalData[i++] = new GraphViewData(result.timestamp, result.level);
            
            return signalData;
        }
    }
    
    public NetworkExpandableListAdapter(Context context, WifiScanResult primaryResult, List<WifiScanResult> results) {
        mContext = context;
        mPrimaryResult = primaryResult;
        mResults = results;
    }

    @Override
    public int getGroupCount() {
        return mResults.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition < mResults.size())
            return 1;

        return 0;
    }

    @Override
    public ListGroup getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public ListChild getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getChild();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        if (groupPosition < mResults.size())
            return childPosition;

        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ListGroup group = (ListGroup) getGroup(groupPosition);
        
        if (convertView == null)
            convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.expandlist_group_item, null, false);

        group.drawView(this.mContext, convertView, parent);
        
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        ListChild child = (ListChild) getChild(groupPosition, childPosition);
        
        if (convertView == null)
            convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.expandlist_child_item, null, false);
    
        child.drawView(this.mContext, convertView, parent);
        
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
