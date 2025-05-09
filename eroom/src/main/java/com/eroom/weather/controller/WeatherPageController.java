package com.eroom.weather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherPageController {

	@GetMapping("/weather")
    public String weatherPage() {
        return "weather/weather"; // templates/weather.html 을 반환함
    }
}
