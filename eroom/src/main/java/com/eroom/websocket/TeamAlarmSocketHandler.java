package com.eroom.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TeamAlarmSocketHandler extends TextWebSocketHandler {

    //  팀코드별로 세션을 저장
    private final Map<String, Set<WebSocketSession>> teamSessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 팀 코드 추출 
        String teamCode = getTeamCode(session);
        if (teamCode != null) {
            teamSessionMap.computeIfAbsent(teamCode, k -> ConcurrentHashMap.newKeySet()).add(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        teamSessionMap.values().forEach(set -> set.remove(session));
    }

    public void broadcastToTeam(String teamCode, String message) {
        Set<WebSocketSession> sessions = teamSessionMap.get(teamCode);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getTeamCode(WebSocketSession session) {
        return Optional.ofNullable(session.getUri())
                       .map(uri -> UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("team"))
                       .orElse(null);
    }
}

