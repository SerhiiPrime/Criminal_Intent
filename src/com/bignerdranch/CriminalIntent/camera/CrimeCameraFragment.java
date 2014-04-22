package com.bignerdranch.CriminalIntent.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import com.bignerdranch.CriminalIntent.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by badgateway on 20.04.14.
 */
public class CrimeCameraFragment extends Fragment {

    public static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "com.bignerdranch.CriminalIntent.camera.photo_filename";

    private Camera mCamera;
    private SurfaceView mSurfaceView;

    private View mProgressContainer;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            String filename = UUID.randomUUID().toString() + ".jpeg";
            FileOutputStream fos = null;
            boolean success = true;

            try {
                fos = getActivity().openFileOutput(filename, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
                fos.write(bytes);
                Log.d(TAG, "Picture Taken");
            } catch (Exception e) {
                Log.e(TAG, "Error writing to file " + filename, e);
                success = false;
            } finally {

                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.d(TAG, "Error closing file" + filename + e);
                        success = false;
                    }
                }
            }



            if (success) {
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, i);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }



        getActivity().finish();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_camera, container, false);
        createTakePhotoButton(view);

        mProgressContainer = view.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);


        mSurfaceView = (SurfaceView) view.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(createSurfaceHolderCallback());

        return view;
    }




    private SurfaceHolder.Callback createSurfaceHolderCallback() {
        return new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera == null) {
                        return;
                    }
                    mCamera.setPreviewDisplay(holder);
                } catch(IOException ex) {
                    mCamera.release();
                    Log.e(TAG, "Error setting up preview display", ex);
                }
            }




            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera == null) {
                    return;
                }

                mCamera.stopPreview();
            }




            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null) {
                    return;
                }

                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }

                try {
                    mCamera.startPreview();
                } catch (Exception ex) {
                    Log.e(TAG, "Could not start preview", ex);
                    mCamera.release();
                    mCamera = null;
                }
            }

        };
    }




    private void createTakePhotoButton(View view) {
        Button takePicture = (Button) view.findViewById(R.id.crime_camera_takePictureButton);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }
        });
    }





    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }



    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }



    @Override
    public void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
