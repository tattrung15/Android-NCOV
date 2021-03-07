package com.example.covid_19situation;

import java.util.Comparator;

public class Country implements Comparator<Country> {
    public String Country_Region;
    public String Confirmed;
    public String Deaths;
    public String Recovered;

    public Country() {
    }

    public Country(String country_Region, String confirmed, String deaths, String recovered) {
        Country_Region = country_Region;
        Confirmed = confirmed;
        Deaths = deaths;
        Recovered = recovered;
    }

    public String getCountry_Region() {
        return Country_Region;
    }

    public void setCountry_Region(String country_Region) {
        Country_Region = country_Region;
    }

    public String getConfirmed() {
        return Confirmed;
    }

    public void setConfirmed(String confirmed) {
        Confirmed = confirmed;
    }

    public String getDeaths() {
        return Deaths;
    }

    public void setDeaths(String deaths) {
        Deaths = deaths;
    }

    public String getRecovered() {
        return Recovered;
    }

    public void setRecovered(String recovered) {
        Recovered = recovered;
    }

    @Override
    public int compare(Country o1, Country o2) {
        return o1.Country_Region.compareTo(o2.Country_Region);
    }

    @Override
    public String toString() {
        return "Country{" +
                "Country_Region='" + Country_Region + '\'' +
                ", Confirmed='" + Confirmed + '\'' +
                ", Deaths='" + Deaths + '\'' +
                ", Recovered='" + Recovered + '\'' +
                '}';
    }
}
