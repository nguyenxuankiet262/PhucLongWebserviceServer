package com.dhkhtn.xk.phuclongserverappver2.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dhkhtn.xk.phuclongserverappver2.Model.Store;
import com.dhkhtn.xk.phuclongserverappver2.Model.Token;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.FCMClient;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IFCMService;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Common {
    public static final String BASE_URL = "https://phuclongvn.000webhostapp.com/";
    //public static final String BASE_URL = "http://192.168.1.4/phuclong/";
    public static final String FCM_URL = "https://fcm.googleapis.com/";
    public static boolean checkChooseImageFromAdapter = false;

    public static List<Store> CurrentStore;
    public static Token CurrentToken = null;

    public static IPhucLongAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IPhucLongAPI.class);
    }

    public static IFCMService getFCMService(){
        return FCMClient.getClient(FCM_URL).create(IFCMService.class);
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            for(int i = 0; i<infos.length;i++) {
                if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String ConvertIntToMoney(int money){
        return NumberFormat.getNumberInstance(Locale.US).format(money) + " VNĐ";
    }
}
