package com.sales.interceptors;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class SalesHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request,
                                   @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler,
                                   @NotNull Map<String, Object> attributes) {
        // Extract username from request (e.g., from a custom header)
        String username = extractUsernameFromRequest(request);

        // Store username in session attributes
        if (username != null) {
            attributes.put("username", username);
        }

        return true; // Proceed with the handshake
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request,
                               @NotNull ServerHttpResponse response,
                               @NotNull WebSocketHandler wsHandler,
                               Exception exception) {
        // Optional: Perform actions after the handshake is complete
    }

    private String extractUsernameFromRequest(ServerHttpRequest request) {
        // Implement your logic here to extract username from the request
        // For example, from a custom header:
        return request.getHeaders().getFirst("X-Username");
    }
}
