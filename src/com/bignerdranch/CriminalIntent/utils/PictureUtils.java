package com.bignerdranch.CriminalIntent.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by badgateway on 22.04.14.
 */
public class PictureUtils {


    public static BitmapDrawable getScaledDrawable(Activity activity, String path) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        BitmapFactory.Options options =     new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);


        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(destHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(activity.getResources(), bitmap);
    }



    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            return;
        }

        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}
