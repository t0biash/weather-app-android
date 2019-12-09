package pl.agh.kis.weatherapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherSlidePageFragment extends Fragment {
    private MainActivity _mainActivity;
    private int _position;

    public WeatherSlidePageFragment() { }

    public WeatherSlidePageFragment(int position) {
        _position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _mainActivity = (MainActivity)getActivity();

        return (ViewGroup) inflater.inflate(R.layout.fragment_weather_slide_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setFragmentData(getView(), _position);
    }

    private void setFragmentData(View view, int fragmentNumber) {
        try {
            JSONArray consolidatedWeather = new JSONObject(_mainActivity.consolidatedWeatherAsString).getJSONArray("consolidated_weather");

            ((ImageView)view.findViewById(R.id.fragmentWeatherImage)).setImageBitmap(_mainActivity.weatherStatesImages.get(consolidatedWeather.getJSONObject(fragmentNumber).getString("weather_state_abbr")));
            ((TextView)view.findViewById(R.id.fragmentWeatherDate)).setText(consolidatedWeather.getJSONObject(fragmentNumber).getString("applicable_date"));
            ((TextView)view.findViewById(R.id.fragmentWeatherMinTemp)).setText(String.format("%s %s%s", "Min temp:", consolidatedWeather.getJSONObject(fragmentNumber).getInt("min_temp"), "°C"));
            ((TextView)view.findViewById(R.id.fragmentWeatherMaxTemp)).setText(String.format("%s %s%s", "Max temp:", consolidatedWeather.getJSONObject(fragmentNumber).getInt("max_temp"), "°C"));
            ((TextView)view.findViewById(R.id.fragmentWeatherAirPressure)).setText(String.format("%s %s%s", "Air pressure:", consolidatedWeather.getJSONObject(fragmentNumber).getInt("air_pressure"), "hPa"));
            ((TextView)view.findViewById(R.id.fragmentWeatherHumidity)).setText(String.format("%s %s%s", "Humidity:", consolidatedWeather.getJSONObject(fragmentNumber).getInt("humidity"), " %"));
            ((TextView)view.findViewById(R.id.fragmentWeatherWind)).setText(String.format("%s %s %s", "Wind:", consolidatedWeather.getJSONObject(fragmentNumber).getInt("wind_speed"), consolidatedWeather.getJSONObject(fragmentNumber).getString("wind_direction_compass")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}