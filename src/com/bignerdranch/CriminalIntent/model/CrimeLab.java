package com.bignerdranch.CriminalIntent.model;

import android.content.Context;
import android.util.Log;
import com.bignerdranch.CriminalIntent.json.CriminalIntentJSONSerializer;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by badgateway on 14.04.14.
 */
public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static final String FILE_NAME = "crimes.json";

    private CriminalIntentJSONSerializer mSerializer;

    private static CrimeLab sCrimeLab;
    private Context mContext;

    private ArrayList<Crime> mCrimes;

    private CrimeLab(Context context) {
        mContext = context;
        mSerializer = new CriminalIntentJSONSerializer(mContext, FILE_NAME);

        try {
            mCrimes = mSerializer.loadCrims();
            Log.d(TAG, "loaded");
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.d(TAG, "Error loadeng files" + e);
        }
    }


    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }


    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime c: mCrimes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }


    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "saved");
            return true;
        } catch (Exception e) {
            Log.d(TAG, "error saving" + e);
            return false;
        }
    }
}
