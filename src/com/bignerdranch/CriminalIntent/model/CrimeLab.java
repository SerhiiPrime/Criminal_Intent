package com.bignerdranch.CriminalIntent.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by badgateway on 14.04.14.
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;

    private ArrayList<Crime> mCrimes;

    private CrimeLab(Context context) {
        mContext = context;

        mCrimes = new ArrayList<Crime>();
        for (int i = 0; i < 20; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime # " + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
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
}
