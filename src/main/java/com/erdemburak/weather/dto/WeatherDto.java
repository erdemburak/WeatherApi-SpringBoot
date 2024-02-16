package com.erdemburak.weather.dto;

import com.erdemburak.weather.model.WeatherEntity;

public record WeatherDto (
        String cityName,
        String country,
        Integer temperature
) {

    public static WeatherDto convert(WeatherEntity from){
        return new WeatherDto(from.getCityName(),from.getCountry(),from.getTemperature());
    }

}
