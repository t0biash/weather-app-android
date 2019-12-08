package pl.agh.kis.weatherapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Dictionary;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final long UPDATE_INTERVAL = 3000;
    private final long FASTEST_INTERVAL = 2000;
    private final int SELECT_CITY_REQUEST_CODE = 100;

    private FusedLocationProviderClient _locationClient;
    private LocationRequest locationRequest;

    public String consolidatedWeatherAsString;
    public Dictionary<String, Bitmap> weatherStatesImages;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();

        if (menuItemId == R.id.actionNewCity) {
            Intent addNewCityActivity = new Intent(this, AddNewCityActivity.class);
            this.startActivity(addNewCityActivity);
        }
        else if (menuItemId == R.id.actionBrowseCities) {
            Intent browseCitiesActivity = new Intent(this, BrowseCitiesActivity.class);
            this.startActivityForResult(browseCitiesActivity, SELECT_CITY_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocationWeather();
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            _locationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            } else {
                getCurrentLocationWeather();
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SELECT_CITY_REQUEST_CODE)  {
            if (data.hasExtra("selectedCity")) {
                String selectedCity = data.getExtras().getString("selectedCity");
                if (selectedCity.equals("current")) {
                    getCurrentLocationWeather();
                }
                else {
                    ((TextView) findViewById(R.id.city)).setText(selectedCity);
                    new FetchMetaWeatherTask(this).execute(selectedCity);
                }
            }
        }
    }

    public void onGetConsolidatedWeather() {
        runOnUiThread(() -> {
            try {
                JSONArray consolidatedWeather = new JSONObject(consolidatedWeatherAsString).getJSONArray("consolidated_weather");
                ((ImageView)findViewById(R.id.weatherImage)).setImageBitmap(weatherStatesImages.get(consolidatedWeather.getJSONObject(0).getString("weather_state_abbr")));
                ((TextView)findViewById(R.id.currentTemperature)).setText(consolidatedWeather.getJSONObject(0).getInt("the_temp") + "°C");
                ViewPager2 viewPager = findViewById(R.id.view_pager);
                viewPager.setAdapter(new ScreenSlidePagerAdapter(this));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void startLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        MainActivity mainActivity = this;

        _locationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        String city = getCityFromCurrentLocation(location);
                        ((TextView)findViewById(R.id.city)).setText(city);

                        new FetchMetaWeatherTask(mainActivity).execute(city);
                    }
                }
            }
        }, Looper.getMainLooper());
    }

    private void getCurrentLocationWeather() {
        _locationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                String city = getCityFromCurrentLocation(location);
                ((TextView)findViewById(R.id.city)).setText(city);

                new FetchMetaWeatherTask(this).execute(city);
            }
            else {
                startLocationUpdates();
            }
        });
    }

    private String getCityFromCurrentLocation(Location location) {
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            return addresses.get(0).getLocality();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
