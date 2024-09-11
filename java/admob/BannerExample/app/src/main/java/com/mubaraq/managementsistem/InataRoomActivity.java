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
package com.mubaraq.managementsistem;

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
import com.mubaraq.managementsistem.data.InterstialMe;
import com.mubaraq.managementsistem.data.VARIABELS;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressLint("SetTextI18n")
public class InataRoomActivity extends AppCompatActivity {

    private static final long GAME_LENGTH_MILLISECONDS = 9000;
    private static final String TAG = "InataRoomActivity";

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer, countDownTimerAR;
    private Button retryButton;
    private boolean gamePaused;
    private boolean gameOver;
    private boolean adIsLoading;
    private long timerMilliseconds;


    public final String RELOADE="reload";
    public boolean reload=false,autoclose,autoreload,IsIndo;
    public boolean rotation,vpnprot,indoprot,keepgoing,mixbanerinter,usetestunit;
    public int maxsuccess = 1, maxfail = 1;
    public int gagalt=0,berhasilt=0,cik=0,show=0,impressed = 0,requestot=0,TimerInata=60;
    public String ratess,AdsUnitID;
    Button sett;
    TextView jmlrequest,berhasil,gagal,auto,categori,close,tanggalan,adopen,rate,showon,times,impreson,logprogram;

    @Override
    public void onBackPressed() {
        if(countDownTimerAR != null) {
            countDownTimerAR.cancel();
            countDownTimerAR = null;
        }


        if (isTaskRoot()) {
            // Jika aktivitas ini adalah aktivitas teratas (tidak ada aktivitas lain dalam tumpukan)
            // tambahkan logika untuk membuka menu Home activity atau lakukan tindakan yang sesuai.
            // Misalnya:
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Selesai dengan aktivitas ini
        } else {
            super.onBackPressed(); // Panggil perilaku default jika tidak ada dalam tumpukan teratas
        }
    }

    public String GetUnitID() {

        String randomAdCode = "";

        // Mengambil SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        // Mengambil array ads sebagai Set
        Set<String> layarPembukaAplikasiSet = sharedPref.getStringSet("Iklan_Interstisial", new HashSet<>());

        // Konversi Set menjadi array
        String[] layarPembukaAplikasiArray = layarPembukaAplikasiSet.toArray(new String[0]);

        // Memilih adCode secara random jika ada data
        if (layarPembukaAplikasiArray.length > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(layarPembukaAplikasiArray.length);
            randomAdCode = layarPembukaAplikasiArray[randomIndex];

            // Menampilkan adCode random
            Log.d("AnotherActivity", "Random Ad Code: " + randomAdCode);
        }

        return randomAdCode;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_inata);
        viewBinds();
        CekDateUP();
        data();

        // Mengambil SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        rotation        = (sharedPref.getInt("isRotation", 0) == 1 );
        mixbanerinter   = (sharedPref.getInt("isMixadstype", 0) == 1 );
        usetestunit     = (sharedPref.getInt("isTestAds", 0) == 1 );
        vpnprot         = (sharedPref.getInt("isVPNProtection", 0) == 1 );;
        indoprot        = (sharedPref.getInt("isIndoprot", 0) == 1 );
        keepgoing       = (sharedPref.getInt("isKeepgoing", 0) == 1 );

        maxsuccess  = sharedPref.getInt("maxsuccess", 0);
        maxfail     = sharedPref.getInt("maxfail", 0);

        Log.d("SettingsLog", "maxsuccess: " + maxsuccess);
        Log.d("SettingsLog", "maxfail: " + maxfail);
        Log.d("SettingsLog", "berhasilt: " + berhasilt);
        Log.d("SettingsLog", "gagalt: " + gagalt);

        if (gagalt > maxfail || berhasilt > maxsuccess) {
            String message;
            if (gagalt > maxfail) {
                message = "Maksimal Fail tercukupi : " + gagalt + "/" + maxfail;
            } else {
                message = "Maksimal Load tercukupi : " + berhasilt + "/" + maxsuccess;
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            if (isTaskRoot()) {
                // Jika aktivitas ini adalah aktivitas teratas (tidak ada aktivitas lain dalam tumpukan)
                // tambahkan logika untuk membuka menu Home activity atau lakukan tindakan yang sesuai.
                // Misalnya:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

            finish(); // Selesai dengan aktivitas ini
            return;
        }

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
                    Log.e("DISINI","MASUK SINI 1");
                }

                if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                    invalidateOptionsMenu();
                }
            });

        if (googleMobileAdsConsentManager.canRequestAds()) {
            Log.e("DISINI","MASUK SINI 2");
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
                MobileAds.openAdInspector(
                        InataRoomActivity.this,
                        error -> {
                            // Error will be non-null if ad inspector closed due to an error.
                            if (error != null) {
                                Toast.makeText(InataRoomActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void loadAd() {
        requestot++;
        InterstialMe.saveInteger(InterstialMe.JMLREQUEST,requestot,InataRoomActivity.this);
        dataC();
        logprogram.setText("Log : Memuat iklan interstitial");
        if (adIsLoading || interstitialAd != null) {
          return;
        }
        adIsLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
            this,
            GetUnitID(),
            adRequest,
            new InterstitialAdLoadCallback() {
              @Override
              public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                InataRoomActivity.this.interstitialAd = interstitialAd;
                adIsLoading = false;

                Toast.makeText(InataRoomActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                  berhasilt++;
                  InterstialMe.saveInteger(InterstialMe.BERHASIL,berhasilt,InataRoomActivity.this);
                  dataC();
                  logprogram.setText("Log : Berhasil Memuat iklan interstitial");

                interstitialAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {

                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                            logprogram.setText("Log : Ad was clicked.");
                            cik++;
                            InterstialMe.saveInteger(InterstialMe.OPEN,cik,InataRoomActivity.this);
                            dataC();
                        }

                      @Override
                      public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                          InataRoomActivity.this.interstitialAd = null;
                          logprogram.setText("Log : The ad was dismissed.");
                      }

                      @Override
                      public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                          InataRoomActivity.this.interstitialAd = null;
                        Log.d("TAG", "The ad failed to show.");
                          logprogram.setText("Log : The ad failed to show.");
                      }

                        @Override
                        public void onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            logprogram.setText("Log : Ad recorded an impression.");
                            impressed++;
                            InterstialMe.saveInteger(InterstialMe.IMPRESSED,impressed,InataRoomActivity.this);
                            dataC();

                            if(autoclose) {
                                countDownTimeAR();
                            }
                        }

                        @Override
                      public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                            logprogram.setText("Log : The ad was shown.");
                        show++;
                        InterstialMe.saveInteger(InterstialMe.SHOW,show,InataRoomActivity.this);
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
                  InterstialMe.saveInteger(InterstialMe.GAGAL,gagalt,InataRoomActivity.this);
                  dataC();

                String error =
                    String.format(
                        Locale.US,
                        "domain: %s, code: %d, message: %s",
                        loadAdError.getDomain(),
                        loadAdError.getCode(),
                        loadAdError.getMessage());
                Toast.makeText(InataRoomActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT).show();

                  logprogram.setText("Log : Error "+error);
                  if (keepgoing){
                      if(autoclose) {
                          countDownTimeAR();
                      }
                  }else{
                      countDownTimer.cancel();
                      Toast.makeText(InataRoomActivity.this, "Reload Jika Fail: "+keepgoing, Toast.LENGTH_SHORT).show();
                  }
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

                if(autoclose) {
                    retryButton.performClick();
                }

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
        popup
                .getMenu()
                .findItem(R.id.privacy_settings)
                .setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
        popup.setOnMenuItemClickListener(
                popupMenuItem -> {
                    if (popupMenuItem.getItemId() == R.id.privacy_settings) {
                        pauseGame();
                        // Handle changes to user consent.
                        googleMobileAdsConsentManager.showPrivacyOptionsForm(
                                this,
                                formError -> {
                                    if (formError != null) {
                                        Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    resumeGame();
                                });
                        return true;
                    } else if (popupMenuItem.getItemId() == R.id.ad_inspector) {
                        MobileAds.openAdInspector(
                                this,
                                error -> {
                                    // Error will be non-null if ad inspector closed due to an error.
                                    if (error != null) {
                                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
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
            Log.e("DISINI","MASUK DALAM");
          return;
        }

        MobileAds.initialize(
              this,
              new OnInitializationCompleteListener() {
                  @Override
                  public void onInitializationComplete(InitializationStatus initializationStatus) {
                      // Load an ad.
                      Log.e("DISINI","MASUK DALAM BERHASIL");
                      loadAd();
                  }
              });
    }

    public void countDownTimeAR(){

        // Initialize the CountDownTimer for 60 seconds, with 1-second intervals
        if (countDownTimerAR == null) {
            countDownTimerAR = new CountDownTimer(TimerInata * 1000L, 1000) {

                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {
                    times.setText(millisUntilFinished / 1000 + " s");
                }

                public void onFinish() {
                    if (autoclose) {
                        // Close the current activity
                        finish();

                        if (countDownTimerAR != null) {
                            countDownTimerAR.cancel();
                            countDownTimerAR = null;
                        }

                        Intent intent;
                        if (mixbanerinter) {
                            // Open BananaFixedActivity (You might want to change this to BananaFixedActivity)
                            intent = new Intent(InataRoomActivity.this, InataRoomActivity.class);
                        } else {
                            // Open InataRoomActivity (stays the same)
                            intent = new Intent(InataRoomActivity.this, InataRoomActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }.start();
        }
    }


    public void viewBinds(){
        berhasil=findViewById(R.id.succsestotint);
        jmlrequest=findViewById(R.id.jmlRequest);
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
        gagalt= InterstialMe.getInteger(InterstialMe.GAGAL,this);
        berhasilt=InterstialMe.getInteger(InterstialMe.BERHASIL,this);
        cik=InterstialMe.getInteger(InterstialMe.OPEN,this);
        ratess=InterstialMe.getString(InterstialMe.RATE,this);
        show=InterstialMe.getInteger(InterstialMe.SHOW,this);
        impressed=InterstialMe.getInteger(InterstialMe.IMPRESSED,this);
        requestot=InterstialMe.getInteger(InterstialMe.JMLREQUEST,this);


        // Mengambil SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("DataLogin", Context.MODE_PRIVATE);

        autoclose=(sharedPref.getInt("ReLoadInata", 0) == 1 );
        autoreload=(sharedPref.getInt("ReLoadInata", 0) == 1 );

        TimerInata = sharedPref.getInt("TimerInata", 0);

        reload=getBool(RELOADE,this);

        tanggalan.setText("Estimates calculation in :\n"+InterstialMe.getString(InterstialMe.DATE,this));
        if((sharedPref.getInt("ReLoadInata", 0) == 1 )){
            auto.setText("AUTO RELOAD ACTIVE");
        }else {
            auto.setText("AUTO RELOAD OFF");
            times.setText("");
        }
        if((sharedPref.getInt("ReLoadInata", 0) == 1 )){
            close.setText("AUTO CLOSE AD ACTIVE ");
        }else{
            close.setText("AUTO CLOSE AD OFF");
        }

        berhasil.setText("LOAD :"+berhasilt);
        jmlrequest.setText("Request:"+requestot);
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
        InterstialMe.saveString(InterstialMe.RATE,ratess,this);
        CekDateUP();
        data();
    }
    public void CekDateUP(){
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if(!date.equals(InterstialMe.getString(InterstialMe.DATE,this))){
            InterstialMe.saveString(InterstialMe.DATE,date,this);
            resetResult();

        }else{
            InterstialMe.saveString(InterstialMe.DATE,date,this);
        }
    }
    public void resetResult(){
        InterstialMe.saveInteger(InterstialMe.SHOW,0,InataRoomActivity.this);
        InterstialMe.saveInteger(InterstialMe.GAGAL,0,InataRoomActivity.this);
        InterstialMe.saveInteger(InterstialMe.BERHASIL,0,InataRoomActivity.this);
        InterstialMe.saveInteger(InterstialMe.OPEN,0,InataRoomActivity.this);
        InterstialMe.saveInteger(InterstialMe.IMPRESSED,0,InataRoomActivity.this);
        InterstialMe.saveString(InterstialMe.RATE,"0",this);
        InterstialMe.saveInteger(InterstialMe.JMLREQUEST,0,InataRoomActivity.this);
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
