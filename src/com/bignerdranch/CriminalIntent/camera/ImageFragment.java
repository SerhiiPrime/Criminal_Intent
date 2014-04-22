package com.bignerdranch.CriminalIntent.camera;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bignerdranch.CriminalIntent.utils.PictureUtils;

/**
 * Created by badgateway on 22.04.14.
 */
public class ImageFragment extends DialogFragment {

    private static final String EXTRA_IMAGE_PATH = "com.bignerdranch.CriminalIntent.image_path";

    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mImageView = new ImageView(getActivity());
        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);

        mImageView.setImageDrawable(image);
        return mImageView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        PictureUtils.cleanImageView(mImageView);
    }

    public static ImageFragment newInstance(String imagePath) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_IMAGE_PATH, imagePath);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }
}
