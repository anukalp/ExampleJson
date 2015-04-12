
package com.android.flightscheduler.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.JsonReader;

/**
 * Must populate this class from JSON stream Non-nested so can be filled with
 * separate JSON file and for code readability supported sorting based on Fare charges
 * @author Anukalp
 **/
public class FlightsJSONData implements FligtsJsonDeSerializer, Comparable<FlightsJSONData> {

    public static final String TAG = "flightsData";

    private static final String TAG_ORIGIN_CODE = "originCode";

    private static final String TAG_DESTINATION_CODE = "destinationCode";

    private static final String TAG_FLIGHT_CODE = "airlineCode";

    private static final String TAG_CLASS_CODE = "class";

    private static final String TAG_TAKE_OFF_TIME = "takeoffTime";

    private static final String TAG_LAND_TIME = "landingTime";

    private static final String TAG_PRICE = "price";

    private String mOriginCode;

    private String mDestCode;

    private String mFlightCode;

    private String mClass;

    private long mTakeoffTime;

    private String mTakeoffTimeString;

    private long mLandTime;

    private String mLandTimeString;

    private String mDurationString;

    private int mPrice;

    public FlightsJSONData() {
    }

    public String getmOriginCode() {
        return mOriginCode;
    }

    public String getmDestCode() {
        return mDestCode;
    }

    public String getmFlightCode() {
        return mFlightCode;
    }

    public String getmClass() {
        return mClass;
    }

    public long getmTakeoffTime() {
        return mTakeoffTime;
    }

    public long getmLandTime() {
        return mLandTime;
    }

    public int getmPrice() {
        return mPrice;
    }

    public String getTimeString(long time) {
        Date startDate = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(startDate);
    }

    private String getDifference(long diff) {
        StringBuilder sb = new StringBuilder();
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        if (diffDays != 0) {
            sb.append(diffDays).append("d ");
        }
        sb.append(diffHours).append("h ");
        sb.append(diffMinutes).append("m");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "FlightsJSONData [mOriginCode=" + mOriginCode + ", mDestCode=" + mDestCode
                + ", mFlightCode=" + mFlightCode + ", mClass=" + mClass + ", mTakeoffTime="
                + mTakeoffTime + ", mLandTime=" + mLandTime + ", mPrice=" + mPrice + "]";
    }

    @Override
    public void populateJsonData(InputStream is) throws UnsupportedEncodingException, IOException {
    }

    @Override
    public void populateJsonData(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (TAG_CLASS_CODE.equals(name)) {
                mClass = reader.nextString();
            } else if (TAG_TAKE_OFF_TIME.equals(name)) {
                mTakeoffTime = reader.nextLong();
                setTakeoffTimeString(getTimeString(mTakeoffTime));
            } else if (TAG_LAND_TIME.equals(name)) {
                mLandTime = reader.nextLong();
                setLandTimeString(getTimeString(mLandTime));
            } else if (TAG_PRICE.equals(name)) {
                mPrice = reader.nextInt();
            } else if (TAG_ORIGIN_CODE.equals(name)) {
                mOriginCode = reader.nextString();
            } else if (TAG_DESTINATION_CODE.equals(name)) {
                mDestCode = reader.nextString();
            } else if (TAG_FLIGHT_CODE.equals(name)) {
                mFlightCode = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        setDurationString(getDifference((mLandTime - mTakeoffTime)));
        reader.endObject();
    }

    public String getLandTimeString() {
        return mLandTimeString;
    }

    private void setLandTimeString(String mLandTimeString) {
        this.mLandTimeString = mLandTimeString;
    }

    public String getTakeoffTimeString() {
        return mTakeoffTimeString;
    }

    private void setTakeoffTimeString(String mTakeoffTimeString) {
        this.mTakeoffTimeString = mTakeoffTimeString;
    }

    public String getDurationString() {
        return mDurationString;
    }

    private void setDurationString(String mDurationString) {
        this.mDurationString = mDurationString;
    }

    @Override
    public int compareTo(FlightsJSONData another) {
        return another.getmPrice() > this.getmPrice() ? -1 : 0;
    }
}
