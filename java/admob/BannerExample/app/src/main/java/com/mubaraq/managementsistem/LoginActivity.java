package com.mubaraq.managementsistem;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mubaraq.managementsistem.login.AuthManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL = "https://zeeid.net/api/mobile/login";
    private static final String PREFS_NAME = "DataLogin";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Mengecek apakah sudah ada data di SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String storedName = sharedPref.getString("name", null);
        String storedEmail = sharedPref.getString("email", null);
        String storedPassword = sharedPref.getString("password", null);

        // Jika name dan email sudah ada di SharedPreferences, arahkan ke InataRoomActivity
        if (storedName != null && storedEmail != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    AuthManager.performLogin(LoginActivity.this, email, password, new AuthManager.LoginCallback() {
                        @Override
                        public void onLoginResult(boolean success) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (success) {
                                        Toast.makeText(LoginActivity.this, "Login Successful: ", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Login Failed: Invalid credentials", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void saveJSONArrayToPreferences(SharedPreferences.Editor editor, String key, JSONArray jsonArray) {
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