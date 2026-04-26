package tests;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class ApiSmokeTest {
    private static final String BASE_URL = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8000");

    public static void main(String[] args) throws Exception {
        post("/contacts", """
                {
                  "name": "Maria Silva",
                  "email": "maria@example.com",
                  "phone": "(11) 99999-0000"
                }
                """);
        get("/contacts");
        get("/reports/agenda-summary");
    }

    private static void get(String path) throws IOException {
        HttpURLConnection connection = open(path, "GET");
        printResponse(connection);
    }

    private static void post(String path, String json) throws IOException {
        HttpURLConnection connection = open(path, "POST");
        connection.setDoOutput(true);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        }
        printResponse(connection);
    }

    private static HttpURLConnection open(String path, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(BASE_URL + path).toURL().openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private static void printResponse(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        byte[] body = status >= 400
                ? connection.getErrorStream().readAllBytes()
                : connection.getInputStream().readAllBytes();

        System.out.println("HTTP " + status);
        System.out.println(new String(body, StandardCharsets.UTF_8));
    }
}
