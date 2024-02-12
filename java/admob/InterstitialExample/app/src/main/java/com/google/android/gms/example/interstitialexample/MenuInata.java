/*
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.interstitialexample;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.gms.example.interstitialexample.data.InterstialMe;
import com.google.android.gms.example.interstitialexample.data.VARIABELS;

import static com.google.android.gms.example.interstitialexample.data.InterstialMe.BERHASIL;
import static com.google.android.gms.example.interstitialexample.data.InterstialMe.DATE;
import static com.google.android.gms.example.interstitialexample.data.InterstialMe.GAGAL;
import static com.google.android.gms.example.interstitialexample.data.InterstialMe.OPEN;
import static com.google.android.gms.example.interstitialexample.data.InterstialMe.RATE;
import static com.google.android.gms.example.interstitialexample.data.InterstialMe.SHOW;
import static com.google.android.gms.example.interstitialexample.data.InterstialMe.IMPRESSED;
import static com.google.android.gms.example.interstitialexample.data.InterstialMe.saveString;

@SuppressLint("SetTextI18n")
public class MenuInata extends AppCompatActivity {

    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String TAG = "MenuInata";

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private Button retryButton;
    private boolean gamePaused;
    private boolean gameOver;
    private boolean adIsLoading;
    private long timerMilliseconds;


    public final String RELOADE="reload";
    public boolean reload=false,autoclose,autoreload,IsIndo;
    public boolean rotation,vpnprot,indoprot,keepgoing,mixbanerinter,usetestunit;
    public int maxsuccess = 1, maxfail = 1;
    public int gagalt=0,berhasilt=0,cik=0,show=0,impressed = 0;
    public String ratess,AdsUnitID;
    Button sett;
    TextView berhasil,gagal,auto,categori,close,tanggalan,adopen,rate,showon,times,impreson,logprogram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inata);
        viewBinds();
        CekDateUP();
        data();

        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(
            this,
            consentError -> {
                if (consentError != null) {
                    Log.w(
                        TAG,
                        String.format(
                            "%s: %s",
                            consentError.getErrorCode(),
                            consentError.getMessage()));
                }

                startGame();

                if (googleMobileAdsConsentManager.canRequestAds()) {
                    initializeMobileAdsSdk();
                }

                if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                    invalidateOptionsMenu();
                }
            });

        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }

        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });

        Button buttonreset = findViewById(R.id.reset);
        buttonreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetResult();
            }
        });

        Button buttonsetting = findViewById(R.id.set_interes);
        buttonsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuInata.this, MenuSetting.class);
                startActivity(intent);
            }
        });
    }

    private void loadAd() {
        logprogram.setText("Log : Memuat iklan interstitial");
    if (adIsLoading || interstitialAd != null) {
      return;
    }
    adIsLoading = true;
    AdRequest adRequest = new AdRequest.Builder().build();
    InterstitialAd.load(
        this,
        AD_UNIT_ID,
        adRequest,
        new InterstitialAdLoadCallback() {
          @Override
          public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            // The mInterstitialAd reference will be null until
            // an ad is loaded.
            MenuInata.this.interstitialAd = interstitialAd;
            adIsLoading = false;

            Toast.makeText(MenuInata.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
              berhasilt++;
              InterstialMe.saveInteger(BERHASIL,berhasilt,MenuInata.this);
              dataC();
              logprogram.setText("Log : Berhasil Memuat iklan interstitial");
            interstitialAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {

                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        logprogram.setText("Log : Ad was clicked.");
                        cik++;
                        InterstialMe.saveInteger(OPEN,cik,MenuInata.this);
                        dataC();
                    }

                  @Override
                  public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    MenuInata.this.interstitialAd = null;
                      logprogram.setText("Log : The ad was dismissed.");
                  }

                  @Override
                  public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    MenuInata.this.interstitialAd = null;
                    Log.d("TAG", "The ad failed to show.");
                      logprogram.setText("Log : The ad failed to show.");
                  }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        logprogram.setText("Log : Ad recorded an impression.");
                        impressed++;
                        InterstialMe.saveInteger(IMPRESSED,impressed,MenuInata.this);
                        dataC();
                    }

                    @Override
                  public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                        logprogram.setText("Log : The ad was shown.");
                    show++;
                    InterstialMe.saveInteger(SHOW,show,MenuInata.this);
                    dataC();
                  }
                });
          }

          @SuppressLint("SuspiciousIndentation")
          @Override
          public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            // Handle the error
            Log.i(TAG, loadAdError.getMessage());
            interstitialAd = null;
            adIsLoading = false;

              gagalt++;
              InterstialMe.saveInteger(GAGAL,gagalt,MenuInata.this);
              dataC();

            String error =
                String.format(
                    java.util.Locale.US,
                    "domain: %s, code: %d, message: %s",
                    loadAdError.getDomain(),
                    loadAdError.getCode(),
                    loadAdError.getMessage());
            Toast.makeText(MenuInata.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT).show();

              logprogram.setText("Log : Error "+error);
          }
        });
  }

    private void createTimer(final long milliseconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        final TextView textView = findViewById(R.id.timer);

        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
                textView.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                gameOver = true;
                textView.setText("done!");
                retryButton.setVisibility(View.VISIBLE);
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem moreMenu = menu.findItem(R.id.action_more);
        moreMenu.setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuItemView = findViewById(item.getItemId());
        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(
            popupMenuItem -> {
                if (popupMenuItem.getItemId() == R.id.privacy_settings) {
                    pauseGame();
                    googleMobileAdsConsentManager.showPrivacyOptionsForm(
                          this,
                          formError -> {
                              if (formError != null) {
                                  Toast.makeText(
                                      this,
                                      formError.getMessage(),
                                      Toast.LENGTH_SHORT).show();
                              }
                              resumeGame();
                          });
                    return true;
                }
                return false;
            });
        return super.onOptionsItemSelected(item);
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            startGame();
            if (googleMobileAdsConsentManager.canRequestAds()) {
                loadAd();
            }
        }
    }

    private void startGame() {
        // Hide the button, and kick off the timer.
        retryButton.setVisibility(View.INVISIBLE);
        createTimer(GAME_LENGTH_MILLISECONDS);
        gamePaused = false;
        gameOver = false;
    }

    private void resumeGame() {
        if (gameOver || !gamePaused) {
          return;
        }
        // Create a new timer for the correct length.
        gamePaused = false;
        createTimer(timerMilliseconds);
    }

    private void pauseGame() {
        if (gameOver || gamePaused) {
          return;
        }
        countDownTimer.cancel();
        gamePaused = true;
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
          return;
        }

        MobileAds.initialize(
              this,
              new OnInitializationCompleteListener() {
                  @Override
                  public void onInitializationComplete(InitializationStatus initializationStatus) {
                      // Load an ad.
                      loadAd();
                  }
              });
    }


    public void viewBinds(){
        berhasil=findViewById(R.id.succsestotint);
        gagal=findViewById(R.id.failtotint);
        adopen=findViewById(R.id.adopenBanint);
        tanggalan=findViewById(R.id.tanggalint);
        rate=findViewById(R.id.rateSBanint);
        auto=findViewById(R.id.A11);
        close=findViewById(R.id.timeautoReloadINTERTV);
        categori=findViewById(R.id.keywordInter);
        logprogram=findViewById(R.id.logprogram);
        sett=findViewById(R.id.set_interes);
        showon=findViewById(R.id.shoewint);
        impreson=findViewById(R.id.impresint);
        times=findViewById(R.id.timede);
    }

    @SuppressLint("SetTextI18n")
    public void data(){
        gagalt= InterstialMe.getInteger(GAGAL,this);
        berhasilt=InterstialMe.getInteger(BERHASIL,this);
        cik=InterstialMe.getInteger(OPEN,this);
        ratess=InterstialMe.getString(RATE,this);
        show=InterstialMe.getInteger(SHOW,this);
        impressed=InterstialMe.getInteger(IMPRESSED,this);


//        autoclose=VARIABELS.getBool(SettingAct.AUTORELOADINTER,this);
//        autoreload=VARIABELS.getBool(SettingAct.AUTORELOADINTER,this);
//        reload=getBool(RELOADE,this);

        tanggalan.setText("Estimates calculation in :\n"+InterstialMe.getString(DATE,this));
//        if(VARIABELS.getBool(SettingAct.AUTORELOADINTER,this)){
//            auto.setText("AUTO RELOAD ACTIVE");
//        }else {
//            auto.setText("AUTO RELOAD OFF");
//            times.setText("");
//        }
//        if(VARIABELS.getBool(SettingAct.AUTORELOADINTER,this)){
//            close.setText("AUTO CLOSE AD ACTIVE ");
//        }else{
//            close.setText("AUTO CLOSE AD OFF");
//        }
//        categori.setText("Keyword : "+VARIABELS.getString(SettingAct.CATEGORYAD,InataActivity.this,getString(R.string.app_name)));
        berhasil.setText("LOAD :"+berhasilt);
        gagal.setText("FAILED :"+gagalt);
        adopen.setText("CLICK :"+cik);
        rate.setText("CTR :"+ratess+"%");
        showon.setText("SHOW :"+show);
        impreson.setText("IMPRES :"+impressed);
    }

    public void dataC(){
        float total = ((float)cik/(float)show)*100;
        DecimalFormat df = new DecimalFormat("####.##");
        ratess = df.format(total);
        saveString(RATE,ratess,this);
        CekDateUP();
        data();
    }
    public void CekDateUP(){
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if(!date.equals(InterstialMe.getString(DATE,this))){
            saveString(DATE,date,this);
            resetResult();

        }else{
            saveString(DATE,date,this);
        }
    }
    public void resetResult(){
        InterstialMe.saveInteger(SHOW,0,MenuInata.this);
        InterstialMe.saveInteger(GAGAL,0,MenuInata.this);
        InterstialMe.saveInteger(BERHASIL,0,MenuInata.this);
        InterstialMe.saveInteger(OPEN,0,MenuInata.this);
        InterstialMe.saveInteger(IMPRESSED,0,MenuInata.this);
        saveString(RATE,"0",this);
        data();
        CekDateUP();
    }
    @SuppressLint("ApplySharedPref")
    public static void saveBool(String key, Boolean value, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    @NonNull
    public static Boolean getBool(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }
}
