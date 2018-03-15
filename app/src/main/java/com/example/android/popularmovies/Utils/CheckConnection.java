package com.example.android.popularmovies.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;


public class CheckConnection {
    public static boolean checkInternetConnection(Context context) {
        // get Connectivity Manager object to check connection (stackoverflow)
        try {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())
                return true;
            else {
                String message = "Error fetching data: No internet connection";
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
