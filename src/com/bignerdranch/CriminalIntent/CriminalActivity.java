package com.bignerdranch.CriminalIntent;

import android.support.v4.app.Fragment;
import com.bignerdranch.CriminalIntent.controler.CrimeFragment;

public class CriminalActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
