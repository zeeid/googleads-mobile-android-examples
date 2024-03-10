package com.zeeidev.android.apps.finance.managements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import com.zeeidev.android.apps.finance.managements.data.FetchGeoIp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity {
    public static TextView data;
    private static final String TAG = "Home Activity";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;


    private static void setSystemTimeZoneByIP(final Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Mengambil informasi zona waktu dari IP
                    String timeZoneId = getTimeZoneFromIP();

                    // Mengatur zona waktu sistem jika informasi berhasil didapatkan
                    if (timeZoneId != null && !timeZoneId.isEmpty()) {
                        if (setSystemTimeZone(context, timeZoneId)) {
                            // Menampilkan Toast jika zona waktu sistem berhasil diatur
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Zona waktu sistem diatur ke: " + timeZoneId, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Menampilkan Toast jika terjadi kesalahan
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Gagal mengatur zona waktu sistem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    // Metode untuk mendapatkan zona waktu dari IP
    private static String getTimeZoneFromIP() {
        String timeZoneId = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Membuat URL untuk mengakses informasi zona waktu berdasarkan IP
            URL url = new URL("http://ip-api.com/json/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Membaca respons JSON
            StringBuilder response = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Parsing JSON untuk mendapatkan informasi zona waktu
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.getString("status").equals("success")) {
                timeZoneId = jsonResponse.getString("timezone");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            // Menutup koneksi dan pembaca
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return timeZoneId;
    }

    // Metode untuk mengatur zona waktu sistem
    private static boolean setSystemTimeZone(Context context, String timeZoneId) {
        try {
            TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));
            Log.d("TimeZoneManager", "Zona waktu sistem diatur ke: " + timeZoneId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
                        // Load an ad.
                        // loadAd();
                    }
                });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        googleMobileAdsConsentManager =
                GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(
                this,
                consentError -> {
                    if (consentError != null) {
                        // Consent not obtained in current session.
                        Log.w(
                                TAG,
                                String.format(
                                        "%s: %s",
                                        consentError.getErrorCode(),
                                        consentError.getMessage()));
                    }

                    // startGame();

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

        viewLayout();
        cekIp();
        setSystemTimeZoneByIP(Home.this);
//        checkPermissions();

        Button buttonInata = findViewById(R.id.buttoninata);
        buttonInata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MenuInata.class);
                startActivity(intent);
            }
        });

        Button buttonRewad = findViewById(R.id.buttonreward);
        buttonRewad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MenuReward.class);
                startActivity(intent);
            }
        });

        Button buttonBanana = findViewById(R.id.buttonBanana);
        buttonBanana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MenuBanana.class);
                startActivity(intent);
            }
        });

        Button buttonRefresh = findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekIp();
            }
        });

        Button buttonSetting = findViewById(R.id.button3);
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MenuSetting.class);
                startActivity(intent);
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kode yang akan dijalankan ketika FAB diklik

                // Tambahkan kode lain sesuai dengan tindakan yang ingin Anda lakukan ketika FAB diklik

                setSystemTimeZoneByIP(Home.this);
            }
        });

    }

    private void cekIp() {
        FetchGeoIp process = new FetchGeoIp();
        process.execute();
    }
    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();
        // resumeGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        // pauseGame();
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
                        // pauseGame();
                        // Handle changes to user consent.
                        googleMobileAdsConsentManager.showPrivacyOptionsForm(
                                this,
                                formError -> {
                                    if (formError != null) {
                                        Toast.makeText(
                                                this,
                                                formError.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    // resumeGame();
                                });
                        return true;
                    }
                    return false;
                });
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void viewLayout(){
        TextView packageName= findViewById(R.id.textView);
        data = findViewById(R.id.textView6);
        packageName.setText("this package :"+getPackageName());
    }



}
