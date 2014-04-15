package com.bignerdranch.CriminalIntent.controler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.bignerdranch.CriminalIntent.CrimePagerActivity;
import com.bignerdranch.CriminalIntent.R;
import com.bignerdranch.CriminalIntent.model.Crime;
import com.bignerdranch.CriminalIntent.model.CrimeLab;

import java.util.ArrayList;

/**
 * Created by badgateway on 14.04.14.
 */
public class CrimeListFragment extends ListFragment {

    private static final String TAG = "CrimeListFragment";
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();


        CrimeAdapter adapter = new CrimeAdapter(mCrimes);

        setListAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Crime crime = ((CrimeAdapter)getListAdapter()).getItem(position);

        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
        startActivity(intent);
    }




    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_crime, null);
            }

            Crime crime = getItem(position);
            TextView titleText = (TextView)convertView.findViewById(R.id.list_item_textTitle);
            titleText.setText(crime.getTitle());

            TextView dateText = (TextView)convertView.findViewById(R.id.crime_list_item_date);
            dateText.setText(crime.getDate().toString());

            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_checkBox);
            checkBox.setChecked(crime.isSolved());

            return convertView;
        }
    }
}
