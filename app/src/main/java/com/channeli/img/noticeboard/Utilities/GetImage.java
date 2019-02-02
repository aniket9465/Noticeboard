package com.channeli.img.noticeboard.Utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.channeli.img.noticeboard.R;

import java.net.HttpURLConnection;
import java.net.URL;

public class GetImage extends AsyncTask<String, Void, Void> {

    private Activity activity;

    public GetImage(Activity activity)
    {
        this.activity=activity;
    }

    @Override
    public Void doInBackground(String... urlString) {

        try {
            // This is getting the url from the string we passed in
            URL url = new URL(urlString[0]);

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

                Bitmap bmp = BitmapFactory.decodeStream(urlConnection.getInputStream());
                ((ImageView)activity.findViewById(R.id.profile)).setImageBitmap(bmp);

            } else {
                Log.d("GetImage :","image retrieval error");
            }

        } catch (Exception e) {
            Log.d("GetImage : ","stream to  image error");
        }
        return null;
    }
}