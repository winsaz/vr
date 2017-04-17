package com.winston.vrproject;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    VrPanoramaView panoramaView;
    private ImageLoaderTask backgroundImageLoaderTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        panoramaView = (VrPanoramaView)findViewById(R.id.pano_view);

        ImageLoaderTask task = backgroundImageLoaderTask;
        if (task != null && !task.isCancelled()) {
            // Cancel any task from a previous loading.
            task.cancel(true);
        }

        // pass in the name of the image to load from assets.
        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;

        // use the name of the image in the assets/ directory.
        String panoImageName = "test_1.jpg";

        // create the task passing the widget view and call execute to start.
        task = new ImageLoaderTask(panoramaView, viewOptions, panoImageName);
        task.execute(this.getAssets());
        backgroundImageLoaderTask = task;
//        final VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
//        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
//        String panoImageUrl = "http://cdn1.360cities.net/pano/rami-saarikorpi/00574590_Kauppatori.jpeg/equirect_crop_3_1/5.jpg";
//        Glide.with(this).load(panoImageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                Log.v("Glide Error", "Resource Ready");
//                if (resource != null) {
//                    Log.v("Glide Error", "Resource Loaded");
//                    panoramaView.loadImageFromBitmap(resource, viewOptions);
//                } else {
//                    Log.v("Glide Error", "Resource Null");
//                }
//            }
//
//            @Override
//            public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                super.onLoadFailed(e, errorDrawable);
//                Log.v("Glide Error", "Error : " + e.getLocalizedMessage());
//            }
//        });
    }

    @Override
    public void onPause() {
        panoramaView.pauseRendering();
        super.onPause();
    }
    @Override
    public void onResume() {
        panoramaView.resumeRendering();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        panoramaView.shutdown();
        super.onDestroy();
    }



    public class ImageLoaderTask extends AsyncTask<AssetManager, Void, Bitmap> {
        private static final String TAG = "ImageLoaderTask";
        private final String assetName;
        private final WeakReference<VrPanoramaView> viewReference;
        private final VrPanoramaView.Options viewOptions;
        private WeakReference<Bitmap> lastBitmap = new WeakReference<>(null);
        private String lastName;
        public ImageLoaderTask(VrPanoramaView view, VrPanoramaView.Options viewOptions, String assetName) {
            viewReference = new WeakReference<>(view);
            this.viewOptions = viewOptions;
            this.assetName = assetName;
        }
        @Override
        protected Bitmap doInBackground(AssetManager... params) {
            AssetManager assetManager = params[0];

            if (assetName.equals(lastName) && lastBitmap.get() != null) {
                return lastBitmap.get();
            }

            try(InputStream istr = assetManager.open(assetName)) {
                Bitmap b = BitmapFactory.decodeStream(istr);
                lastBitmap = new WeakReference<>(b);
                lastName = assetName;
                return b;
            } catch (IOException e) {
                Log.e(TAG, "Could not decode default bitmap: " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            final VrPanoramaView vw = viewReference.get();
            if (vw != null && bitmap != null) {
                vw.loadImageFromBitmap(bitmap, viewOptions);
            }
        }
    }
}
