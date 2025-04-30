package com.eroom.project.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.eroom.project.dto.QualityMeasureDto;

@Service
public class SonarService {

    @Value("${sonar.component-key}")
    private String componentKey;

    @Value("${sonar.token}")
    private String sonarToken;

    public List<QualityMeasureDto> getProjectQualityMeasures() {
        String metrics = String.join(",", List.of(
            "bugs", "vulnerabilities", "code_smells", "coverage", "duplicated_lines_density"
        ));

        String url = String.format(
            "https://sonarcloud.io/api/measures/component?component=%s&metricKeys=%s",
            componentKey, metrics
        );

        return fetchAndParseMeasures(url);
    }

    public List<QualityMeasureDto> getPrQualityMeasures(String prNumber) {
        String metrics = String.join(",", List.of(
            "bugs", "vulnerabilities", "code_smells", "coverage", "duplicated_lines_density"
        ));

        String url = String.format(
            "https://sonarcloud.io/api/measures/component?component=%s&pullRequest=%s&metricKeys=%s",
            componentKey, prNumber, metrics
        );

        return fetchAndParseMeasures(url);
    }

    private List<QualityMeasureDto> fetchAndParseMeasures(String apiUrl) {
        List<QualityMeasureDto> result = new ArrayList<>();

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");

            String encodedAuth = Base64.getEncoder().encodeToString((sonarToken + ":").getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            if (conn.getResponseCode() != 200) return result;

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JSONObject json = new JSONObject(response.toString());
            JSONArray measures = json.getJSONObject("component").getJSONArray("measures");

            for (int i = 0; i < measures.length(); i++) {
                JSONObject obj = measures.getJSONObject(i);
                result.add(new QualityMeasureDto(obj.getString("metric"), obj.getString("value")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
