package pl.agh.kis.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


public class FetchHelper extends AsyncTask<String, Void, String> {
    private MainActivity _mainActivity;
    private Bitmap _weatherImages;

    public FetchHelper(MainActivity mainActivity) {
        _mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String city = strings[0];

        try {
            String url = String.format("%s%s%s", MetaWeather.URL ,MetaWeather.QueryEndpoint, URLEncoder.encode(city, "UTF-8"));
            String metaWeatherResponseJsonAsString = getMetaWeatherResponseAsString(url);
            if(metaWeatherResponseJsonAsString == null)
                return null;
            JSONArray locationSearchResponse = new JSONArray(metaWeatherResponseJsonAsString);
            if(locationSearchResponse.length() == 0)
                return null;
            String firstFoundCityLocationId = locationSearchResponse.getJSONObject(0).getString("woeid");

            url = String.format("%s%s%s", MetaWeather.URL, MetaWeather.LocationEndpoint, firstFoundCityLocationId);
            metaWeatherResponseJsonAsString = getMetaWeatherResponseAsString(url);
            if(metaWeatherResponseJsonAsString == null)
                return null;

            String currentWeatherStateAbbr = new JSONObject(metaWeatherResponseJsonAsString)
                    .getJSONArray("consolidated_weather")
                    .getJSONObject(0)
                    .getString("weather_state_abbr");
            url = String.format("%s%s%s.png", MetaWeather.URL, MetaWeather.WeatherStateImageEndpoint, currentWeatherStateAbbr);
            _weatherImages = getMetaWeatherStateImage(url);

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
        ((ImageView)_mainActivity.findViewById(R.id.weatherImage)).setImageBitmap(_weatherImages);
        ((ImageView)_mainActivity.findViewById(R.id.fragmentWeatherImage)).setImageBitmap((_weatherImages));
        _mainActivity.onGetConsolidatedWeather();
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMetaWeatherResponseAsString(String requestUrl) {
        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        trustEveryone();
        
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null)
                return null;

            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line + "\n");
            if (buffer.length() == 0)
                return null;

            return buffer.toString();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final IOException e) {
                    Log.e("FetchHelper", "Error closing stream", e);
                }
            }
        }

        return null;
    }

    private Bitmap getMetaWeatherStateImage(String requestUrl) {
        Bitmap mIcon11 = null;

        try {
            InputStream in = new java.net.URL(requestUrl).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return mIcon11;
    }
}

class MetaWeather {
    public static final String URL = "https://www.metaweather.com";
    public static final String QueryEndpoint = "/api/location/search/?query=";
    public static final String LocationEndpoint = "/api/location/";
    public static final String WeatherStateImageEndpoint = "/static/img/weather/png/64/";
}
