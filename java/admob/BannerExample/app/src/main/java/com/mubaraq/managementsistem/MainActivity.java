package com.mubaraq.managementsistem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mubaraq.managementsistem.data.FetchGeoIp;
import com.mubaraq.managementsistem.login.AuthManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    public static TextView data;
    private static final String TAG = "Home Activity";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private static final String PREFS_NAME = "DataLogin";

    private class GetAdvertisingIdTask extends AsyncTask<Void, Void, String> {

        private Context context;

        public GetAdvertisingIdTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            } catch (GooglePlayServicesNotAvailableException |
                     GooglePlayServicesRepairableException | IOException e) {
                // Error handling omitted.
            }
            return adInfo == null ? null : adInfo.getId();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String advertisingId) {
            super.onPostExecute(advertisingId);

            // Gunakan advertisingId untuk pelacakan.
            Log.d("Home", "Advertising ID: " + advertisingId);
            TextView AdvertisingId= findViewById(R.id.AdvertisingId);
            AdvertisingId.setText("Advertising ID: "+advertisingId);
        }
    }

    // ================ Metode untuk mengatur zona waktu sistem ==========
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
    // ================= END TIME ZONE ====================================

    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("SetTextI18n")
    public void viewLayout(){
        TextView packageName= findViewById(R.id.textView);
        data = findViewById(R.id.textView6);
        packageName.setText("this package :"+getPackageName());
    }

    private void cekIp() {
        FetchGeoIp process = new FetchGeoIp();
        process.execute();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new GetAdvertisingIdTask(this).execute();

        viewLayout();
        cekIp();
        setSystemTimeZoneByIP(MainActivity.this);
//        checkPermissions();

        Button buttonInata = findViewById(R.id.buttoninata);
        buttonInata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InataRoomActivity.class);
                startActivity(intent);
            }
        });

        Button buttonRewad = findViewById(R.id.buttonreward);
        buttonRewad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MenuReward.class);
//                startActivity(intent);
            }
        });

        Button buttonInataRewad = findViewById(R.id.buttoninatareward);
        buttonInataRewad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MenuInataReward.class);
//                startActivity(intent);
            }
        });

        Button buttonBanana = findViewById(R.id.buttonBanana);
        buttonBanana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MenuBananaAdaptive.class);
//                startActivity(intent);
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
//                Intent intent = new Intent(MainActivity.this, MenuSetting.class);
//                startActivity(intent);
            }
        });

        // Tombol untuk sinkronisasi akun
        Button buttonSyncAccount = findViewById(R.id.buttonSyncAccount);
        buttonSyncAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengecek apakah sudah ada data di SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String storedName = sharedPref.getString("name", null);
                String storedEmail = sharedPref.getString("email", null);
                String storedPassword = sharedPref.getString("password", null);

                // Panggil fungsi login di AuthManager dengan callback untuk hasil login
                AuthManager.performLogin(MainActivity.this, storedEmail, storedPassword, new AuthManager.LoginCallback() {
                    @Override
                    public void onLoginResult(boolean success) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    // Login berhasil
                                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Login gagal
                                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kode yang akan dijalankan ketika FAB diklik

                // Tambahkan kode lain sesuai dengan tindakan yang ingin Anda lakukan ketika FAB diklik

                setSystemTimeZoneByIP(MainActivity.this);
            }
        });

    }


}
