
package com.android.flightscheduler.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.JsonReader;
import android.util.Log;

/**
 * Wrapper class for loader data {@link FlightListLoader}
 * @author Anukalp
 **/
public class CustomJSONWrapper implements FligtsJsonDeSerializer {

    private AirlineJSONMap mAirLineMap;

    private AirportJsonMap mAirportJson;

    private ArrayList<FlightsJSONData> mFlightsJSONData;

    public AirportJsonMap getAirportJSONMap() {
        return mAirportJson;
    }

    public AirlineJSONMap getAirLineMap() {
        return mAirLineMap;
    }

    public ArrayList<FlightsJSONData> getFlightsJSONData() {
        return mFlightsJSONData;
    }

    /**
     * Inner classes to provide encapsulation Manage Map for Flight Name
     * abbreviations fetched from FlightData
     **/
    private class AirlineJSONMap implements FligtsJsonDeSerializer {
        public static final String TAG = "airlineMap";

        private HashMap<String, String> mAirlineMap = new HashMap<String, String>();

        @Override
        public void populateJsonData(InputStream is) throws IOException {
        }

        @Override
        public void populateJsonData(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                String value = reader.nextString();
                mAirlineMap.put(name, value);
            }
            reader.endObject();
        }

        @Override
        public String toString() {
            return "mAirlineMap [mAirlineMap=" + mAirlineMap + "]";
        }
    }

    /**
     * Inner classes to provide encapsulation Manage Map for Airport Name
     * abbreviations fetched from FlightData
     **/
    private class AirportJsonMap implements FligtsJsonDeSerializer {
        public static final String TAG = "airportMap";

        private HashMap<String, String> mAirportMap = new HashMap<String, String>();

        @Override
        public void populateJsonData(InputStream is) throws IOException {
        }

        @Override
        public void populateJsonData(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                String value = reader.nextString();
                mAirportMap.put(name, value);
            }
            reader.endObject();
        }

        @Override
        public String toString() {
            return "AirportJsonMap [mAirportMap=" + mAirportMap + "]";
        }

        public String getTitleString() {
            StringBuilder sb = new StringBuilder();
            for (String airport : mAirportMap.keySet()) {
                sb.append(airport).append(" - ");
            }
            sb.delete(sb.length() - 3, sb.length());
            return sb.toString();
        }

    }

    @Override
    public void populateJsonData(InputStream is) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        reader.setLenient(true);
        mAirLineMap = new AirlineJSONMap();
        mAirportJson = new AirportJsonMap();
        mFlightsJSONData = new ArrayList<FlightsJSONData>();
        try {
            parseJsonMap(reader);
        } finally {
            reader.close();
        }
    }

    private void parseJsonMap(JsonReader reader) throws IOException {
        reader.beginObject();
        if (AirlineJSONMap.TAG.equals(reader.nextName())) {
            mAirLineMap.populateJsonData(reader);
        }
        if (AirportJsonMap.TAG.equals(reader.nextName())) {
            mAirportJson.populateJsonData(reader);
        }
        if (FlightsJSONData.TAG.equals(reader.nextName())) {
            reader.beginArray();
            while (reader.hasNext()) {
                FlightsJSONData flightData = new FlightsJSONData();
                flightData.populateJsonData(reader);
                mFlightsJSONData.add(flightData);
            }
            reader.endArray();
        }
        reader.endObject();
    }

    @Override
    public void populateJsonData(JsonReader reader) throws IOException {
        // TODO : Do Nothing
    }

    @Override
    public String toString() {
        return "CustomJSONWrapper [mAirLineMap=" + mAirLineMap + ", mAirportJson=" + mAirportJson
                + ", mFlightsJSONData=" + mFlightsJSONData + "]";
    }

    public String getTitleString() {
        return mAirportJson.getTitleString();
    }

}
