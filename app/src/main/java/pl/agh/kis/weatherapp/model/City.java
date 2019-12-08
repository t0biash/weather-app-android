package pl.agh.kis.weatherapp.model;

import com.orm.SugarRecord;

public class City extends SugarRecord<City> {
    public String name;

    public City() { }

    public City(String name) {
        this.name = name;
    }
}
