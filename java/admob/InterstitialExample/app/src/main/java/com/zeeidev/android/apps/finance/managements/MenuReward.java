package com.zeeidev.android.apps.finance.managements;


import com.zeeidev.android.apps.finance.managements.data.InterstialMe;
import com.zeeidev.android.apps.finance.managements.data.RewardMe;
import com.zeeidev.android.apps.finance.managements.data.VARIABELS;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

/** Main Activity. Inflates main activity xml. */
@SuppressLint("SetTextI18n")
public class MenuReward extends AppCompatActivity {
    private static final String AD_UNIT_ID = "ca-app-pub-7944170612384609/5624255326";
    private static final long COUNTER_TIME = 10;
    private static final int GAME_OVER_REWARD = 1;
    private static final String TAG = "MenuReward";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    private int coinCount;
    private TextView coinCountText;
    private CountDownTimer countDownTimer, countDownTimerAR;
    private boolean gameOver;
    private boolean gamePaused;

    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private RewardedAd rewardedAd;
    private Button retryButton;
    private Button showVideoButton;
    private long timeRemaining;
    boolean isLoading;


    public final String RELOADE="reload";
    public boolean reload=false,autoclose,autoreload,IsIndo;
    public boolean rotation,vpnprot,indoprot,keepgoing,mixbanerinter,usetestunit;
    public int maxsuccess = 1, maxfail = 1;
    public int gagalt=0,berhasilt=0,cik=0,show=0,impressed = 0,requestot=0;
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
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish(); // Selesai dengan aktivitas ini
        } else {
            super.onBackPressed(); // Panggil perilaku default jika tidak ada dalam tumpukan teratas
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        // ====== SETTING UI =======
            viewBinds();
            CekDateUP();
            data();

            rotation        = VARIABELS.getBool(MenuSetting.ROTATION,this);
            mixbanerinter   = VARIABELS.getBool(MenuSetting.MIXBANERINTER,this);
            usetestunit     = VARIABELS.getBool(MenuSetting.USETESTUNIT,this);
            vpnprot         = VARIABELS.getBool(MenuSetting.VPNPROT,this);
            indoprot        = VARIABELS.getBool(MenuSetting.INDOPROT,this);
            keepgoing       = VARIABELS.getBool(MenuSetting.KEEPGOING,this);

            maxsuccess  = VARIABELS.getInteger(MenuSetting.MAXLOAD,this,10);
            maxfail     = VARIABELS.getInteger(MenuSetting.MINLOAD,this,10);

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
                    Intent intent = new Intent(this, Home.class);
                    startActivity(intent);
                }

                finish(); // Selesai dengan aktivitas ini
                return;
            }

        // ====== END SETTING UI =======

        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        googleMobileAdsConsentManager =
                GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(
                this,
                consentError -> {
                    if (consentError != null) {
                        // Consent not obtained in current session.
                        Log.w(
                                TAG,
                                String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                    }

                    startGame();

                    if (googleMobileAdsConsentManager.canRequestAds()) {
                        initializeMobileAdsSdk();
                    }

                    if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                        // Regenerate the options menu to include a privacy setting.
                        invalidateOptionsMenu();
                    }
                });

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }

        // Create the "retry" button, which tries to show a rewarded ad between game plays.
        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startGame();
                        if (
                                rewardedAd != null
                                        && !isLoading
                                        && googleMobileAdsConsentManager.canRequestAds()
                        ) {
                            loadRewardedAd();
                        }
                    }
                });

        // Create the "show" button, which shows a rewarded video if one is loaded.
        showVideoButton = findViewById(R.id.show_video_button);
        showVideoButton.setVisibility(View.INVISIBLE);
        showVideoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showRewardedVideo();
                    }
                });

        // Display current coin count to user.
        coinCountText = findViewById(R.id.coin_count_text);
        coinCount = 0;
        coinCountText.setText("Coins: " + coinCount);

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
                Intent intent = new Intent(MenuReward.this, MenuSetting.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeGame();
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
                        // Handle changes to user consent.
                        pauseGame();
                        googleMobileAdsConsentManager.showPrivacyOptionsForm(
                                this,
                                formError -> {
                                    if (formError != null) {
                                        Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    resumeGame();
                                });
                        return true;
                    }
                    return false;
                });
        return super.onOptionsItemSelected(item);
    }

    private void pauseGame() {
        if (gameOver || gamePaused) {
            return;
        }
        countDownTimer.cancel();
        gamePaused = true;
    }

    private void resumeGame() {
        if (gameOver || !gamePaused) {
            return;
        }
        createTimer(timeRemaining);
        gamePaused = false;
    }

    private void loadRewardedAd() {
        requestot++;
        InterstialMe.saveInteger(InterstialMe.JMLREQUEST,requestot,MenuReward.this);
        dataC();
        logprogram.setText("Log : Memuat iklan interstitial");

        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    AD_UNIT_ID,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.getMessage());
                            rewardedAd = null;
                            MenuReward.this.isLoading = false;
                            Toast.makeText(MenuReward.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();

                            String error =
                                    String.format(
                                            java.util.Locale.US,
                                            "domain: %s, code: %d, message: %s",
                                            loadAdError.getDomain(),
                                            loadAdError.getCode(),
                                            loadAdError.getMessage());

                            gagalt++;
                            InterstialMe.saveInteger(InterstialMe.GAGAL,gagalt,MenuReward.this);
                            dataC();
                            logprogram.setText("Log : Error "+error);
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            MenuReward.this.rewardedAd = rewardedAd;
                            Log.d(TAG, "onAdLoaded");
                            MenuReward.this.isLoading = false;
                            Toast.makeText(MenuReward.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

                            berhasilt++;
                            InterstialMe.saveInteger(InterstialMe.BERHASIL,berhasilt,MenuReward.this);
                            dataC();
                            logprogram.setText("Log : Berhasil Memuat iklan Reward");
                        }
                    });
        }
    }

    private void addCoins(int coins) {
        coinCount += coins;
        coinCountText.setText("Coins: " + coinCount);
    }

    private void startGame() {
        // Hide the retry button, load the ad, and start the timer.
        retryButton.setVisibility(View.INVISIBLE);
        showVideoButton.setVisibility(View.INVISIBLE);
        createTimer(COUNTER_TIME);
        gamePaused = false;
        gameOver = false;
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private void createTimer(long time) {
        final TextView textView = findViewById(R.id.timer);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer =
                new CountDownTimer(time * 1000, 50) {
                    @Override
                    public void onTick(long millisUnitFinished) {
                        timeRemaining = ((millisUnitFinished / 1000) + 1);
                        textView.setText("seconds remaining: " + timeRemaining);
                    }

                    @Override
                    public void onFinish() {
                        if (rewardedAd != null) {
                            showVideoButton.setVisibility(View.VISIBLE);
                        }
                        textView.setText("You Lose!");
                        addCoins(GAME_OVER_REWARD);
                        retryButton.setVisibility(View.VISIBLE);
                        gameOver = true;

                        if(autoclose) {
                            showVideoButton.performClick();
                        }
                    }
                };
        countDownTimer.start();
    }

    private void showRewardedVideo() {
        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }
        showVideoButton.setVisibility(View.INVISIBLE);

        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.");
                        logprogram.setText("Log : Ad was clicked.");
                        cik++;
                        InterstialMe.saveInteger(InterstialMe.OPEN,cik,MenuReward.this);
                        dataC();
                    }
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "onAdShowedFullScreenContent");
                        Toast.makeText(MenuReward.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
                                .show();

                        logprogram.setText("Log : The ad was shown.");
                        show++;
                        InterstialMe.saveInteger(InterstialMe.SHOW,show,MenuReward.this);
                        dataC();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                        Toast.makeText(
                                        MenuReward.this, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
                                .show();
                        logprogram.setText("Log : The ad failed to show.");
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.");
                        logprogram.setText("Log : Ad recorded an impression.");
                        impressed++;
                        InterstialMe.saveInteger(InterstialMe.IMPRESSED,impressed,MenuReward.this);
                        dataC();

                        if(autoclose) {
                            countDownTimeAR();
                        }
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        logprogram.setText("Log : The ad was dismissed.");
                        rewardedAd = null;
                        Log.d(TAG, "onAdDismissedFullScreenContent");
                        Toast.makeText(MenuReward.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
                                .show();
//                        if (googleMobileAdsConsentManager.canRequestAds()) {
//                            // Preload the next rewarded ad.
//                            MenuReward.this.loadRewardedAd();
//                        }
                    }
                });
        Activity activityContext = MenuReward.this;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        logprogram.setText("Log : The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();

                        addCoins(rewardAmount);
                    }
                });
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Load an ad.
        loadRewardedAd();
    }

    public void countDownTimeAR(){
        if (countDownTimerAR == null) countDownTimerAR = new CountDownTimer(VARIABELS.getInteger(MenuSetting.DURATIONRELOADINTERSTITIAL,this,60)*1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                times.setText(millisUntilFinished/1000+" s");
            }

            public void onFinish() {
                if(autoclose){
                    // Menutup aktivitas saat ini
                    finish();

                    if(countDownTimerAR != null) {
                        countDownTimerAR.cancel();
                        countDownTimerAR = null;
                    }

                    Intent intent;
                    if (mixbanerinter){
                        // Membuka BananaFixedActivity
                        intent = new Intent(MenuReward.this, MenuBanana.class);
                    }else{
                        intent = new Intent(MenuReward.this, MenuReward.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }.start();
    }

    // ======= SETTING UI ===========
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
        gagalt= RewardMe.getInteger(RewardMe.GAGAL,this);
        berhasilt=RewardMe.getInteger(RewardMe.BERHASIL,this);
        cik=RewardMe.getInteger(RewardMe.OPEN,this);
        ratess=RewardMe.getString(RewardMe.RATE,this);
        show=RewardMe.getInteger(RewardMe.SHOW,this);
        impressed=RewardMe.getInteger(RewardMe.IMPRESSED,this);
        requestot=RewardMe.getInteger(RewardMe.JMLREQUEST,this);


        autoclose=VARIABELS.getBool(MenuSetting.AUTORELOADINTER,this);
        autoreload=VARIABELS.getBool(MenuSetting.AUTORELOADINTER,this);
        reload=getBool(RELOADE,this);

        tanggalan.setText("Estimates calculation in :\n"+RewardMe.getString(RewardMe.DATE,this));
        if(VARIABELS.getBool(MenuSetting.AUTORELOADINTER,this)){
            auto.setText("AUTO RELOAD ACTIVE");
        }else {
            auto.setText("AUTO RELOAD OFF");
            times.setText("");
        }
        if(VARIABELS.getBool(MenuSetting.AUTORELOADINTER,this)){
            close.setText("AUTO CLOSE AD ACTIVE ");
        }else{
            close.setText("AUTO CLOSE AD OFF");
        }
        categori.setText("Keyword : "+VARIABELS.getString(MenuSetting.CATEGORYAD,MenuReward.this,getString(R.string.app_name)));
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
        RewardMe.saveString(RewardMe.RATE,ratess,this);
        CekDateUP();
        data();
    }
    public void CekDateUP(){
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if(!date.equals(RewardMe.getString(RewardMe.DATE,this))){
            RewardMe.saveString(RewardMe.DATE,date,this);
            resetResult();

        }else{
            RewardMe.saveString(RewardMe.DATE,date,this);
        }
    }
    public void resetResult(){
        RewardMe.saveInteger(RewardMe.SHOW,0,MenuReward.this);
        RewardMe.saveInteger(RewardMe.GAGAL,0,MenuReward.this);
        RewardMe.saveInteger(RewardMe.BERHASIL,0,MenuReward.this);
        RewardMe.saveInteger(RewardMe.OPEN,0,MenuReward.this);
        RewardMe.saveInteger(RewardMe.IMPRESSED,0,MenuReward.this);
        RewardMe.saveString(RewardMe.RATE,"0",this);
        RewardMe.saveInteger(RewardMe.JMLREQUEST,0,MenuReward.this);
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
