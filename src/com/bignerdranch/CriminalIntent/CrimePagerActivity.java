package com.bignerdranch.CriminalIntent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.bignerdranch.CriminalIntent.controler.CrimeFragment;
import com.bignerdranch.CriminalIntent.model.Crime;
import com.bignerdranch.CriminalIntent.model.CrimeLab;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by badgateway on 15.04.14.
 */
public class CrimePagerActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCrimes = CrimeLab.get(this).getCrimes();

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.ViewPager);
        setContentView(mViewPager);


        final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });


        UUID crimeId = (UUID)getIntent()
                .getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);

        setCurrentTitle();

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }


    }


    private void setCurrentTitle() {

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Crime crime = mCrimes.get(position);
                if (crime.getTitle() != null) {
                    setTitle(crime.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
