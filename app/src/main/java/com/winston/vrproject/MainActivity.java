package com.winston.vrproject;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

public class MainActivity extends AppCompatActivity {
    VrPanoramaView panoramaView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        panoramaView = (VrPanoramaView)findViewById(R.id.pano_view);
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        String panoImageUrl = "https://upload.wikimedia.org/wikipedia/commons/6/6f/Helvellyn_Striding_Edge_360_Panorama%2C_Lake_District_-_June_09.jpg";
        Glide.with(this).load(getResources().getDrawable(R.drawable.1)).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (resource != null) {
                    panoramaView.loadImageFromBitmap(resource, new VrPanoramaView.Options());
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                Log.v("Glide Error", "Error : " + e.getLocalizedMessage());
            }
        });
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
}
