package com.google.android.gms.example.interstitialexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.gms.example.interstitialexample.data.VARIABELS;

/** Main Activity. Inflates main activity xml and child fragments. */
public class MenuBanana extends AppCompatActivity {

    // These are ad unit IDs for test ads. Replace with your own banner ad unit IDs.
    private static final String AD_UNIT_ID_1 = "ca-app-pub-3940256099942544/9214589741";
    private static final String AD_UNIT_ID_2 = "ca-app-pub-3940256099942544/9214589741";
    private static final String TAG = "Menu Banana";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private AdView adView1;
    private AdView adView2;
    private FrameLayout adContainerView1;
    private FrameLayout adContainerView2;
    private AtomicBoolean initialLayoutComplete = new AtomicBoolean(false);

    private int sizebans;
    private int banyak;
    TextView berhasil,gagal,auto,jumato,jumbanner,categori,size,tanggalan,adopen,rate,impression,opened;

    private static final String DATE="yyasd",GG="gsdag",BB="beqrewb",CIK="casdfsc",CT="crewt",IMP="imasfdpr",
            IMPRESSION="impression",
            OPENED ="opened";

    public int gagalt=0,berhasilt=0,impre=0,cik=0,openedt=0,impressiont=0;
    public String ratess,AdsUnitID;

    public boolean sedang=false,asd,IsIndo;
    public boolean rotation,vpnprot,indoprot,keepgoing,mixbanerinter,usetestunit;
    public int maxsuccess = 1, maxfail = 1;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banana);

        rotation        = VARIABELS.getBool(MenuSetting.ROTATION,this);
        mixbanerinter   = VARIABELS.getBool(MenuSetting.MIXBANERINTER,this);
        usetestunit     = VARIABELS.getBool(MenuSetting.USETESTUNIT,this);
        vpnprot         = VARIABELS.getBool(MenuSetting.VPNPROT,this);
        indoprot        = VARIABELS.getBool(MenuSetting.INDOPROT,this);
        keepgoing       = VARIABELS.getBool(MenuSetting.KEEPGOING,this);

        maxsuccess      = VARIABELS.getInteger(MenuSetting.MAXLOAD,this,1);
        maxfail         = VARIABELS.getInteger(MenuSetting.MINLOAD,this,1);

        CekDateUP();
        viewBinds();
        data();


        adContainerView1 = findViewById(R.id.ad_view_container1);
        adContainerView2 = findViewById(R.id.ad_view_container2);

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

        // Since we're loading the banners based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        adContainerView1
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> {
                            if (!initialLayoutComplete.getAndSet(true)
                                    && googleMobileAdsConsentManager.canRequestAds()) {
                                loadBanners();
                            }
                        });

        Button buttonreset = findViewById(R.id.set_reset);
        buttonreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetResult();
            }
        });

        Button buttonsetting = findViewById(R.id.set_banner);
        buttonsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuBanana.this, MenuSetting.class);
                startActivity(intent);
            }
        });

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());
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
                        googleMobileAdsConsentManager.showPrivacyOptionsForm(
                                this,
                                formError -> {
                                    if (formError != null) {
                                        Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return true;
                    }
                    return false;
                });
        return super.onOptionsItemSelected(item);
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView1 != null) {
            adView1.pause();
        }
        if (adView2 != null) {
            adView2.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView1 != null) {
            adView1.resume();
        }
        if (adView2 != null) {
            adView2.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView1 != null) {
            adView1.destroy();
        }
        if (adView2 != null) {
            adView2.destroy();
        }
        super.onDestroy();
    }

    public void onBackPressed() {

        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (adView1 != null) {
            adView1.destroy();
        }
        if (adView2 != null) {
            adView2.destroy();
        }

        if (isTaskRoot()) {
            // Jika aktivitas ini adalah aktivitas teratas (tidak ada aktivitas lain dalam tumpukan)
            // tambahkan logika untuk membuka menu Home activity atau lakukan tindakan yang sesuai.
            // Misalnya:
            Intent intent = new Intent(this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Selesai dengan aktivitas ini
        } else {
            finish();
            super.onBackPressed(); // Panggil perilaku default jika tidak ada dalam tumpukan teratas
        }
    }

    public void AutoReload(){
        if (countDownTimer == null) countDownTimer =  new CountDownTimer(VARIABELS.getInteger(MenuSetting.DURATIONRELOADBANNER,this,60)*1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                sedang=true;
                if (VARIABELS.getBool(MenuSetting.AUTORELOADBANNER,MenuBanana.this)){
                    jumato.setText("in :"+((millisUntilFinished/1000)-1)+" second");
                }

            }

            public void onFinish() {

                if (VARIABELS.getBool(MenuSetting.AUTORELOADBANNER,MenuBanana.this)){
                    if (adView1 != null) {
                        adView1.destroy();
                    }
                    if (adView2 != null) {
                        adView2.destroy();
                    }

                    // Menutup aktivitas saat ini
                    finish();
                    Intent intent;
                    if (mixbanerinter){
                        // Membuka BananaFixedActivity
                        intent = new Intent(MenuBanana.this, MenuInata.class);
                    }else{
                        intent = new Intent(MenuBanana.this, MenuBanana.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        }.start();
    }

    private void setAdListeners() {
        if (VARIABELS.getBool(MenuSetting.AUTORELOADBANNER,this)){
            AutoReload();
        }
        AdListener adListener = new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                cik++;
                saveInteger(CIK,cik,MenuBanana.this);
                adopen.setText("CLICK :"+cik);
                cekRate();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                gagalt++;
                saveInteger(GG,gagalt,MenuBanana.this);
                gagal.setText("FAILED :"+gagalt);
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                impre++;
                saveInteger(IMP,impre,MenuBanana.this);

                impressiont++;
                saveInteger(IMPRESSION,impressiont,MenuBanana.this);
                impression.setText("IMPRES :"+impressiont);
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                berhasilt++;
                saveInteger(BB,berhasilt,MenuBanana.this);
                berhasil.setText("LOAD :"+berhasilt);
                cekRate();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                openedt++;
                saveInteger(OPENED,openedt,MenuBanana.this);
                opened.setText("OPENED :"+openedt);
                cekRate();
            }
        };

        adView1.setAdListener(adListener);
        adView2.setAdListener(adListener);
    }

    private void loadBanners() {
        // Create new ad views.
        adView1 = new AdView(this);
        adView1.setAdUnitId(AD_UNIT_ID_1);
        adView1.setAdSize(getAdSize(adContainerView1));

        adView2 = new AdView(this);
        adView2.setAdUnitId(AD_UNIT_ID_2);
        adView2.setAdSize(getAdSize(adContainerView2));

        // Replace ad containers with new ad views.
        adContainerView1.removeAllViews();
        adContainerView1.addView(adView1);

        adContainerView2.removeAllViews();
        adContainerView2.addView(adView2);

        // Set AdListeners for both AdViews
        setAdListeners();

        // Start loading the ads in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);
        adView2.loadAd(adRequest);
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                        //Log.e("IKLAN","initializeMobileAdsSdk KOMPLITE");
                    }
                });

        // Load the banners.
        if (initialLayoutComplete.get()) {
            loadBanners();
        }
    }

    private AdSize getAdSize(FrameLayout adContainerView) {
        // Determine the screen width (less decorations) to use for the ad width.
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @SuppressLint("SetTextI18n")
    public void data(){

        tanggalan.setText("Estimates calculation in :\n"+getString(DATE,this));
        berhasilt = getInteger(BB, this);
        gagalt=getInteger(GG,this);
        impressiont=getInteger(IMPRESSION,this);
        openedt=getInteger(OPENED,this);
        impre=getInteger(IMP,this);
        cik=getInteger(CIK,this);
        ratess = getString(CT, this);

        cekRate();

        berhasil.setText("LOAD :"+berhasilt);
        gagal.setText("FAILED :"+gagalt);
        impression.setText("IMPRES :"+impressiont);
        opened.setText("OPENED :"+openedt);
        adopen.setText("CLICK :"+cik);
        banyak=VARIABELS.getInteger(MenuSetting.TOTALBANNER,this,1);
        jumbanner.setText("Total ad per imprs :"+banyak);
        if(VARIABELS.getBool(MenuSetting.AUTORELOADBANNER,this)){
            auto.setText("AUTO RELOAD ACTIVE");
            jumato.setText("in :"+VARIABELS.getInteger(MenuSetting.DURATIONRELOADBANNER,this,60)+" second");
        }else{
            auto.setText("AUTO RELOAD DEACTIVE");
            jumato.setText("NULL");
        }
        categori.setText("keyword ad : "+VARIABELS.getString(MenuSetting.CATEGORYAD,this,getString(R.string.app_name)));
        //size.setText(BannerSetting.getStringBan(BannerSetting.SIZEBANNER,this));
        sizebans=VARIABELS.getInteger(MenuSetting.SIZEBANNER,this,6);
    }

    @SuppressLint("SetTextI18n")
    public void resetResult(){
        saveInteger(GG,0,this);
        saveInteger(CIK,0,this);
        saveInteger(BB,0,this);
        saveInteger(IMP,0,this);
        saveString(CT,"0",this);
        saveInteger(IMPRESSION,0,this);
        saveInteger(OPENED,0,this);

//        cekRate();
//        adopen.setText("CLICK : 0");
//        gagal.setText("FAILED : 0");
//        impression.setText("IMPRES : 0");
//        berhasil.setText("LOAD : 0");
//        opened.setText("OPENED : 0");
//        rate.setText("CTR : 0%");

    }

    public void viewBinds(){
        berhasil=findViewById(R.id.succsestot);
        gagal=findViewById(R.id.failtot);
        impression = findViewById(R.id.impression);
        opened = findViewById(R.id.opened);
        auto=findViewById(R.id.autoReloads);
        adopen=findViewById(R.id.adopenBan);
        jumato=findViewById(R.id.timereload);
        jumbanner=findViewById(R.id.jumlahbanner);
        categori=findViewById(R.id.categoryban);
        size=findViewById(R.id.sizebanner);
        rate=findViewById(R.id.rateSBan);
        tanggalan=findViewById(R.id.tanggal);
    }

    public void CekDateUP(){
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        if(!date.equals(getString(DATE, this))){
            resetResult();
            saveString(DATE,date,this);
        }else{
            saveString(DATE,date,this);
        }
    }

    @SuppressLint("SetTextI18n")
    public void cekRate(){

        float total = ((float)cik/(float)berhasilt)*100;
        DecimalFormat df = new DecimalFormat("####.##");
        ratess = df.format(total);
        saveString(CT,ratess,MenuBanana.this);
        rate.setText("CTR :"+ratess+"%");
    }
    @SuppressLint("ApplySharedPref")
    public void saveString(String key, String value, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "empty");
    }

    @SuppressLint("ApplySharedPref")
    public void saveInteger(String key, Integer value, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public static int getInteger(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }
}
