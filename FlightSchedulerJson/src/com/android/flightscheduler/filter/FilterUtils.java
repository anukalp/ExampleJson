
package com.android.flightscheduler.filter;

import java.sql.Time;
import com.android.flightscheduler.json.FlightsJSONData;

import android.content.ContentValues;
import android.util.Log;

/**
 * Utility class for filter related changes validate list data based on filter
 * requirements
 * 
 * @author Anukalp
 */
public class FilterUtils {

    public static final String TAG_FARE_START = "start";

    public static final String TAG_FARE_END = "end";

    public static final String TAG_DEPART_START = "depart_start";

    public static final String TAG_DEPART_END = "depart_end";

    public static final String TAG_ARRIVE_START = "arrive_start";

    public static final String TAG_ARRIVE_END = "arrive_end";

    public static final String TAG_BUSINESS = "business";

    public static final String TAG_ECONOMY = "economy";

    public static boolean isFilterChanged(ContentValues newValues, ContentValues oldValues) {
        boolean filterChanged = false;
        Log.d("anu", "isFilterChanged :: " + newValues.valueSet() + " :: " + oldValues.valueSet());
        if (null == oldValues || newValues == null)
            return true;
        for (final String key : newValues.keySet()) {
            if (!oldValues.containsKey(key)) {
                filterChanged = true;
                break;
            } else if (!newValues.get(key).equals(oldValues.get(key))) {
                filterChanged = true;
                break;
            }
        }
        return filterChanged;
    }

    public static boolean shouldRemoveEntry(ContentValues value, FlightsJSONData data) {
        boolean remove = false;
        int price = data.getmPrice();
        String mClass = data.getmClass();
        String startTime = data.getTakeoffTimeString();
        String endTime = data.getLandTimeString();
        String departStartTime = value.getAsString(TAG_DEPART_START);
        String departEndTime = value.getAsString(TAG_DEPART_END);
        String arriveStartTime = value.getAsString(TAG_ARRIVE_START);
        String arriveEndTime = value.getAsString(TAG_ARRIVE_END);
        if (price < value.getAsInteger(FilterUtils.TAG_FARE_START)
                || price > value.getAsInteger(FilterUtils.TAG_FARE_END)
                || (!value.getAsBoolean(TAG_ECONOMY) && mClass.equalsIgnoreCase(TAG_ECONOMY))
                || (!value.getAsBoolean(TAG_BUSINESS) && mClass.equalsIgnoreCase(TAG_BUSINESS))
                || compareTime(startTime, departStartTime, departEndTime)
                || compareTime(endTime, arriveStartTime, arriveEndTime)) {
            remove = true;
        }
        return remove;
    }

    //TODO : Time needs to be calculated using calendar/Date instance
    public static boolean compareTime(String time, String startTime, String endTime) {
        Time original = Time.valueOf(time + ":0");
        Time t = Time.valueOf(startTime + ":0");
        Time t1 = Time.valueOf(endTime + ":0");
        return original.getTime() < t.getTime() || original.getTime() >  t1.getTime();

    }
}
