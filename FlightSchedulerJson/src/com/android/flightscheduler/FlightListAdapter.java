
package com.android.flightscheduler;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.flightscheduler.json.FlightsJSONData;

/**
 * Adapter for custom data set and uses ViewHolder internally to improve
 * bindView timings, acts as a bridge between adapter view and data set passed
 * 
 * @author Anukalp
 */
public class FlightListAdapter extends BaseAdapter {

    private static final String TIME_TOKENIZER = "-";

    private LayoutInflater mLayoutInflterService;

    private ArrayList<FlightsJSONData> mData;

    public FlightListAdapter(Context context) {
        mLayoutInflterService = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFlightData(ArrayList<FlightsJSONData> data) {
        mData = data;
    }

    public ArrayList<FlightsJSONData> getData() {
        return mData;
    }

    static class ViewHolder {
        TextView time;

        TextView timestamp;

        TextView price;

        TextView flightName;

        TextView flightClass;

        int position;
    }

    @Override
    public int getCount() {
        return null != mData ? mData.size() : 0;
    }

    @Override
    public FlightsJSONData getItem(int position) {
        return null != mData ? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            convertView = mLayoutInflterService.inflate(R.layout.list_item, null, false);
            holder = new ViewHolder();
            holder.time = (TextView)convertView.findViewById(R.id.time);
            holder.price = (TextView)convertView.findViewById(R.id.price);
            holder.timestamp = (TextView)convertView.findViewById(R.id.duration);
            holder.flightClass = (TextView)convertView.findViewById(R.id.flight_class);
            holder.flightName = (TextView)convertView.findViewById(R.id.flight_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final FlightsJSONData data = mData.get(position);
        StringBuilder timeText = new StringBuilder();
        timeText.append(data.getTakeoffTimeString()).append(" ").append(TIME_TOKENIZER).append(" ")
                .append(data.getLandTimeString());
        holder.time.setText(timeText.toString());
        holder.price.setText(Integer.toString(data.getmPrice()));
        holder.flightClass.setText(data.getmClass());
        holder.timestamp.setText(data.getDurationString());
        holder.flightName.setText(data.getmFlightCode());
        return convertView;
    }

}
