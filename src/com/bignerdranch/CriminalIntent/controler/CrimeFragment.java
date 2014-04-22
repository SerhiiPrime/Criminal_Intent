package com.bignerdranch.CriminalIntent.controler;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bignerdranch.CriminalIntent.CrimeCameraActivity;
import com.bignerdranch.CriminalIntent.R;
import com.bignerdranch.CriminalIntent.camera.CrimeCameraFragment;
import com.bignerdranch.CriminalIntent.camera.ImageFragment;
import com.bignerdranch.CriminalIntent.date.DatePickerFragment;
import com.bignerdranch.CriminalIntent.model.Crime;
import com.bignerdranch.CriminalIntent.model.CrimeLab;
import com.bignerdranch.CriminalIntent.model.photo.Photo;
import com.bignerdranch.CriminalIntent.utils.PictureUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by badgateway on 13.04.14.
 */
public class CrimeFragment extends Fragment implements ActionMode.Callback{

    private static final String DIALOG_IMAGE = "image";
    private static final String TAG = "CrimeFragment";
    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final  String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;


    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhoteButton;
    private ImageView mPhotoView;
    private Button mSuspectButton;

    private ActionMode mActionMode;


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
        createPhotoButton(v);
        createPhotoView(v);
        createReportButton(v);
        createSuspectButton(v);
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


    private void createPhotoButton(View v) {
        mPhoteButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        mPhoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        PackageManager manager = getActivity().getPackageManager();
        if (!manager.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhoteButton.setEnabled(false);
        }
    }


    private void createPhotoView(View v) {
        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo p = mCrime.getPhoto();
                if (p == null) {
                    return;
                }

                FragmentManager fragmentManager = getActivity()
                        .getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fragmentManager, DIALOG_DATE);

            }
        });


        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getActivity().startActionMode((ActionMode.Callback) CrimeFragment.this);
                return true;
            }
        });
    }


    private void createReportButton(View v) {
        v.findViewById(R.id.crime_reportButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                        intent = Intent.createChooser(intent, getString(R.string.send_report));
                        startActivity(intent);
                    }
                }
        );
    }


    private void createSuspectButton(View v) {
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(i, REQUEST_CONTACT);
                    }
                }
        );

        if (mCrime.getSuspect() != null) {
            Log.d(TAG, mCrime.getSuspect());
            mSuspectButton.setText(mCrime.getSuspect());
        }
    }


    private void showPhoto() {
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;

        if (p != null) {
            String path = getActivity()
                    .getFileStreamPath(p.getFilename())
                    .getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }


    @Override
    public void  onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDateButton();
        } else if (requestCode == REQUEST_PHOTO) {

            deletePhoto();

            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);

            if (filename != null) {

                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                showPhoto();
            }
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            if (c.getCount() == 0) {
                c.close();
                return;
            }
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            c.close();
        }


    }



    private void deletePhoto() {
        if (mCrime.getPhoto() != null) {
            Photo p = mCrime.getPhoto();
            mCrime.setPhoto(null);
            File file = getActivity().getFileStreamPath(p.getFilename()).getAbsoluteFile();
            if (file.exists()) {
                file.delete();
                Log.d(TAG, "deleted " );
            }
            mPhotoView.setImageDrawable(null);
        }
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


    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = (String) DateFormat.format(dateFormat, mCrime.getDate());
                String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);} else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
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
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.crime_list_item_context, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {return false;}

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_delete_crime:
                deletePhoto();
                actionMode.finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {}
}
