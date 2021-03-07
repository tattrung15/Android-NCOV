package com.example.covid_19situation;

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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL = "https://apincov.herokuapp.com";

    Moshi moshi = new Moshi.Builder().build();
    List<Country> countries;
    int finalTotalConfirmed;
    int finalTotalDeaths;
    int finalTotalRecovered;

    private Boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        boolean checkOnline = isOnline();
        if (!checkOnline) {
            Toast.makeText(MainActivity.this, "Network Error!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_LONG).show();
            final ListView listView = findViewById(R.id.lvShow);
            final EditText editText = findViewById(R.id.edtSearch);
            final TextView txtConfirmedGlobal = findViewById(R.id.txtConfirmedGlobal);
            final TextView txtDeathsGlobal = findViewById(R.id.txtDeathsGlobal);
            final TextView txtRecoveredGlobal = findViewById(R.id.txtRecoveredGlobal);

            StringRequest getAllCountries = new StringRequest(Request.Method.GET,
                    BASE_URL + "/countries", response -> {
                try {
                    JsonAdapter<Country[]> jsonAdapter = moshi.adapter(Country[].class);

                    countries =Arrays.asList(Objects.requireNonNull(jsonAdapter.fromJson(response)));
                    Log.e("TAG", String.valueOf(countries.size()));
                    countries.sort(new SortByCountry());

                    int totalConfirmed = 0, totalDeaths = 0, totalRecovered = 0;

                    for (int i = 0; i < countries.size(); i++) {
                        totalConfirmed += Integer.parseInt(countries.get(i).Confirmed);
                        totalDeaths += Integer.parseInt(countries.get(i).Deaths);
                        totalRecovered += Integer.parseInt(countries.get(i).Recovered);
                    }

                    finalTotalConfirmed = totalConfirmed;
                    finalTotalDeaths = totalDeaths;
                    finalTotalRecovered = totalRecovered;

                    CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this,
                            R.layout.dong_thong_tin, countries);
                    listView.setAdapter(countryAdapter);
                    txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmed));
                    txtDeathsGlobal.setText(String.valueOf(finalTotalDeaths));
                    txtRecoveredGlobal.setText(String.valueOf(finalTotalRecovered));
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, error -> {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            });

            requestQueue.add(getAllCountries);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(final Editable s) {
                    String str = s.toString();
                    if (str.compareTo("") == 0) {
                        CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this,
                                R.layout.dong_thong_tin, countries);
                        listView.setAdapter(countryAdapter);
                        txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmed));
                        txtDeathsGlobal.setText(String.valueOf(finalTotalDeaths));
                        txtRecoveredGlobal.setText(String.valueOf(finalTotalRecovered));
                        return;
                    }

                    List<Country> temp = new ArrayList<>();
                    for (int i = 0; i < countries.size(); i++) {
                        if (countries.get(i).getCountry_Region().toLowerCase()
                                .contains(str.toLowerCase())) {
                            temp.add(countries.get(i));
                        }
                    }

                    int totalConfirmed = 0, totalDeaths = 0, totalRecovered = 0;

                    for (int i = 0; i < temp.size(); i++) {
                        totalConfirmed += Integer.parseInt(temp.get(i).Confirmed);
                        totalDeaths += Integer.parseInt(temp.get(i).Deaths);
                        totalRecovered += Integer.parseInt(temp.get(i).Recovered);
                    }

                    finalTotalConfirmed = totalConfirmed;
                    finalTotalDeaths = totalDeaths;
                    finalTotalRecovered = totalRecovered;

                    CountryAdapter countryAdapter = new CountryAdapter(MainActivity.this,
                            R.layout.dong_thong_tin, temp);
                    listView.setAdapter(countryAdapter);
                    txtConfirmedGlobal.setText(String.valueOf(finalTotalConfirmed));
                    txtDeathsGlobal.setText(String.valueOf(finalTotalDeaths));
                    txtRecoveredGlobal.setText(String.valueOf(finalTotalRecovered));
                }
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
