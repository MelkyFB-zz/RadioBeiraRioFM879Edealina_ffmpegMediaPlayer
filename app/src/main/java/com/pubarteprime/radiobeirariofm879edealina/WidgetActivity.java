package com.pubarteprime.radiobeirariofm879edealina;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * Created by melkysalem on 03/01/17.
 */

public class WidgetActivity extends AppWidgetProvider {

    private static final String URLMUSIC = "http://server3.webradios.com.br:9482/7.html";
    private String PLAYPAUSE_ACTION = "PLAYPAUSE";
    private String CLOSE_ACTION = "CLOSE";

    public void onUpdate(Context context, AppWidgetManager appwidgetManager, int[] appWidgetIds){
        ComponentName thisWidget = new ComponentName(context,
                WidgetActivity.class);
        int[] allWidgetIds = appwidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.activity_widget);

            remoteViews.setTextViewText(R.id.txv_widget_musica, getMusicaAtual());

            Intent i = new Intent(context,MediaPlayerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setOnClickPendingIntent(R.id.ibt_widget_logo,pendingIntent);

            appwidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public String getMusicaAtual() {
        StringBuilder html = new StringBuilder();
        String musicaAtual = "";
        try{
            // Build and set timeout values for the request.
            URLConnection connection = (new URL(URLMUSIC)).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            // Read and store the result line by line then return the entire string.
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            for (String line; (line = reader.readLine()) != null; ) {
                html.append(line);
            }
            in.close();
            musicaAtual = "Música: "+ Html.fromHtml(html.toString().substring(html.toString().lastIndexOf(',')+1,html.toString().lastIndexOf("body")-2));
        } catch (MalformedURLException e) {
            musicaAtual = "Música: Não Disponível!";
            e.printStackTrace();
        } catch (IOException e) {
            musicaAtual = "Música: Não Disponível!";
            e.printStackTrace();
        } finally {
            return musicaAtual;
        }
    }

}
