package com.eroom.websocket;

import java.net.URI;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        URI uri = request.getURI();
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        if (uri.getQuery() != null) {
            String[] params = uri.getQuery().split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "senderNo".equals(keyValue[0])) {
                    attributes.put("senderNo", keyValue[1]); // 세션에 저장
                }
            }
            // 현재 연결된 사람의 employeeNo. 로그인한 사람의 사번
            String employeeNoStr = servletRequest.getParameter("employeeNo");
            if (employeeNoStr != null) {
                attributes.put("employeeNo", Long.valueOf(employeeNoStr));
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
    }
}
