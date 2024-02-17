package com.erdemburak.weather.service;

import com.erdemburak.weather.dto.WeatherDto;
import com.erdemburak.weather.dto.WeatherResponse;
import com.erdemburak.weather.model.WeatherEntity;
import com.erdemburak.weather.repository.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class WeatherService {

    //https://api.weatherstack.com/current?access_key=63ff7d724dc5d69a0c49851505b8984c&query=london
    private static final String API_URL = "https://api.weatherstack.com/current?access_key=63ff7d724dc5d69a0c49851505b8984c&query=";

    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;
    // ObjectMapper -> gelen json verisini objeye dönüştürmek için kullanıyoruz.
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherService(WeatherRepository weatherRepository, RestTemplate restTemplate) {
        this.weatherRepository = weatherRepository;
        this.restTemplate = restTemplate;
    }

    public WeatherDto getWeatherByCityName(String city){
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository
                .findFirstByRequestedCityNameOrderByUpdatedTimeDesc(city);

        return weatherEntityOptional.map(weather -> {
                if(weather.getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))){
                    return WeatherDto.convert(getWeatherFromWeatherStack(city));
                }
                return WeatherDto.convert(weather);
            }).orElseGet(() -> WeatherDto.convert(getWeatherFromWeatherStack(city)));
    }

    private WeatherEntity getWeatherFromWeatherStack(String city) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity((API_URL + city) , String.class);

        try {
            WeatherResponse weatherResponse = objectMapper.readValue(responseEntity.getBody(), WeatherResponse.class);
            return saveWeatherEntity(city, weatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private WeatherEntity saveWeatherEntity(String city, WeatherResponse weatherResponse){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        WeatherEntity weatherEntity = new WeatherEntity(city,
                weatherResponse.location().name(),
                weatherResponse.location().country(),
                weatherResponse.current().temperature(),
                LocalDateTime.now(),
                LocalDateTime.parse(weatherResponse.location().localtime(), dateTimeFormatter));

        return weatherRepository.save(weatherEntity);

    }
}
