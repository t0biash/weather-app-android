package pl.agh.kis.weatherapp;

public final class MetaWeather {
    public static final String URL = "https://www.metaweather.com";
    public static final String QueryEndpoint = "/api/location/search/?query=";
    public static final String LocationEndpoint = "/api/location/";
    public static final String WeatherStateImageEndpoint = "/static/img/weather/png/";
    public static final String[] WeatherStates = { "sn", "sl", "h", "t", "hr", "lr", "s", "hc", "lc", "c" };
}
