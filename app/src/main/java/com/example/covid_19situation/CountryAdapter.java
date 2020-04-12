package com.example.covid_19situation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CountryAdapter extends BaseAdapter {

    Context context;
    int layout;
    List<Country> list;

    public CountryAdapter(Context context, int layout, List<Country> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);

        TextView txtCountry = convertView.findViewById(R.id.txtCountry);
        TextView txtConfirmed = convertView.findViewById(R.id.txtConfirmed);
        TextView txtDeaths = convertView.findViewById(R.id.txtDeaths);
        TextView txtRecovered = convertView.findViewById(R.id.txtRecovered);

        txtCountry.setText(list.get(position).Country_Region);
        txtConfirmed.setText(list.get(position).Confirmed);
        txtDeaths.setText(list.get(position).Deaths);
        txtRecovered.setText(list.get(position).Recovered);

        return convertView;
    }
}
