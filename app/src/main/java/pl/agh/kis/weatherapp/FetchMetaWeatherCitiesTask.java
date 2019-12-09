package pl.agh.kis.weatherapp;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FetchMetaWeatherCitiesTask extends AsyncTask<String, Void, List<String>> {
    private AddNewCityActivity _addNewCityActivity;

    public FetchMetaWeatherCitiesTask(AddNewCityActivity addNewCityActivity) { _addNewCityActivity = addNewCityActivity; }

    protected List<String> doInBackground(String... strings) {
        String query = strings[0];
        List<String> foundCities = new ArrayList<>();

        try {
            String url = String.format("%s%s%s", MetaWeather.URL, MetaWeather.QueryEndpoint, URLEncoder.encode(query, "UTF-8"));
            String metaWeatherResponseJsonAsString = MetaWeatherFetchHelper.getMetaWeatherResponseAsString(url);

            if (metaWeatherResponseJsonAsString == null)
                return null;

            JSONArray cities = new JSONArray(metaWeatherResponseJsonAsString);
            if (cities.length() == 0)
                return null;

            for(int i = 0; i < cities.length(); ++i)
                foundCities.add(cities.getJSONObject(i).getString("title"));

            return foundCities;
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<String> foundCities) {
        ((ListView)_addNewCityActivity.findViewById(R.id.foundCities)).setAdapter(new ArrayAdapter<>(_addNewCityActivity, R.layout.new_city_row, foundCities));
    }
}
