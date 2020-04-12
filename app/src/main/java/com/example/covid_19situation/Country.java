package com.example.covid_19situation;

import java.util.Comparator;

public class Country implements Comparator<Country> {
    public String Country_Region;
    public String Confirmed;
    public String Deaths;
    public String Recovered;

    @Override
    public int compare(Country o1, Country o2) {
        return o1.Country_Region.compareTo(o2.Country_Region);
    }
}
