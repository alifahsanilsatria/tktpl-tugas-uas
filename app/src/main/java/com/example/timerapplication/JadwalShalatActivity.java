package com.example.timerapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JadwalShalatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_shalat);

        final TextView jadwalShalatTV = findViewById(R.id.jadwalShalat);

        String location = getIntent().getStringExtra("location");
        TextView locationTV = findViewById(R.id.location);
        locationTV.setText("Your location : " + location);

        if (location != null) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String api_key = "0ed95b5b4351397847111c1988b40a79";

            String[] locationPerWord = location.split(" ");
            String url_location = locationPerWord.length > 1 ? joinString(locationPerWord) : locationPerWord[0];
            String url = "https://muslimsalat.com/" + url_location + ".json?key=" + api_key;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);

                                JSONObject jadwalShalatJson = jsonResponse.getJSONArray("items").getJSONObject(0);

                                StringBuilder jadwalShalatBuilder = new StringBuilder();
                                jadwalShalatBuilder.append("Fajr    : " + jadwalShalatJson.get("fajr") + "\n");
                                jadwalShalatBuilder.append("Shurooq :" + jadwalShalatJson.get("shurooq") + "\n");
                                jadwalShalatBuilder.append("Dhuhr   :" + jadwalShalatJson.get("dhuhr") + "\n");
                                jadwalShalatBuilder.append("Ashr    :" + jadwalShalatJson.get("asr") + "\n");
                                jadwalShalatBuilder.append("Maghrib :" + jadwalShalatJson.get("maghrib") + "\n");
                                jadwalShalatBuilder.append("Isha    :" + jadwalShalatJson.get("isha"));

                                jadwalShalatTV.setText(jadwalShalatBuilder.toString());
                            }
                            catch (JSONException e) {
                                //do nothing
                                Log.d("Error Info", e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    jadwalShalatTV.setText("That didn't work!");
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            queue.start();
        }

        else {
            jadwalShalatTV.setText("Jadwal Shalat Tidak Tersedia");
        }
    }

    public String joinString(String[] locationPerWord) {
        StringBuilder urlLocationBuilder = new StringBuilder();
        int urlLocationBuilderLength = locationPerWord.length;
        for(int i = 0; i < urlLocationBuilderLength; i++) {
            urlLocationBuilder.append(locationPerWord[i]);
            if (i < urlLocationBuilderLength - 1) {
                urlLocationBuilder.append("+");
            }
        }
        return urlLocationBuilder.toString();
    }
}
