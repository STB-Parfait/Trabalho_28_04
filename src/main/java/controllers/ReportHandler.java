package controllers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import services.ReportService;

public class ReportHandler extends BaseHandler {
    private final ReportService reportService;

    public ReportHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        if (!"GET".equals(method(exchange))) {
            throw new NotFoundException("Route not found.");
        }
        sendJson(exchange, 200, reportService.buildSummary());
    }
}
