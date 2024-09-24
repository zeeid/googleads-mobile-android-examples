package com.mubaraq.managementsistem.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthManager {

    private static final String LOGIN_URL = "https://zeeid.net/api/mobile/login";

    public static void performLogin(final Context context, final String email, final String password) {
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
                        String name = jsonResponse.optString("name");
                        String token = jsonResponse.optString("token");

                        // Simpan token ke SharedPreferences
                        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_token", token);
                        editor.putString("user_name", name);
                        editor.apply();

                        // Tampilkan toast di UI thread
                        showToast(context, "Login berhasil: " + name);

                    } else {
                        showToast(context, "Login gagal, response code: " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(context, "Error: " + e.getMessage());
                }
            }
        });
    }

    private static void showToast(final Context context, final String message) {
        // Untuk menampilkan Toast dari background thread
        new android.os.Handler(android.os.Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

}
