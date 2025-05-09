package com.eroom.weather.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eroom.weather.dto.WeatherDto;
import com.eroom.weather.service.WeatherService;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // 위도, 경도로 날씨 정보 가져오기
    @GetMapping("/coord")
    public ResponseEntity<WeatherDto> getWeatherByCoord(
            @RequestParam(name = "lat") double latitude,
            @RequestParam(name = "lon") double longitude) {
        WeatherDto weather = weatherService.getWeatherByCoord(latitude, longitude);
        return ResponseEntity.ok(weather);
    }

    // 도시 이름으로 날씨 정보 가져오기
    @GetMapping
    public ResponseEntity<String> getWeather(@RequestParam(name = "city") String city) {
        String weatherData = weatherService.getWeatherByCity(city);
        return ResponseEntity.ok(weatherData);
    }
}
