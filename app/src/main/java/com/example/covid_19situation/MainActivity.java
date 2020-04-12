package com.example.covid_19situation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean checkOnline = isOnline();
        if (!checkOnline){
            Toast.makeText(MainActivity.this, "Network Error!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_LONG).show();
            final ListView listView = findViewById(R.id.lvShow);
            final EditText editText = findViewById(R.id.edtSearch);
            final TextView txtConfirmedGlobal = findViewById(R.id.txtConfirmedGlobal);
            final TextView txtDeathsGlobal = findViewById(R.id.txtDeathsGlobal);
            final TextView txtRecoveredGlobal = findViewById(R.id.txtRecoveredGlobal);

            OkHttpClient client = new OkHttpClient();
            Moshi moshi = new Moshi.Builder().build();
            Type countryType = Types.newParameterizedType(List.class, Country.class);
            final JsonAdapter<List<Country>> jsonAdapter = moshi.adapter(countryType);

            Request request = new Request.Builder().url("https://apincov.herokuapp.com/countries").build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Error", "Network Error");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    final List<Country> countryList = jsonAdapter.fromJson(json);
                    Collections.sort(countryList, new SortByCountry());

                    int totalConfirmed = 0, totalDeaths = 0, totalRecovered = 0;

                    for (int i = 0; i < countryList.size(); i++){
                        totalConfirmed += Integer.parseInt(countryList.get(i).Confirmed);
                        totalDeaths += Integer.parseInt(countryList.get(i).Deaths);
                        totalRecovered += Integer.parseInt(countryList.get(i).Recovered);
                    }

                    final int finalTotalConfirmed = totalConfirmed;
                    final int finalTotalDeaths = totalDeaths;
                    final int finalTotalRecovered = totalRecovered;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this, R.layout.dong_thong_tin, countryList);
                            listView.setAdapter(countryAdapter);
                            txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmed));
                            txtDeathsGlobal.setText(String.valueOf(finalTotalDeaths));
                            txtRecoveredGlobal.setText(String.valueOf(finalTotalRecovered));
                        }
                    });
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(final Editable s) {
                    OkHttpClient client = new OkHttpClient();
                    Moshi moshi = new Moshi.Builder().build();
                    Type countryType = Types.newParameterizedType(List.class, Country.class);
                    final JsonAdapter<List<Country>> jsonAdapter = moshi.adapter(countryType);

                    Request request = new Request.Builder().url("https://apincov.herokuapp.com/countries").build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Error", "Network Error");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            final List<Country> countryList = jsonAdapter.fromJson(json);
                            Collections.sort(countryList, new SortByCountry());
                            final List<Country> countries = new ArrayList<>();

                            int totalConfirmed = 0, totalDeaths = 0, totalRecovered = 0;
                            int totalConfirmedNew = 0, totalDeathsNew = 0, totalRecoveredNew = 0;

                            for (int i = 0; i < countryList.size(); i++) {
                                if (countryList.get(i).Country_Region.toLowerCase().contains(s.toString().toLowerCase())) {
                                    countries.add(countryList.get(i));
                                }
                                totalConfirmed += Integer.parseInt(countryList.get(i).Confirmed);
                                totalDeaths += Integer.parseInt(countryList.get(i).Deaths);
                                totalRecovered += Integer.parseInt(countryList.get(i).Recovered);
                            }

                            for (int i = 0; i < countries.size(); i++){
                                totalConfirmedNew += Integer.parseInt(countries.get(i).Confirmed);
                                totalDeathsNew += Integer.parseInt(countries.get(i).Deaths);
                                totalRecoveredNew += Integer.parseInt(countries.get(i).Recovered);
                            }

                            final int finalTotalConfirmedNew = totalConfirmedNew;
                            final int finalTotalDeathsNew = totalDeathsNew;
                            final int finalTotalRecoveredNew = totalRecoveredNew;
                            final int finalTotalConfirmed = totalConfirmed;
                            final int finalTotalDeaths = totalDeaths;
                            final int finalTotalRecovered = totalRecovered;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (s.toString().length() != 0) {
                                        CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this, R.layout.dong_thong_tin, countries);
                                        listView.setAdapter(countryAdapter);
                                        txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmedNew));
                                        txtDeathsGlobal.setText(String.valueOf(finalTotalDeathsNew));
                                        txtRecoveredGlobal.setText(String.valueOf(finalTotalRecoveredNew));
                                    } else {
                                        CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this, R.layout.dong_thong_tin, countryList);
                                        listView.setAdapter(countryAdapter);
                                        txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmed));
                                        txtDeathsGlobal.setText(String.valueOf(finalTotalDeaths));
                                        txtRecoveredGlobal.setText(String.valueOf(finalTotalRecovered));
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean checkOnline = isOnline();
        if (!checkOnline){
            Toast.makeText(MainActivity.this, "Network Error!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_LONG).show();
            final ListView listView = findViewById(R.id.lvShow);
            final EditText editText = findViewById(R.id.edtSearch);
            final TextView txtConfirmedGlobal = findViewById(R.id.txtConfirmedGlobal);
            final TextView txtDeathsGlobal = findViewById(R.id.txtDeathsGlobal);
            final TextView txtRecoveredGlobal = findViewById(R.id.txtRecoveredGlobal);

            editText.setText("");

            OkHttpClient client = new OkHttpClient();
            Moshi moshi = new Moshi.Builder().build();
            Type countryType = Types.newParameterizedType(List.class, Country.class);
            final JsonAdapter<List<Country>> jsonAdapter = moshi.adapter(countryType);

            Request request = new Request.Builder().url("https://apincov.herokuapp.com/countries").build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Error", "Network Error");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    final List<Country> countryList = jsonAdapter.fromJson(json);
                    Collections.sort(countryList, new SortByCountry());

                    int totalConfirmed = 0, totalDeaths = 0, totalRecovered = 0;

                    for (int i = 0; i < countryList.size(); i++){
                        totalConfirmed += Integer.parseInt(countryList.get(i).Confirmed);
                        totalDeaths += Integer.parseInt(countryList.get(i).Deaths);
                        totalRecovered += Integer.parseInt(countryList.get(i).Recovered);
                    }

                    final int finalTotalConfirmed = totalConfirmed;
                    final int finalTotalDeaths = totalDeaths;
                    final int finalTotalRecovered = totalRecovered;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this, R.layout.dong_thong_tin, countryList);
                            listView.setAdapter(countryAdapter);
                            txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmed));
                            txtDeathsGlobal.setText(String.valueOf(finalTotalDeaths));
                            txtRecoveredGlobal.setText(String.valueOf(finalTotalRecovered));
                        }
                    });
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(final Editable s) {
                    OkHttpClient client = new OkHttpClient();
                    Moshi moshi = new Moshi.Builder().build();
                    Type countryType = Types.newParameterizedType(List.class, Country.class);
                    final JsonAdapter<List<Country>> jsonAdapter = moshi.adapter(countryType);

                    Request request = new Request.Builder().url("https://apincov.herokuapp.com/countries").build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Error", "Network Error");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            final List<Country> countryList = jsonAdapter.fromJson(json);
                            Collections.sort(countryList, new SortByCountry());
                            final List<Country> countries = new ArrayList<>();

                            int totalConfirmed = 0, totalDeaths = 0, totalRecovered = 0;
                            int totalConfirmedNew = 0, totalDeathsNew = 0, totalRecoveredNew = 0;

                            for (int i = 0; i < countryList.size(); i++) {
                                if (countryList.get(i).Country_Region.toLowerCase().contains(s.toString().toLowerCase())) {
                                    countries.add(countryList.get(i));
                                }
                                totalConfirmed += Integer.parseInt(countryList.get(i).Confirmed);
                                totalDeaths += Integer.parseInt(countryList.get(i).Deaths);
                                totalRecovered += Integer.parseInt(countryList.get(i).Recovered);
                            }

                            for (int i = 0; i < countries.size(); i++){
                                totalConfirmedNew += Integer.parseInt(countries.get(i).Confirmed);
                                totalDeathsNew += Integer.parseInt(countries.get(i).Deaths);
                                totalRecoveredNew += Integer.parseInt(countries.get(i).Recovered);
                            }

                            final int finalTotalConfirmedNew = totalConfirmedNew;
                            final int finalTotalDeathsNew = totalDeathsNew;
                            final int finalTotalRecoveredNew = totalRecoveredNew;
                            final int finalTotalConfirmed = totalConfirmed;
                            final int finalTotalDeaths = totalDeaths;
                            final int finalTotalRecovered = totalRecovered;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (s.toString().length() != 0) {
                                        CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this, R.layout.dong_thong_tin, countries);
                                        listView.setAdapter(countryAdapter);
                                        txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmedNew));
                                        txtDeathsGlobal.setText(String.valueOf(finalTotalDeathsNew));
                                        txtRecoveredGlobal.setText(String.valueOf(finalTotalRecoveredNew));
                                    } else {
                                        CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this, R.layout.dong_thong_tin, countryList);
                                        listView.setAdapter(countryAdapter);
                                        txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmed));
                                        txtDeathsGlobal.setText(String.valueOf(finalTotalDeaths));
                                        txtRecoveredGlobal.setText(String.valueOf(finalTotalRecovered));
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
