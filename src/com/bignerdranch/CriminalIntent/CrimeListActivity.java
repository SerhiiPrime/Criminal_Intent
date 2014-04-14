package com.bignerdranch.CriminalIntent;

import android.support.v4.app.Fragment;

/**
 * Created by badgateway on 14.04.14.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {

        return new CrimeListFragment();
    }
}
