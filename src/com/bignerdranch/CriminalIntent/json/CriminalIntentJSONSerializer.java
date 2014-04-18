package com.bignerdranch.CriminalIntent.json;

import android.content.Context;
import android.util.Log;
import com.bignerdranch.CriminalIntent.model.Crime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by badgateway on 18.04.14.
 */
public class CriminalIntentJSONSerializer {

    private Context mContext;
    private String mFileName;

    public CriminalIntentJSONSerializer(Context context, String fileName) {
        mContext = context;
        mFileName = fileName;
    }


    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {

        Log.d("CrimeLab", "In CriminalIntentJSONSerializer");
        JSONArray array = new JSONArray();

        for(Crime c: crimes) {
            array.put(c.toJSON());
        }


        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);

            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }



    public ArrayList<Crime> loadCrims() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;

        try {
            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuffer jsonString = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return crimes;
    }

}
