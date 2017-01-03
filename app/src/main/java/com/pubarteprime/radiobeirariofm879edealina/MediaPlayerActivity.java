package com.pubarteprime.radiobeirariofm879edealina;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import wseemann.media.FFmpegMediaPlayer;

public class MediaPlayerActivity extends AppCompatActivity {

    private FFmpegMediaPlayer mPlayer;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog alertaUrlOff;
    private static final String URLPLAY = "http://server3.webradios.com.br:1935/9482-hdradio/9482.stream/playlist.m3u8";
    private static final String URLMUSIC = "http://server3.webradios.com.br:9482/7.html";
    private TextView txvMusica;
    private CountDownTimer timer;
    private CountDownTimer timerads;
    private Button btn_site;
    private Button btn_whatsapp;
    private Button btn_sms;
    private Button btn_whatsappgroup;
    private Button btn_ligar;
    private AdView mAdView;
    private AlertDialog alertaSobre;
    private Notification noti = null;
    private NotificationManager notificationManager;
    private SeekBar sk_volume;
    private AudioManager audioManager;

    private CoordinatorLayout col_mediaplayer;

    private String TAG = MediaPlayerActivity.class.getSimpleName();
    InterstitialAd mInterstitialAd;
    private String PLAYPAUSE_ACTION = "PLAYPAUSE";
    private String CLOSE_ACTION = "CLOSE";

    private int VOLUME_STREAMING = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        requestPermissions();

        registerReceiver(broadcastReceiver, new IntentFilter("PLAYPAUSE"));
        registerReceiver(broadcastReceiver, new IntentFilter("CLOSE"));

        col_mediaplayer = (CoordinatorLayout) findViewById(R.id.col_mediaplayer);

        sk_volume = new SeekBar(getApplicationContext());

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        VOLUME_STREAMING = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        getSupportActionBar().setIcon(getResources().getDrawable(R.mipmap.ic_launcher));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btn_site = (Button) findViewById(R.id.btn_site);
        btn_whatsapp = (Button) findViewById(R.id.btn_whatsapp);
        btn_whatsappgroup = (Button) findViewById(R.id.btn_whatsappgroup);
        btn_sms = (Button) findViewById(R.id.btn_sms);
        btn_ligar = (Button) findViewById(R.id.btn_ligar);

        btn_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MediaPlayerActivity.this);
                dialog.setContentView(R.layout.activity_enviar_pedido);
                dialog.setTitle("Enviar recado para Site");
                final EditText edt_nome = (EditText) dialog.findViewById(R.id.edt_nome);
                final EditText edt_email = (EditText) dialog.findViewById(R.id.edt_email);
                final EditText edt_recado = (EditText) dialog.findViewById(R.id.edt_recado);
                final Button btn_enviar = (Button) dialog.findViewById(R.id.btn_enviar);
                final Button btn_cancelar = (Button) dialog.findViewById(R.id.btn_cancelar);
                final TextView txv_alerta = (TextView) dialog.findViewById(R.id.txv_alerta);

                btn_enviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nome = edt_nome.getText().toString();
                        String email = edt_email.getText().toString();
                        String recado = edt_recado.getText().toString();

                        if ((nome.length() > 0) && email.length() > 0 && recado.length() > 0) {
                            HttpURLConnection httpURLConnection = null;
                            try {
                                String postData = "nome=" + URLEncoder.encode(nome) + "&email=" + URLEncoder.encode(email) + "&recado=" + URLEncoder.encode(recado);
                                URL url = new URL("http://www.beirariofmedealina.com.br/recados/enviar"); //Enter URL here
                                httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                                httpURLConnection.setRequestProperty("HOST", "www.beirariofmedealina.com.br");
                                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                                httpURLConnection.setFixedLengthStreamingMode(postData.getBytes().length);

                                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                                wr.writeBytes(postData);
                                wr.close();

                                int status = httpURLConnection.getResponseCode();
                                InputStream in;

                                if (status != HttpURLConnection.HTTP_OK)
                                    in = httpURLConnection.getErrorStream();
                                else
                                    in = httpURLConnection.getInputStream();

                                InputStreamReader isw = new InputStreamReader(in);

                                int data = isw.read();
                                String mensagem = "";
                                while (data != -1) {
                                    char current = (char) data;
                                    data = isw.read();
                                    mensagem += current;
                                }
                                Toast.makeText(getApplicationContext(), "Mensagem: " + mensagem, Toast.LENGTH_LONG).show();

                                Toast.makeText(getApplicationContext(), "Recado enviado com sucesso.", Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Não foi possível enviar a mensagem, tente por outro método.", Toast.LENGTH_LONG).show();
                            } finally {
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                            }
                        } else {
                            txv_alerta.setText("Todos os campos deve ser preenchidos.");
                            txv_alerta.setVisibility(View.VISIBLE);
                        }
                    }
                });
                btn_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:" + "6484799253");
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(i, ""));
            }
        });
        btn_ligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "64984799253"));
                startActivity(callIntent);
            }
        });
        btn_whatsappgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://chat.whatsapp.com/1WBNd1dT2xZLkuKb1ekMe7";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", "64984799253");
                smsIntent.putExtra("sms_body","Escreva seu pedido aqui.");
                startActivity(smsIntent);
            }
        });

        createAlertas();
        txvMusica = (TextView) findViewById(R.id.txvMusica);
        txvMusica.setSelected(true);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fab_volume = (FloatingActionButton) findViewById(R.id.fab_volume);
        atualizaMusica();

        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, VOLUME_STREAMING, 0);
            mPlayer = new FFmpegMediaPlayer();
            mPlayer.setDataSource(URLPLAY);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(FFmpegMediaPlayer mediaPlayer) {
                    fab.setVisibility(View.VISIBLE);
                    fab_volume.setVisibility(View.VISIBLE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            alertaUrlOff.show();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(mPlayer.isPlaying()){
                mPlayer.pause();
                fab.setImageResource(R.mipmap.ic_play);
            } else {
                mPlayer.start();
                fab.setImageResource(R.mipmap.ic_pause);
            }
            }
        });

        fab_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sk_volume.getVisibility() == View.INVISIBLE){
                    fab_volume.setBackgroundColor(Color.parseColor("#00ffda"));
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    sk_volume.setProgress(VOLUME_STREAMING);
                    sk_volume.setMax(20);
                    sk_volume.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT));
                    sk_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if(b) audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                            VOLUME_STREAMING = i;
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    sk_volume.setX(10);
                    sk_volume.setY(y - view.getHeight());
                    sk_volume.setVisibility(View.VISIBLE);
                    col_mediaplayer.addView(sk_volume);
                } else {
                    fab_volume.setBackgroundColor(Color.parseColor("#00ff81"));
                    col_mediaplayer.removeView(sk_volume);
                    sk_volume.setVisibility(View.INVISIBLE);
                }
            }
        });

        timer = new CountDownTimer(5000, 20) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try{
                    atualizaMusica();
                    timer.start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        timer.start();

        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));

        adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);


        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        timerads = new CountDownTimer(300000, 20) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try{
                    mInterstitialAd.show();
                    timerads.start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        timerads.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }
                break;
            case 2:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }
                break;
            case 3:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void requestPermissions(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permCall = checkSelfPermission(Manifest.permission.CALL_PHONE);
            int permInternet = checkSelfPermission(Manifest.permission.INTERNET);
            int permAccessNetworkState = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
            if (permCall != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                        1);
                return;
            }
            if (permInternet != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.INTERNET},
                        2);
                return;
            }
            if (permAccessNetworkState != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_NETWORK_STATE},
                        3);
                return;
            }

        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        createNotification();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (noti != null && notificationManager != null){
            notificationManager.cancelAll();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (noti != null && notificationManager != null){
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public void createAlertas(){
        alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Erro!");
        alertBuilder.setMessage("A configuração da URL de Streaming está incorreta. Verifique com o Desenvolvedor.\nmelkysalem@gmail.com");
        alertBuilder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertaUrlOff.dismiss();
            }
        });
        alertBuilder.setPositiveButton("Avisar Desenvolvedor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL,"melkysalem@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT,"URL de Streaming Incorreta");
                intent.putExtra(Intent.EXTRA_TEXT,URLPLAY+" para Radio Beira Rio FM 87,9 Edealina não está válida.");
                startActivity(Intent.createChooser(intent,"Enviar Email"));
            }
        });
        alertaUrlOff = alertBuilder.create();
        alertBuilder.setTitle("Sobre");
        alertBuilder.setMessage("Este aplicativo foi desenvolvido por Melky-Salém.\n Quer um aplicativo? envie um email para melkysalem@gmail.com");
        alertBuilder.setNegativeButton("Fechar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertaUrlOff.dismiss();
            }
        });
        alertBuilder.setPositiveButton("Contactar Desenvolvedor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL,"melkysalem@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Contato para Desenvolvimento de Aplicação");
                intent.putExtra(Intent.EXTRA_TEXT,"Envie aqui sua mensagem!");
                startActivity(Intent.createChooser(intent,"Enviar Email"));
            }
        });
        alertaSobre = alertBuilder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media_player, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(mPlayer.isPlaying()) mPlayer.stop();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            alertBuilder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void atualizaMusica(){
        String musicaAtual = "";
        try{
            musicaAtual = getMusicaAtual();
            musicaAtual = "Música: "+ Html.fromHtml(musicaAtual.substring(musicaAtual.lastIndexOf(',')+1,musicaAtual.lastIndexOf("body")-2));
        } catch (IOException e) {
            musicaAtual = "Música: Não Disponível!";
        }finally {
            txvMusica.setText(musicaAtual);
            txvMusica.setSelected(true);
        }
    }

    public String getMusicaAtual() throws IOException {
        // Build and set timeout values for the request.
        URLConnection connection = (new URL(URLMUSIC)).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();

        // Read and store the result line by line then return the entire string.
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line);
        }
        in.close();

        return html.toString();
    }

    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, MediaPlayerActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Intent playPauseReceive = new Intent();
        playPauseReceive.setAction(PLAYPAUSE_ACTION);
        PendingIntent pendingPlayPauseAction = PendingIntent.getBroadcast(this, 12345, playPauseReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeReceive = new Intent();
        closeReceive.setAction(CLOSE_ACTION);
        PendingIntent pendingCloseAction = PendingIntent.getBroadcast(this, 12345, closeReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        // Actions are just fake
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            noti = new Notification.Builder(this)
                    .setContentTitle("Radio Beira Rio FM")
                    .setSmallIcon(R.drawable.ic_mini)
                    .setContentText("Aplicativo está aberto!")
                    .addAction((mPlayer.isPlaying() ? R.drawable.ic_pause_noti : R.drawable.ic_play_noti),(mPlayer.isPlaying() ? "Pausar" : "Tocar"), pendingPlayPauseAction)
                    .addAction(R.drawable.ic_close_noti,"Fechar",pendingCloseAction)
                    .setContentIntent(pIntent).build();
        } else {
            noti = new Notification.Builder(this)
                    .setContentTitle("Radio Beira Rio FM")
                    .setSmallIcon(R.drawable.ic_mini)
                    .setContentText("Aplicativo está aberto!")
                    .setContentIntent(pIntent).getNotification();

        }
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(0, noti);

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(PLAYPAUSE_ACTION.equals(action)){
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                    createNotification();
                }
                else {
                    mPlayer.start();
                    createNotification();
                }
            } else if(CLOSE_ACTION.equals(action)){
                mPlayer.pause();
                notificationManager.cancelAll();
                MediaPlayerActivity.this.finish();
            }
        }
    };

}
