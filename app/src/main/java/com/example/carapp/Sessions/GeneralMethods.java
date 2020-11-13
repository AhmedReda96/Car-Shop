package com.example.carapp.Sessions;

import android.content.Context;
import android.content.res.Configuration;

import com.example.carapp.Sessions.internetcheck.ConnectionDetector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GeneralMethods {
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;


    public void setLocale(Context context) {

        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());


    }

    public Boolean checkInternet(Context context){

        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            return  false;
        }
        return  true;
    }


    public String getDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy h:mm aa", Locale.getDefault());
        return sdf.format(new Date());

    }




    }



