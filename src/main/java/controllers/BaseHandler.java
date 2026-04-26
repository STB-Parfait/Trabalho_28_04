package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import config.JsonConfig;
import exceptions.AppException;
import exceptions.ValidationException;
import models.ApiError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public abstract class BaseHandler implements HttpHandler {
    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            handleRequest(exchange);
        } catch (JsonMappingException exception) {
            sendJson(exchange, 400, new ApiError(400, "Invalid JSON body."));
        } catch (AppException exception) {
            sendJson(exchange, exception.getStatusCode(), new ApiError(exception.getStatusCode(), exception.getMessage()));
        } catch (Exception exception) {
            sendJson(exchange, 500, new ApiError(500, "Unexpected server error."));
            exception.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    protected abstract void handleRequest(HttpExchange exchange) throws Exception;

    protected <T> T readJson(HttpExchange exchange, Class<T> type) throws IOException {
        try (InputStream body = exchange.getRequestBody()) {
            if (body == null) {
                throw new ValidationException("Request body is required.");
            }
            return JsonConfig.mapper().readValue(body, type);
        } catch (JsonProcessingException exception) {
            throw exception;
        }
    }

    protected void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] response = JsonConfig.mapper().writeValueAsBytes(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }

    protected void sendNoContent(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(204, -1);
    }

    protected String[] pathParts(HttpExchange exchange, String basePath) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        if (!path.startsWith(basePath)) {
            return new String[0];
        }
        String relative = path.substring(basePath.length());
        if (relative.isBlank() || "/".equals(relative)) {
            return new String[0];
        }
        if (relative.startsWith("/")) {
            relative = relative.substring(1);
        }
        return relative.split("/");
    }

    protected long parseId(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new ValidationException("Invalid id.");
        }
    }

    protected String method(HttpExchange exchange) {
        return exchange.getRequestMethod().toUpperCase();
    }

    protected String requestBodyAsText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
}
