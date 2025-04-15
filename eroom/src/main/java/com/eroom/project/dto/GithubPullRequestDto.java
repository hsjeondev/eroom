package com.eroom.project.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubPullRequestDto {

    @JsonProperty("html_url")
    private String htmlUrl;

    private int number;
    private String state;
    private String title;
    private String body;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("closed_at")
    private String closedAt;

    @JsonProperty("merged_at")
    private String mergedAt;

    // 중첩된 user.login에서 추출
    private String author;

    // 중첩된 base.ref에서 추출
    private String baseBranch;

    // 중첩된 head.ref에서 추출
    private String headBranch;

    // JSON 중첩 필드에서 납작한 필드 추출하는 custom setter

    @JsonProperty("user")
    private void unpackUser(Map<String, Object> user) {
        this.author = (String) user.get("login");
    }

    @JsonProperty("base")
    private void unpackBase(Map<String, Object> base) {
        this.baseBranch = (String) base.get("ref");
    }

    @JsonProperty("head")
    private void unpackHead(Map<String, Object> head) {
        this.headBranch = (String) head.get("ref");
    }
}
