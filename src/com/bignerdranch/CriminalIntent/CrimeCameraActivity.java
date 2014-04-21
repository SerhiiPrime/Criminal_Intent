package com.bignerdranch.CriminalIntent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;
import com.bignerdranch.CriminalIntent.camera.CrimeCameraFragment;

/**
 * Created by badgateway on 20.04.14.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
