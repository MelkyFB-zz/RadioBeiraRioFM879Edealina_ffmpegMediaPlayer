package com.pubarteprime.radiobeirariofm879edealina;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by melkysalem on 02/01/17.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "PLAYPAUSE":
                PlayPauseSendBroadCast(context,intent);
                break;
            case "CLOSE":
                CloseSendBroadCast(context,intent);
                break;
        }
    }
    public static void PlayPauseSendBroadCast(Context context, Intent intent){
        context.sendBroadcast(new Intent("PLAYPAUSE"));
    }
    public static void CloseSendBroadCast(Context context, Intent intent){
        context.sendBroadcast(new Intent("CLOSE"));
    }
}
