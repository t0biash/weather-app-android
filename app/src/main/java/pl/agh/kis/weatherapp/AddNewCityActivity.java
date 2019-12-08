package pl.agh.kis.weatherapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.agh.kis.weatherapp.model.City;

public class AddNewCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_city);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button searchButton = (Button)findViewById(R.id.searchButton);
        ListView foundCities = (ListView)findViewById(R.id.foundCities);
        FetchMetaWeatherCitiesTask fetchMetaWeatherCitiesTask = new FetchMetaWeatherCitiesTask(this);

        foundCities.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String)parent.getItemAtPosition(position);
            City city = new City(selectedItem);
            city.save();

            finish();
        });
        searchButton.setOnClickListener(v -> {
            String queryCity = ((EditText)findViewById(R.id.queryCity)).getText().toString();
            fetchMetaWeatherCitiesTask.execute(queryCity);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
