package pl.agh.kis.weatherapp;

import android.os.AsyncTask;
import android.util.Log;
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
}

class MetaWeather {
    public static final String URL = "https://www.metaweather.com";
    public static final String QueryEndpoint = "/api/location/search/?query=";
    public static final String LocationEndpoint = "/api/location/";
}
