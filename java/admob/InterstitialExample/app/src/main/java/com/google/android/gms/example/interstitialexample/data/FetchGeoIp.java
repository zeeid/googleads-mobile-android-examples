package com.google.android.gms.example.interstitialexample.data;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.example.interstitialexample.Home;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


public class FetchGeoIp extends AsyncTask<Void,Void,Void> {
    private String data="";
    private String dataAll="";
    private String lines = "";
    private String TAG = Home.class.getSimpleName();

    @Override
    protected Void doInBackground(Void... voids) {

        BufferedReader bufferedReader = null;
        try {
            Log.e("CEKIP","MASUK SINI");
            URL url = new URL("http://ip-api.com/json/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();

            while (!Objects.equals(line, lines)) {
                data = line + "\n";
                lines = line;
            }

            JSONObject JO = new JSONObject(data);
            dataAll = "IP : "+JO.getString("query")+"\n"+
                    "Country Code : "+JO.getString("countryCode")+"\n"+
                    "Country Name : "+JO.getString("country")+"\n"+
                    "Region Code : "+JO.getString("region")+"\n"+
                    "Region Name : "+JO.getString("regionName")+"\n"+
                    "City : "+JO.getString("city")+"\n"+
                    "Zip Code : "+JO.getString("zip")+"\n"+
                    "Time Zone : "+JO.getString("timezone")+"\n"+
                    "Latitude : "+JO.getString("lat")+"\n"+
                    "Longitude : "+JO.getString("lon")+"\n"+
                    "ISP : "+JO.getString("isp")+"\n\n\n";


        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Home.data.setText("Loading...");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e("IP1", "onPostExecute: "+data);
        Log.d("IP2", "onPostExecute: "+dataAll);
        Home.data.setText(this.dataAll);
    }
}
