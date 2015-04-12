
package com.android.flightscheduler.filter;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * CustomFilter : Singleton class to hold filter value across different
 * application components ContentValues concept is used to later support DB
 * related changes Filter will be derived through content key value pairs Basic
 * Filter will be set by loader with the supplied data for Min and Max fares
 * TODO: SharedPreference to save data for basic interaction or database support
 * and fetch filter details in separate handler thread Never push view listeners
 * or callbacks inside this class otherwise it will not be available for garbage
 * collection
 * @author Anukalp
 **/
public abstract class CustomFilter {

    private static CustomFilter mFilter;

    public static CustomFilter getInstance(Context context) {
        Context appContext = context.getApplicationContext();
        if (null == mFilter) {
            mFilter = createcustomFilter(appContext);
        }
        return mFilter;
    }

    private static synchronized CustomFilter createcustomFilter(Context appContext) {
        return new CustomFilterImpl(appContext);
    }

    public abstract void buildFilter(ContentValues newValues);

    public abstract ContentValues getCurrentFilter();

    public abstract void setDefaultFilter(ContentValues defaultValue);

    public abstract void applyDefaultFilter();
}

class CustomFilterImpl extends CustomFilter {

    private Context mContext;

    private ContentValues mValues;

    private ContentValues mDefaultValues;

    public CustomFilterImpl(Context appContext) {
        mContext = appContext;
        mValues = new ContentValues();
    }

    @Override
    public void buildFilter(ContentValues newValues) {
        Log.d("anu", " new Object::" + newValues.valueSet());
        if (FilterUtils.isFilterChanged(newValues, mValues)) {
            mValues = newValues;
        }
    }

    @Override
    public ContentValues getCurrentFilter() {
        return mValues;
    }

    @Override
    public void setDefaultFilter(ContentValues defaultValue) {
        mDefaultValues = defaultValue;
    }

    @Override
    public void applyDefaultFilter() {
        mValues = mDefaultValues;
    }
}
