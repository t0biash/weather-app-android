package pl.agh.kis.weatherapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;


public class FetchMetaWeatherTask extends AsyncTask<String, Void, String> {
    private MainActivity _mainActivity;

    public FetchMetaWeatherTask(MainActivity mainActivity) {
        _mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String city = strings[0];

        try {
            String url = String.format("%s%s%s", MetaWeather.URL ,MetaWeather.QueryEndpoint, URLEncoder.encode(city, "UTF-8"));
            String metaWeatherResponseJsonAsString = MetaWeatherFetchHelper.getMetaWeatherResponseAsString(url);
            if(metaWeatherResponseJsonAsString == null)
                return null;
            JSONArray locationSearchResponse = new JSONArray(metaWeatherResponseJsonAsString);
            if(locationSearchResponse.length() == 0)
                return null;
            String firstFoundCityLocationId = locationSearchResponse.getJSONObject(0).getString("woeid");

            url = String.format("%s%s%s", MetaWeather.URL, MetaWeather.LocationEndpoint, firstFoundCityLocationId);
            metaWeatherResponseJsonAsString = MetaWeatherFetchHelper.getMetaWeatherResponseAsString(url);
            if(metaWeatherResponseJsonAsString == null)
                return null;

            _mainActivity.weatherStatesImages = new Hashtable<>();
            for(String weatherState : MetaWeather.WeatherStates) {
                url = String.format("%s%s%s.png", MetaWeather.URL, MetaWeather.WeatherStateImageEndpoint, weatherState);
                _mainActivity.weatherStatesImages.put(weatherState, MetaWeatherFetchHelper.getMetaWeatherStateImage(url));
            }

            return metaWeatherResponseJsonAsString;
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
    protected void onPostExecute(String result) {
        _mainActivity.consolidatedWeatherAsString = result;
        _mainActivity.onGetConsolidatedWeather();
    }
}
