
package com.android.flightscheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;

import com.android.flightscheduler.filter.CustomFilter;
import com.android.flightscheduler.filter.FilterUtils;
import com.android.flightscheduler.json.CustomJSONWrapper;

/**
 * FlightListLoader : loads asynchronously on thread pool executor
 * 1. Scalable in terms of local Database Support (Fetching cursor and query database)
 * 2. LoaderManager takes care of activity life cycle callback's and internal caching is supported
 * 3. ForceLoadContentObserver : Later versions with local database support you can keep and observer over the db URI
 * 4. JSON object will update the database and app will listen to changes without haste.
 * @author Anukalp
 **/
public class FlightListLoader extends AsyncTaskLoader<CustomJSONWrapper> {

    private CustomJSONWrapper mJsonWrapper;

    public FlightListLoader(Context context) {
        super(context);
    }

    @Override
    public CustomJSONWrapper loadInBackground() {
        CustomJSONWrapper lData = null;
        try {
            InputStream is = getContext().getAssets().open("data.js");
            lData = new CustomJSONWrapper();
            lData.populateJsonData(is);
            Collections.sort(lData.getFlightsJSONData());
            int length = lData.getFlightsJSONData().size();
            if (length > 0) {
                ContentValues cv = CustomFilter.getInstance(getContext()).getCurrentFilter();
                cv.put(FilterUtils.TAG_FARE_START, lData.getFlightsJSONData().get(0).getmPrice());
                cv.put(FilterUtils.TAG_FARE_END, lData.getFlightsJSONData().get(length - 1)
                        .getmPrice());
                cv.put(FilterUtils.TAG_DEPART_START, "00:00");
                cv.put(FilterUtils.TAG_DEPART_END, "23:59");
                cv.put(FilterUtils.TAG_ARRIVE_START, "00:00");
                cv.put(FilterUtils.TAG_ARRIVE_END, "23:59");
                cv.put(FilterUtils.TAG_BUSINESS, true);
                cv.put(FilterUtils.TAG_ECONOMY, true);
                CustomFilter.getInstance(getContext()).setDefaultFilter(cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lData;
    }

    @Override
    protected void onStartLoading() {
        if (mJsonWrapper != null) {
            deliverResult(mJsonWrapper);
        }
        if (takeContentChanged() || mJsonWrapper == null) {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(CustomJSONWrapper data) {
        if (isReset()) {
            mJsonWrapper = null;
            return;
        }
        CustomJSONWrapper oldValue = mJsonWrapper;
        mJsonWrapper = data;

        if (isStarted()) {
            super.deliverResult(mJsonWrapper);
        }

        if (null != oldValue && oldValue != mJsonWrapper) {
            oldValue = null;
        }
        super.deliverResult(data);
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(CustomJSONWrapper data) {
        data = null;
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();
        mJsonWrapper = null;
    }

}
