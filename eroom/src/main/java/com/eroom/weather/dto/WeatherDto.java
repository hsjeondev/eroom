package com.eroom.weather.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // 추가된 부분
public class WeatherDto {
    private Coord coord;             // 좌표 정보 (경도, 위도)
    private String base;             // OpenWeatherMap에서 제공하는 base 정보 (예: "stations")
    private String name;             // 도시 이름
    private String cod;              // 응답 코드 (예: "200")
    private Main main;               // 기온, 습도 등
    private List<Weather> weather;   // 날씨 상태 (맑음, 흐림 등)
    private Wind wind;               // 바람 정보
    private Rain rain;               // 강수량 정보
    private Clouds clouds;           // 구름 정보
    private Sys sys;                 // 시스템 정보 (예: 일출/일몰 시간)
    private int visibility;          // 가시거리 (m 단위)
    private long dt;                 // 데이터가 수집된 시간 (Unix timestamp)
    private int timezone;            // 시간대 정보 (예: "Asia/Seoul")

    @Data
    public static class Main {
        private double temp;          // 현재 기온
        private double feels_like;    // 체감 온도
        private double temp_min;      // 최저 기온
        private double temp_max;      // 최고 기온
        private int pressure;         // 대기압
        private int humidity;         // 습도 (%)
        private int sea_level;        // 해수면 기압 (단위: hPa)
        private int grnd_level;       // 지면 기압 (단위: hPa)
    }

    @Data
    public static class Weather {
        private int id;               // 날씨 상태 ID
        private String main;          // 날씨 종류 (Rain, Clear 등)
        private String description;   // 상세 설명 (보통 비, 흐림 등)
        private String icon;          // 날씨 아이콘 코드
    }

    @Data
    public static class Wind {
        private double speed;         // 바람 속도 (m/s)
        private int deg;              // 바람 방향 (도)
        private Double gust;  // gust는 필수 값이 아닐 수 있으므로 `Double`로 선언
    }

    @Data
    public static class Rain {
        @JsonProperty("1h")
        private double oneHour;       // 1시간 강수량 (mm)
    }

    @Data
    public static class Clouds {
        private int all;              // 구름 비율 (%)
    }

    @Data
    public static class Sys {
        private long type;            // 시스템 정보 타입
        private long id;              // 시스템 ID
        private String country;       // 국가 코드
        private long sunrise;         // 일출 시간 (epoch time)
        private long sunset;          // 일몰 시간 (epoch time)
    }

    @Data
    public static class Coord {
        private double lon;           // 경도
        private double lat;           // 위도
    }
}
