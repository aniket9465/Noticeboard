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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class GetImage extends AsyncTask<String, Void, Void> {

    private Activity activity;

    public GetImage(Activity activity)
    {
        this.activity=activity;
    }

    @Override
    public Void doInBackground(String... urlString) {
        disableSSLCertificateChecking();
        try {
            // This is getting the url from the string we passed in
            URL url = new URL(urlString[0]);

            // Create the urlConnection
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

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

    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}