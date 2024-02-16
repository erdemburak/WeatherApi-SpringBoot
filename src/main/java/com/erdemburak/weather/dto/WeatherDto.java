package com.erdemburak.weather.dto;

public record WeatherDto (
        String cityName,
        String country,
        Integer temperature
) {
}
