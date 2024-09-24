package com.mubaraq.managementsistem.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AuthManager {

    private static final String LOGIN_URL = "https://zeeid.net/api/mobile/login";
    private static final String PREFS_NAME = "DataLogin";

    // Interface untuk callback hasil login
    public interface LoginCallback {
        void onLoginResult(boolean success);
    }

    public static void performLogin(final Context context, final String email, final String password, final LoginCallback callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(LOGIN_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    // Buat JSON body untuk request login
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", email);
                    jsonBody.put("password", password);

                    // Kirim body request
                    BufferedOutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                    outputStream.write(jsonBody.toString().getBytes());
                    outputStream.flush();

                    // Dapatkan response code dari server
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Baca response
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // Parsing JSON response
                        JSONObject jsonResponse = new JSONObject(response.toString());

                        Log.d("RESPNSE", String.valueOf(jsonResponse));

                        // Ambil data dari response
                        String name = jsonResponse.getString("name");
                        String userEmail = jsonResponse.getString("email");
                        int jml_baner = jsonResponse.getInt("jml_baner");
                        int ReLoadBaner = jsonResponse.getInt("ReLoadBaner");
                        int TimerBaner = jsonResponse.getInt("TimerBaner");
                        int ReLoadInata = jsonResponse.getInt("ReLoadInata");
                        int jml_inata = jsonResponse.getInt("jml_inata");
                        int TimerInata = jsonResponse.getInt("TimerInata");
                        int isClearCache = jsonResponse.getInt("isClearCache");
                        int isVPNProtection = jsonResponse.getInt("isVPNProtection");
                        int isTestAds = jsonResponse.getInt("isTestAds");
                        int isRotation = jsonResponse.getInt("isRotation");
                        int isMixadstype = jsonResponse.getInt("isMixadstype");
                        int isIndoprot = jsonResponse.getInt("isIndoprot");
                        int isKeepgoing = jsonResponse.getInt("isKeepgoing");
                        int isAcakSponsor = jsonResponse.getInt("isAcakSponsor");
                        int maxsuccess = jsonResponse.getInt("maxsuccess");
                        int maxfail = jsonResponse.getInt("maxfail");

                        

                        // Simpan data ke SharedPreferences
                        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("name", name);
                        editor.putString("email", userEmail);
                        editor.putString("password", password);
                        editor.putInt("jml_baner", jml_baner);
                        editor.putInt("ReLoadBaner", ReLoadBaner);
                        editor.putInt("TimerBaner", TimerBaner);
                        editor.putInt("ReLoadInata", ReLoadInata);
                        editor.putInt("jml_inata", jml_inata);
                        editor.putInt("TimerInata", TimerInata);
                        editor.putInt("isClearCache", isClearCache);
                        editor.putInt("isVPNProtection", isVPNProtection);
                        editor.putInt("isTestAds", isTestAds);
                        editor.putInt("isRotation", isRotation);
                        editor.putInt("isMixadstype", isMixadstype);
                        editor.putInt("isIndoprot", isIndoprot);
                        editor.putInt("isKeepgoing", isKeepgoing);
                        editor.putInt("isAcakSponsor", isAcakSponsor);
                        editor.putInt("maxsuccess", maxsuccess);
                        editor.putInt("maxfail", maxfail);



                        // Simpan arrays
                        saveJSONArrayToPreferences(editor, "Iklan_Layar_Pembuka_Aplikasi", jsonResponse.optJSONArray("Iklan_Layar_Pembuka_Aplikasi"));
                        saveJSONArrayToPreferences(editor, "Iklan_Banner_Adaptif", jsonResponse.optJSONArray("Iklan_Banner_Adaptif"));
                        saveJSONArrayToPreferences(editor, "Iklan_Banner_Ukuran_Tetap", jsonResponse.optJSONArray("Iklan_Banner_Ukuran_Tetap"));
                        saveJSONArrayToPreferences(editor, "Iklan_Interstisial", jsonResponse.optJSONArray("Iklan_Interstisial"));
                        saveJSONArrayToPreferences(editor, "Iklan_Iklan_Reward", jsonResponse.optJSONArray("Iklan_Iklan_Reward"));
                        saveJSONArrayToPreferences(editor, "Iklan_Interstisial_Reward", jsonResponse.optJSONArray("Iklan_Interstisial_Reward"));
                        saveJSONArrayToPreferences(editor, "Iklan_Native", jsonResponse.optJSONArray("Iklan_Native"));
                        saveJSONArrayToPreferences(editor, "Iklan_Video_Native", jsonResponse.optJSONArray("Iklan_Video_Native"));

                        editor.apply(); // Simpan perubahan


                        JSONArray layarPembukaAplikasi = jsonResponse.optJSONArray("Iklan_Layar_Pembuka_Aplikasi");
                        if (layarPembukaAplikasi != null) {
                            for (int i = 0; i < layarPembukaAplikasi.length(); i++) {
                                String adCode = layarPembukaAplikasi.getString(i);

                            }
                        }

                        JSONArray bannerAdaptif = jsonResponse.optJSONArray("Iklan_Banner_Adaptif");
                        if (bannerAdaptif != null) {
                            for (int i = 0; i < bannerAdaptif.length(); i++) {
                                String adCode = bannerAdaptif.getString(i);
                                Log.d("Iklan_Banner_Adaptif", "Ad Code " + (i + 1) + ": " + adCode);
                            }
                        }

                        JSONArray bannerUkuranTetap = jsonResponse.optJSONArray("Iklan_Banner_Ukuran_Tetap");
                        if (bannerUkuranTetap != null) {
                            for (int i = 0; i < bannerUkuranTetap.length(); i++) {
                                String adCode = bannerUkuranTetap.getString(i);
                                Log.d("Iklan_Banner_Ukuran_Tetap", "Ad Code " + (i + 1) + ": " + adCode);
                            }
                        }

                        JSONArray interstisial = jsonResponse.optJSONArray("Iklan_Interstisial");
                        if (interstisial != null) {
                            for (int i = 0; i < interstisial.length(); i++) {
                                String adCode = interstisial.getString(i);
                                Log.d("Iklan_Interstisial", "Ad Code " + (i + 1) + ": " + adCode);
                            }
                        }

                        JSONArray iklanReward = jsonResponse.optJSONArray("Iklan_Iklan_Reward");
                        if (iklanReward != null) {
                            for (int i = 0; i < iklanReward.length(); i++) {
                                String adCode = iklanReward.getString(i);
                                Log.d("Iklan_Iklan_Reward", "Ad Code " + (i + 1) + ": " + adCode);
                            }
                        }

                        JSONArray interstisialReward = jsonResponse.optJSONArray("Iklan_Interstisial_Reward");
                        if (interstisialReward != null) {
                            for (int i = 0; i < interstisialReward.length(); i++) {
                                String adCode = interstisialReward.getString(i);
                                Log.d("Iklan_Interstisial_Reward", "Ad Code " + (i + 1) + ": " + adCode);
                            }
                        }

                        JSONArray nativeAds = jsonResponse.optJSONArray("Iklan_Native");
                        if (nativeAds != null) {
                            for (int i = 0; i < nativeAds.length(); i++) {
                                String adCode = nativeAds.getString(i);
                                Log.d("Iklan_Native", "Ad Code " + (i + 1) + ": " + adCode);
                            }
                        }

                        JSONArray videoNative = jsonResponse.optJSONArray("Iklan_Video_Native");
                        if (videoNative != null) {
                            for (int i = 0; i < videoNative.length(); i++) {
                                String adCode = videoNative.getString(i);
                                Log.d("Iklan_Video_Native", "Ad Code " + (i + 1) + ": " + adCode);
                            }
                        }

                        // Jika login berhasil, panggil callback dengan true
                        callback.onLoginResult(true);
                    } else {
                        // Jika login gagal, panggil callback dengan false
                        callback.onLoginResult(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // Jika terjadi error, panggil callback dengan false
                    callback.onLoginResult(false);
                }
            }
        });
    }

    private static void saveJSONArrayToPreferences(SharedPreferences.Editor editor, String key, JSONArray jsonArray) {
        if (jsonArray != null) {
            Set<String> set = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    set.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            editor.putStringSet(key, set);
        } else {
            editor.remove(key); // Remove key if JSONArray is null
        }
    }

}
