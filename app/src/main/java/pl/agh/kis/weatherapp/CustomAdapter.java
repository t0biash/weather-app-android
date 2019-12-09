package pl.agh.kis.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.agh.kis.weatherapp.model.City;

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> _list;
    private Context _context;
    private BrowseCitiesActivity _browseCitiesActivity;


    public CustomAdapter(ArrayList<String> list, Context context) {
        _list = list;
        _context = context;
        _browseCitiesActivity = (BrowseCitiesActivity)context;
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Object getItem(int pos) {
        return _list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.browse_cities_row, null);
        }

        TextView savedCity = (TextView)view.findViewById(R.id.savedCity);
        savedCity.setText(_list.get(position));

        Button deleteBtn = (Button)view.findViewById(R.id.deleteCityBtn);
        Button selectBtn = (Button)view.findViewById(R.id.selectCityBtn);

        deleteBtn.setOnClickListener(v -> {
            City cityToDelete = (City)City.find(City.class, "name = ?", (String) savedCity.getText()).get(0);
            cityToDelete.delete();
            notifyDataSetChanged();
        });
        selectBtn.setOnClickListener(v -> {
            _browseCitiesActivity.onSelectCity(savedCity.getText().toString());
        });

        return view;
    }
}
