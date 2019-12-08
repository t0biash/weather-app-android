package pl.agh.kis.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import pl.agh.kis.weatherapp.model.City;

public class BrowseCitiesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_cities);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ArrayList<String> savedCities = new ArrayList<>();
        for(City city : City.listAll(City.class))
            savedCities.add(city.name);
        CustomAdapter adapter = new CustomAdapter(savedCities, this);
        ((ListView)findViewById(R.id.savedCities)).setAdapter(adapter);

        Button getCurrentLocationWeather = (Button)findViewById(R.id.getCurrentLocationWeatherBtn);
        getCurrentLocationWeather.setOnClickListener(v -> {
            onSelectCity("current");
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSelectCity(String selectedCity) {
        Intent data = new Intent();
        data.putExtra("selectedCity", selectedCity);
        setResult(RESULT_OK, data);
        finish();
    }
}
