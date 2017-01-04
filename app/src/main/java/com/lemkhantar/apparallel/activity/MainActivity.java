package com.lemkhantar.apparallel.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.lemkhantar.apparallel.R;
import com.lemkhantar.apparallel.database.DBManager;
import com.lemkhantar.apparallel.entity.Tache;
import com.lemkhantar.apparallel.service.TimeService;
import com.lemkhantar.apparallel.view.TaskProgressBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TaskProgressBar taskProgressBar;
    Dialog dialog;
    TimeService myService;
    boolean isBound = false;
    Typeface font;

    int tacheActive = -1;
    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            TimeService.MyLocalBinder binder = (TimeService.MyLocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };
    Handler handler =new Handler();
    final Runnable r = new Runnable() {
        public void run() {
            handler.postDelayed(this, 5000);
            refresh();
        }
    };


    InterstitialAd interstitial;
    AdRequest adRequest2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, TimeService.class);
        startService(intent);
        intent = new Intent(this, TimeService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
        handler.postDelayed(r, 0000);
        font = Typeface.createFromAsset(getAssets(), "fonts/police.ttf");
        ((TextView)findViewById(R.id.titre)).setTypeface(font);
        ((TextView)findViewById(R.id.titre)).setTextColor(Color.WHITE);
        final DBManager dbManager = new DBManager(this);
        dbManager.openDataBase();
        tacheActive = dbManager.getTacheActive();
        refresh();


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.Interstitial_ad_unit_id));
        adRequest2 = new AdRequest.Builder().build();

    }


    public void displayInterstitial() {

        interstitial.loadAd(adRequest2);
        interstitial.show();

    }


    public void ajoutertache(View view) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ajouttache);
        ((TextView)dialog.findViewById(R.id.inputTitre)).setTypeface(font);
        ((TextView)dialog.findViewById(R.id.inputDuree)).setTypeface(font);
        ((TextView)dialog.findViewById(R.id.annuler)).setTypeface(font);
        ((TextView)dialog.findViewById(R.id.ajouter)).setTypeface(font);
        ((TextView)dialog.findViewById(R.id.inputTitre)).setTextColor(Color.WHITE);
        ((TextView)dialog.findViewById(R.id.inputDuree)).setTextColor(Color.WHITE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public void aide(View view)
    {
        displayInterstitial();
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.aide);
        ((TextView)dialog.findViewById(R.id.aideTexte)).setTypeface(font);
        ((TextView)dialog.findViewById(R.id.aideButton)).setTypeface(font);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }


    public void annuler(View v)
    {
        dialog.hide();
    }

    public void ajouter(View view) {
        String titre = ((EditText)dialog.findViewById(R.id.titre)).getText().toString();
        String duree = ((EditText)dialog.findViewById(R.id.duree)).getText().toString();

        int dureee;
        if(!titre.equals(""))
        {
            try
            {
                dureee = Integer.parseInt(duree);
                Tache tache = new Tache();
                tache.set_titre(titre);
                tache.set_duree(dureee*60);
                tache.set_tempsecoule(0);
                DBManager dbManager = new DBManager(this);
                dbManager.openDataBase();
                dbManager.ajouterTache(tache);
                refresh();
                dialog.dismiss();
                displayInterstitial();
            }
            catch (NumberFormatException e)
            {
                Toast.makeText(this, "Duree invalide !", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Titre vide !", Toast.LENGTH_SHORT).show();





    }

    public void refresh() {
        ((LinearLayout)findViewById(R.id.layout)).removeAllViews();

        final DBManager dbManager = new DBManager(this);
        dbManager.openDataBase();

        final ArrayList<Tache> taches = dbManager.getAllTache();

        for(int i=0; i<taches.size(); i++)
        {
            taskProgressBar = new TaskProgressBar(this);
            taskProgressBar.getTitre().setText(taches.get(i).get_titre());
            taskProgressBar.getTitre().setTypeface(font);
            if(taches.get(i).get_duree()==0)
            {
                taskProgressBar.getDuree().setText(taches.get(i).get_tempsecoule()/60+"/"+taches.get(i).get_duree()/60+" min - 100 %");

            }
            else
            {
                taskProgressBar.getDuree().setText(taches.get(i).get_tempsecoule()/60+"/"+taches.get(i).get_duree()/60+" min - "+((taches.get(i).get_tempsecoule()*100)/taches.get(i).get_duree())+" %");

            }
            taskProgressBar.getDuree().setTypeface(font);
            taskProgressBar.getProgressBar().setMax(taches.get(i).get_duree());
            taskProgressBar.getProgressBar().setProgress(taches.get(i).get_tempsecoule());
            taskProgressBar.getProgressBar().setId(taches.get(i).get_id());
            final int finalI = i;
            taskProgressBar.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myService.updateId(taches.get(finalI).get_id());
                    tacheActive = taches.get(finalI).get_id();
                    dbManager.updateTacheActive(tacheActive);
                    refresh();
                    displayInterstitial();
                }
            });
            taskProgressBar.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Tache : "+taches.get(finalI).get_titre())
                            .setNeutralButton("Supprimer", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbManager.deleteTache(taches.get(finalI).get_id());
                                    if(tacheActive == taches.get(finalI).get_id())
                                    {
                                        myService.updateId(-1);
                                        dbManager.updateTacheActive(-1);
                                    }
                                    refresh();
                                }
                            })
                            .setNegativeButton("Remise a zero", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbManager.openDataBase();
                                    dbManager.reinitialiserTache(taches.get(finalI).get_id());
                                    if(tacheActive == taches.get(finalI).get_id())
                                    {
                                        myService.updateId(-1);
                                        dbManager.updateTacheActive(-1);
                                    }
                                    refresh();
                                }
                            })
                            .setPositiveButton("Desactiver", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbManager.openDataBase();
                                    if(tacheActive == taches.get(finalI).get_id())
                                    {
                                        tacheActive = -1;
                                        myService.updateId(-1);
                                        dbManager.updateTacheActive(-1);
                                    }
                                    refresh();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                    return false;
                }
            });
             if(taches.get(i).get_tempsecoule()>=taches.get(i).get_duree())
        {
            taskProgressBar.getProgressBar().getProgressDrawable().setColorFilter(
                    Color.rgb(153,0,153), android.graphics.PorterDuff.Mode.SRC_IN);
            taskProgressBar.getTitre().setTextColor(Color.rgb(153,0,153));
            taskProgressBar.getDuree().setTextColor(Color.rgb(153,0,153));
        }
            else if(tacheActive == taches.get(i).get_id())
            {
                taskProgressBar.getProgressBar().getProgressDrawable().setColorFilter(
                        Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
                taskProgressBar.getTitre().setTextColor(Color.BLACK);
                taskProgressBar.getDuree().setTextColor(Color.BLACK);
            }

            else
            {
                taskProgressBar.getProgressBar().getProgressDrawable().setColorFilter(
                        Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
                taskProgressBar.getTitre().setTextColor(Color.WHITE);
                taskProgressBar.getDuree().setTextColor(Color.WHITE);
            }
            ((LinearLayout)findViewById(R.id.layout)).addView(taskProgressBar);
        }


    }

    @Override
    public void onBackPressed() {
        displayInterstitial();
        super.onBackPressed();
    }
}
