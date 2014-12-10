package com.fcd.glasgowcycling.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by michaelhayes on 10/12/14.
 */
public class ImageUtil {
    public static Bitmap getImage(Context context, Uri imageLocation, int targetWidth, int targetHeight) {
        Bitmap image;
        InputStream imageStream = null;
        try {
            imageStream = context.getContentResolver().openInputStream(imageLocation);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Decode to appropriately sized image - saves memory
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        image = BitmapFactory.decodeStream(imageStream, null, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int heightSample = imageHeight / targetHeight;
        int widthSample = imageWidth  / targetWidth;

        if (heightSample < widthSample) {
            options.inSampleSize = heightSample;
        } else {
            options.inSampleSize = widthSample;
        }

        options.inJustDecodeBounds = false;

        try {
            imageStream = context.getContentResolver().openInputStream(imageLocation);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = BitmapFactory.decodeStream(imageStream, null, options);

        if (image.getWidth() > targetWidth || image.getHeight() > targetHeight){
            int x = (image.getWidth() / 2) - (targetWidth/2);
            if (x < 0) {
                x = 0;
            }

            int y = (image.getHeight() / 2) - (targetHeight/2);
            if (y < 0) {
                y = 0;
            }
            image = Bitmap.createBitmap(image, x, y,
                    Math.min(image.getWidth(), targetWidth), Math.min(image.getHeight(), targetHeight));
        }

        return image;
    }
}
