
package com.android.flightscheduler.json;

import java.io.IOException;
import java.io.InputStream;
import android.util.JsonReader;

/**
 * FligtsJsonDeSerializer : DeSerializer interface
 * 
 * @author Anukalp
 */
public interface FligtsJsonDeSerializer {
    /**
     * Must populate this object from the given JSON inputStream
     *
     * @param is InputStream to be parsed by JSONReader
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    void populateJsonData(final InputStream is) throws IOException;

    /**
     * Must populate this object from the given JsonReader
     *
     * @param reader : existing JsonReader prepared from inputStream
     * @throws UnsupportedEncodingException
     * @throws IOException
     */

    void populateJsonData(final JsonReader reader) throws IOException;
}
