package com.erdemburak.weather.service;

import com.erdemburak.weather.dto.WeatherDto;
import com.erdemburak.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public WeatherDto getWeatherByCityName(String city){

    }
}
