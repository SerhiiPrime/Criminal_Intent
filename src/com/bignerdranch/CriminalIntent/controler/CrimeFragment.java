package com.bignerdranch.CriminalIntent.controler;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.bignerdranch.CriminalIntent.R;
import com.bignerdranch.CriminalIntent.date.DatePickerFragment;
import com.bignerdranch.CriminalIntent.model.Crime;
import com.bignerdranch.CriminalIntent.model.CrimeLab;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by badgateway on 13.04.14.
 */
public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final  String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;


    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeID = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        createTitle(v);
        createDateButton(v);
        createSolvedCheckBox(v);
        return v;
    }

    private void createTitle(View v) {

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void createDateButton(View  v) {
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDateButton();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();

                DatePickerFragment dialog = DatePickerFragment.newInstence(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
    }


    @Override
    public void  onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
        mCrime.setDate(date);
        updateDateButton();
    }

    private void createSolvedCheckBox (View v) {

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });
    }


    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(bundle);

        return crimeFragment;
    }

    private SimpleDateFormat setSimpleDateFormat() {
        return new SimpleDateFormat("MM/dd/yyyy");
    }

    private void updateDateButton() {
        mDateButton.setText(setSimpleDateFormat().format(mCrime.getDate()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).saveCrimes();
    }
}
