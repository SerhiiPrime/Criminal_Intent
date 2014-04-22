package com.bignerdranch.CriminalIntent.controler;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.*;
import com.bignerdranch.CriminalIntent.CrimePagerActivity;
import com.bignerdranch.CriminalIntent.R;
import com.bignerdranch.CriminalIntent.model.Crime;
import com.bignerdranch.CriminalIntent.model.CrimeLab;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by badgateway on 14.04.14.
 */
public class CrimeListFragment extends ListFragment {

    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        setHasOptionsMenu(true);


        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
    }


    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);

        ListView listView = (ListView)v.findViewById(android.R.id.list);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {}

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    MenuInflater inflater = actionMode.getMenuInflater();
                    inflater.inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_item_delete_crime:
                            CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
                            CrimeLab lab = CrimeLab.get(getActivity());
                            for(int i = adapter.getCount() - 1; i >= 0; i-- ) {
                                if (getListView().isItemChecked(i)) {
                                    lab.deleteCrime(adapter.getItem(i));
                                }
                            }
                            actionMode.finish();
                            adapter.notifyDataSetChanged();
                            return true;

                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {}
            });
        }

        return v;
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
            dateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(crime.getDate()));

            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_checkBox);
            checkBox.setChecked(crime.isSolved());

            return convertView;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);

                Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
                intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
        Crime crime = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }


    @Override
    public void onStop() {
        super.onStop();
        CrimeLab.get(getActivity()).saveCrimes();
    }
}
