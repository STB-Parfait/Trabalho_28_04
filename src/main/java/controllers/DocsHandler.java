package controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DocsHandler implements HttpHandler {
    private final String contentType;
    private final String resourcePath;
    private final String inlineContent;

    private DocsHandler(String contentType, String resourcePath, String inlineContent) {
        this.contentType = contentType;
        this.resourcePath = resourcePath;
        this.inlineContent = inlineContent;
    }

    public static DocsHandler openapi() {
        return new DocsHandler("application/json; charset=utf-8", "/openapi.json", null);
    }

    public static DocsHandler swagger() {
        return new DocsHandler("text/html; charset=utf-8", null, swaggerHtml());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        byte[] body = inlineContent != null
                ? inlineContent.getBytes(StandardCharsets.UTF_8)
                : readResource(resourcePath);

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        } finally {
            exchange.close();
        }
    }

    private byte[] readResource(String path) throws IOException {
        try (InputStream inputStream = DocsHandler.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                return "{\"error\":\"OpenAPI resource not found\"}".getBytes(StandardCharsets.UTF_8);
            }
            return inputStream.readAllBytes();
        }
    }

    private static String swaggerHtml() {
        return """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8">
                    <title>Agenda API Swagger</title>
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css">
                    <style>
                      body { margin: 0; background: #f7f7f7; }
                      .topbar { display: none; }
                    </style>
                  </head>
                  <body>
                    <div id="swagger-ui"></div>
                    <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
                    <script>
                      window.ui = SwaggerUIBundle({
                        url: '/openapi.json',
                        dom_id: '#swagger-ui',
                        deepLinking: true,
                        presets: [SwaggerUIBundle.presets.apis],
                        layout: 'BaseLayout'
                      });
                    </script>
                  </body>
                </html>
                """;
    }
}
