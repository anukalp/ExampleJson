
package com.android.flightscheduler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.flightscheduler.filter.CustomFilter;
import com.android.flightscheduler.filter.FilterManager;
import com.android.flightscheduler.filter.FilterUtils;
import com.android.flightscheduler.json.CustomJSONWrapper;
import com.android.flightscheduler.json.FlightsJSONData;

/**
 * Activity for basic initialization and setup of UI, manages loaders for JSON
 * parsing and filter related changes Drawer Layout is used to display the
 * filter content
 * 
 * @author Anukalp
 */
public class FlightListActivity extends Activity implements LoaderCallbacks<CustomJSONWrapper>,
        DrawerListener {

    private ListView mListView;

    private DrawerLayout mDrawerLayout;

    private FlightListAdapter mAdapter;

    public Collection<? extends FlightsJSONData> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mListView = (ListView)findViewById(R.id.listView);
        mAdapter = new FlightListAdapter(getApplicationContext());
        mListView.setAdapter(mAdapter);
        mDrawerLayout.setDrawerShadow(android.R.drawable.alert_light_frame, GravityCompat.START);
        mDrawerLayout.setDrawerListener(this);
        ImageView image = (ImageView)findViewById(R.id.filter_button);
        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });
    }

    @Override
    public void onStart() {
        getLoaderManager().initLoader(0, null, this);
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.flight_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_by) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.END)) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<CustomJSONWrapper> onCreateLoader(int id, Bundle args) {
        // TODO Auto-generated method stub
        return new FlightListLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<CustomJSONWrapper> loader, CustomJSONWrapper data) {
        if (null == data)
            return;
        TextView textview = (TextView)findViewById(R.id.flightDetails);
        textview.setText(data.getTitleString());
        mAdapter.setFlightData(data.getFlightsJSONData());
        mAdapter.notifyDataSetChanged();
        mData = data.getFlightsJSONData();
    }

    @Override
    public void onLoaderReset(Loader<CustomJSONWrapper> loader) {
        // TODO Auto-generated method stub

    }

    public void restFilters() {
        new FilterAsyncTask().execute();
    }

    /**
     * AyncTask : to load latest filter details Original data is copied into
     * other map and then passed to adapter. Since, we don't have a local
     * database we need to keep two copies to avoid parsing JSON
     **/
    public class FilterAsyncTask extends AsyncTask<Void, Void, ArrayList<FlightsJSONData>> {

        /**
         * WeakReference to avoid context/window leak from progress dialog
         */
        private WeakReference<ProgressDialog> mProgress;

        private ContentValues mFilter;

        private ArrayList<FlightsJSONData> mLocalDataSet;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("anu", "OnPreExecute");
            mProgress = new WeakReference<ProgressDialog>(new ProgressDialog(
                    FlightListActivity.this));
            mFilter = CustomFilter.getInstance(getApplicationContext()).getCurrentFilter();
            mProgress.get().show();
            mLocalDataSet = new ArrayList<FlightsJSONData>(mData);
        }

        @Override
        protected ArrayList<FlightsJSONData> doInBackground(Void... params) {
            boolean isChanged = false;
            Log.d("anu", "isChanged ");
            ArrayList<FlightsJSONData> removedData = new ArrayList<FlightsJSONData>();
            if (null != mLocalDataSet) {
                for (FlightsJSONData flightsJSONData : mLocalDataSet) {
                    if (FilterUtils.shouldRemoveEntry(mFilter, flightsJSONData)) {
                        removedData.add(flightsJSONData);
                        isChanged = true;
                    }
                }
            }
            if (isChanged) {
                mLocalDataSet.removeAll(removedData);
            }
            return mLocalDataSet;
        }

        @Override
        protected void onPostExecute(ArrayList<FlightsJSONData> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (null != mProgress.get()) {
                mProgress.get().dismiss();
                mProgress.clear();
                mProgress = null;
            }
            if (mDrawerLayout.isDrawerVisible(GravityCompat.END)) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
            if (null != result) {
                mAdapter.setFlightData(result);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDrawerClosed(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(
                    ((FilterManager)findViewById(R.id.filter_manager)).getWindowToken(), 0);
        }
        ((FilterManager)findViewById(R.id.filter_manager)).initValues();
    }

    @Override
    public void onDrawerOpened(View view) {
        ((FilterManager)findViewById(R.id.filter_manager)).initValues();
    }

    @Override
    public void onDrawerSlide(View view, float value) {
    }

    @Override
    public void onDrawerStateChanged(int value) {
    }

}
